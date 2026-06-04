package com.example.javaee_ecomorder.admin.handler;

import com.example.javaee_ecomorder.common.basic.CacheKeyPrefix;
import com.example.javaee_ecomorder.common.basic.StatusCode;
import com.example.javaee_ecomorder.common.context.UserInfo;
import com.example.javaee_ecomorder.admin.security.SecurityUser;
import com.example.javaee_ecomorder.common.utils.JwtUtil;
import com.example.javaee_ecomorder.common.utils.RedisCacheUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        List<String> authorities = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());

        String token = jwtUtil.generateToken(user.getUsername(), authorities);

        String role = authorities.stream()
                .filter(a -> a.startsWith("ROLE_"))
                .findFirst()
                .orElse("ROLE_USER");

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setUsername(user.getUsername());
        userInfo.setPermissions(authorities.stream().filter(a -> a.contains(":")).toList());
        redisCacheUtil.set(CacheKeyPrefix.TOKEN + token, userInfo,
                jwtUtil.getExpiration(), TimeUnit.MILLISECONDS);
        redisCacheUtil.set(CacheKeyPrefix.TOKEN_USER + user.getUserId(), token,
                jwtUtil.getExpiration(), TimeUnit.MILLISECONDS);

        Map<String, Object> body = new HashMap<>();
        body.put("code", StatusCode.SUCCESS);
        body.put("msg", "success");
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getUserId());
        data.put("username", user.getUsername());
        data.put("token", token);
        data.put("expireTime", System.currentTimeMillis() + jwtUtil.getExpiration());
        data.put("role", role);
        data.put("permissions", userInfo.getPermissions());
        body.put("data", data);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}