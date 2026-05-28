package com.example.javaee_ecomorder.service.impl;

import com.example.javaee_ecomorder.common.CacheKeyPrefix;
import com.example.javaee_ecomorder.context.UserInfo;
import com.example.javaee_ecomorder.entity.LoginLog;
import com.example.javaee_ecomorder.entity.User;
import com.example.javaee_ecomorder.exception.BusinessException;
import com.example.javaee_ecomorder.mapper.LoginLogMapper;
import com.example.javaee_ecomorder.mapper.UserMapper;
import com.example.javaee_ecomorder.security.EcomPasswordEncoder;
import com.example.javaee_ecomorder.security.SecurityUser;
import com.example.javaee_ecomorder.service.AuthService;
import com.example.javaee_ecomorder.utils.IpUtil;
import com.example.javaee_ecomorder.utils.JwtUtil;
import com.example.javaee_ecomorder.utils.RedisCacheUtil;
import com.example.javaee_ecomorder.vo.LoginResultVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
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
    @Override
    public LoginResultVO login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new BusinessException("用户名/密码不能为空");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        upgradePasswordIfNeeded(user.getUserId(), password, user.getPassword());
        List<String> authorities = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());
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
            throw new BusinessException("登录已过期，请重新登录");
        }
        logout(token);
        String username = jwtUtil.getUsernameFromToken(token);
        List<String> authorities = jwtUtil.getAuthoritiesFromToken(token);
        String newToken = jwtUtil.generateToken(username, authorities);
        redisCacheUtil.set(CacheKeyPrefix.TOKEN + newToken, cached,
                jwtUtil.getExpiration(), TimeUnit.MILLISECONDS);
        LoginResultVO result = new LoginResultVO();
        result.setUsername(username);
        result.setToken(newToken);
        result.setExpireTime(System.currentTimeMillis() + jwtUtil.getExpiration());
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
