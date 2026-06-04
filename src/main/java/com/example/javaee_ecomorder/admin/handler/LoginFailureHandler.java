package com.example.javaee_ecomorder.admin.handler;

import com.example.javaee_ecomorder.common.basic.CacheKeyPrefix;
import com.example.javaee_ecomorder.common.basic.StatusCode;
import com.example.javaee_ecomorder.common.entity.LoginLog;
import com.example.javaee_ecomorder.common.entity.User;
import com.example.javaee_ecomorder.common.mapper.LoginLogMapper;
import com.example.javaee_ecomorder.common.mapper.UserMapper;
import com.example.javaee_ecomorder.common.utils.IpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Value("${security.login-failure-max-attempts:5}")
    private int maxAttempts;

    @Value("${security.login-failure-lock-duration:180}")
    private long lockDurationSeconds;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LoginLogMapper loginLogMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String username = request.getParameter("username");
        if (username == null) {
            username = "";
        }
        User user = userMapper.selectByUsername(username);
        if (user != null) {
            int failCount = user.getFailCount() == null ? 0 : user.getFailCount();
            failCount++;
            boolean locked = failCount >= maxAttempts;
            userMapper.updateAccountStatus(user.getId(), !locked, failCount);
            if (locked) {
                stringRedisTemplate.opsForValue().set(
                        CacheKeyPrefix.LOCK + username, "1", lockDurationSeconds, TimeUnit.SECONDS);
            }
            saveLog(user.getId(), username, request, 0);
        } else {
            saveLog(null, username, request, 0);
        }
        Map<String, Object> body = new HashMap<>();
        body.put("code", StatusCode.ERROR);
        body.put("msg", "用户名或密码错误");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }

    private void saveLog(Long userId, String username, HttpServletRequest request, int status) {
        LoginLog log = new LoginLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setLoginTime(new Date());
        log.setIp(IpUtil.getIpAddr(request));
        log.setStatus(status);
        loginLogMapper.insert(log);
    }
}
