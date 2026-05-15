package com.example.javaee_ecomorder.interceptor;

import com.example.javaee_ecomorder.common.CacheKeyPrefix;
import com.example.javaee_ecomorder.exception.BusinessException;
import com.example.javaee_ecomorder.utils.RedisCacheUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String uri = request.getRequestURI();
        if (uri.endsWith("/auth/login") || uri.endsWith("/users/register")) {
            return true;
        }
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (!StringUtils.hasText(token)) {
            throw new BusinessException("未登录，请先登录");
        }
        if (!redisCacheUtil.hasKey(CacheKeyPrefix.TOKEN + token)) {
            throw new BusinessException("登录已过期，请重新登录");
        }
        request.setAttribute("userId", redisCacheUtil.get(CacheKeyPrefix.TOKEN + token));
        return true;
    }
}
