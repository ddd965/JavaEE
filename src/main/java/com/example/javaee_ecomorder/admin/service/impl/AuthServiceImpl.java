package com.example.javaee_ecomorder.admin.service.impl;

import com.example.javaee_ecomorder.common.basic.CacheKeyPrefix;
import com.example.javaee_ecomorder.common.context.UserInfo;
import com.example.javaee_ecomorder.common.entity.LoginLog;
import com.example.javaee_ecomorder.common.entity.User;
import com.example.javaee_ecomorder.common.exception.BusinessException;
import com.example.javaee_ecomorder.common.mapper.LoginLogMapper;
import com.example.javaee_ecomorder.common.mapper.UserMapper;
import com.example.javaee_ecomorder.common.security.EcomPasswordEncoder;
import com.example.javaee_ecomorder.admin.security.SecurityUser;
import com.example.javaee_ecomorder.admin.service.AccountLockService;
import com.example.javaee_ecomorder.admin.service.AuthService;
import com.example.javaee_ecomorder.common.utils.IpUtil;
import com.example.javaee_ecomorder.common.utils.JwtUtil;
import com.example.javaee_ecomorder.common.utils.RedisCacheUtil;
import com.example.javaee_ecomorder.common.vo.LoginResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisCacheUtil redisCacheUtil;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LoginLogMapper loginLogMapper;
    @Autowired
    private EcomPasswordEncoder passwordEncoder;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private AccountLockService accountLockService;

    @Value("${security.login-failure-max-attempts:5}")
    private int maxAttempts;

    @Value("${security.login-failure-lock-duration:180}")
    private long lockDurationSeconds;

    @Override
    public LoginResultVO login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new BusinessException("用户�?密码不能为空");
        }
        accountLockService.releaseExpiredLockIfNeeded(username);
        if (accountLockService.isLockedInRedis(username)) {
            recordFailedLogin(username);
            throw new BusinessException("账户已锁�? + accountLockService.formatRemainingLockMessage(username));
        }
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
        } catch (LockedException e) {
            recordFailedLogin(username);
            throw new BusinessException("账户已锁�? + accountLockService.formatRemainingLockMessage(username));
        } catch (BadCredentialsException e) {
            handleBadCredentials(username);
            throw new BusinessException("用户名或密码错误");
        }
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        upgradePasswordIfNeeded(user.getUserId(), password, user.getPassword());
        List<String> authorities = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());
        //
        String role = authorities.stream()
                .filter(a -> a.startsWith("ROLE_"))
                .findFirst()
                .orElse("ROLE_USER");
        //

        String token = jwtUtil.generateToken(user.getUsername(), authorities);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setUsername(user.getUsername());
        userInfo.setPermissions(authorities.stream().filter(a -> a.contains(":")).toList());
        redisCacheUtil.set(CacheKeyPrefix.TOKEN + token, userInfo,
                jwtUtil.getExpiration(), TimeUnit.MILLISECONDS);
        redisCacheUtil.set(CacheKeyPrefix.TOKEN_USER + user.getUserId(), token,
                jwtUtil.getExpiration(), TimeUnit.MILLISECONDS);
        userMapper.updateAccountStatus(user.getUserId(), true, 0);
        userMapper.updateLastLoginTime(user.getUserId());
        saveLoginLog(user.getUserId(), username, 1);
        LoginResultVO result = new LoginResultVO();
        result.setUserId(user.getUserId());
        result.setUsername(user.getUsername());
        result.setToken(token);
        result.setExpireTime(System.currentTimeMillis() + jwtUtil.getExpiration());
        //
        result.setRole(role);
        //
        return result;
    }

    @Override
    public void logout(String token) {
        if (!StringUtils.hasText(token)) {
            return;
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        try {
            String username = jwtUtil.getUsernameFromToken(token);
            User user = userMapper.selectByUsername(username);
            if (user != null) {
                redisCacheUtil.delete(CacheKeyPrefix.TOKEN_USER + user.getId());
            }
        } catch (Exception ignored) {
        }
        redisCacheUtil.delete(CacheKeyPrefix.TOKEN + token);
    }

    @Override
    public LoginResultVO refresh(String token) {
        if (!StringUtils.hasText(token)) {
            throw new BusinessException("令牌不能为空");
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException("令牌无效或已过期");
        }
        Object cached = redisCacheUtil.get(CacheKeyPrefix.TOKEN + token);
        if (cached == null) {
            throw new BusinessException("登录已过期，请重新登�?);
        }
        logout(token);
        String username = jwtUtil.getUsernameFromToken(token);
        List<String> authorities = jwtUtil.getAuthoritiesFromToken(token);
        String newToken = jwtUtil.generateToken(username, authorities);

        String role = authorities.stream()
                .filter(a -> a.startsWith("ROLE_"))
                .findFirst()
                .orElse("ROLE_USER");

        redisCacheUtil.set(CacheKeyPrefix.TOKEN + newToken, cached,
                jwtUtil.getExpiration(), TimeUnit.MILLISECONDS);
        LoginResultVO result = new LoginResultVO();
        result.setUsername(username);
        result.setToken(newToken);
        result.setExpireTime(System.currentTimeMillis() + jwtUtil.getExpiration());
        result.setRole(role);

        if (cached instanceof UserInfo info) {
            result.setUserId(info.getUserId());
            redisCacheUtil.set(CacheKeyPrefix.TOKEN_USER + info.getUserId(), newToken,
                    jwtUtil.getExpiration(), TimeUnit.MILLISECONDS);
        }
        return result;
    }

    private void upgradePasswordIfNeeded(Long userId, String rawPassword, String encodedPassword) {
        if (!passwordEncoder.isBcrypt(encodedPassword)) {
            userMapper.updatePassword(userId, passwordEncoder.encode(rawPassword));
        }
    }

    private void handleBadCredentials(String username) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            saveLoginLog(null, username, 0);
            throw new BusinessException("用户名或密码错误");
        }
        int failCount = user.getFailCount() == null ? 0 : user.getFailCount();
        failCount++;
        boolean locked = failCount >= maxAttempts;
        userMapper.updateAccountStatus(user.getId(), !locked, failCount);
        saveLoginLog(user.getId(), username, 0);
        if (locked) {
            stringRedisTemplate.opsForValue().set(
                    CacheKeyPrefix.LOCK + username, "1", lockDurationSeconds, TimeUnit.SECONDS);
            throw new BusinessException("密码错误次数过多，账户已锁定" + accountLockService.formatRemainingLockMessage(username));
        }
        int remaining = maxAttempts - failCount;
        throw new BusinessException("用户名或密码错误，还可尝�?"+ remaining + "�?);
    }

    private void recordFailedLogin(String username) {
        User user = userMapper.selectByUsername(username);
        saveLoginLog(user != null ? user.getId() : null, username, 0);
    }

    private void saveLoginLog(Long userId, String username, int status) {
        LoginLog log = new LoginLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setLoginTime(new Date());
        log.setStatus(status);
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            log.setIp(IpUtil.getIpAddr(attrs.getRequest()));
        }
        loginLogMapper.insert(log);
    }
}