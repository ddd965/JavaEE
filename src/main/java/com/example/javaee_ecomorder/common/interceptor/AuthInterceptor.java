package com.example.javaee_ecomorder.common.interceptor;

import com.example.javaee_ecomorder.common.basic.CacheKeyPrefix;
import com.example.javaee_ecomorder.common.config.EcomAopProperties;
import com.example.javaee_ecomorder.common.context.UserInfo;
import com.example.javaee_ecomorder.common.exception.UnauthorizedException;
import com.example.javaee_ecomorder.common.utils.RedisCacheUtil;
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
    @Autowired
    private EcomAopProperties aopProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String uri = request.getRequestURI();
        if (uri.endsWith("/auth/login") || uri.endsWith("/users/register")) {
            return true;
        }
        String token = request.getHeader(aopProperties.getTokenHeader());
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (!StringUtils.hasText(token)) {
            throw new UnauthorizedException("未登录，请先登录");
        }
        Object cached = redisCacheUtil.get(CacheKeyPrefix.TOKEN + token);
        if (cached == null) {
            throw new UnauthorizedException("登录已过期，请重新登�?);
        }
        Long userId = extractUserId(cached);
        request.setAttribute("userId", userId);
        request.setAttribute("currentUser", cached);
        return true;
    }

    private Long extractUserId(Object cached) {
        if (cached instanceof UserInfo userInfo) {
            return userInfo.getUserId();
        }
        if (cached instanceof Number number) {
            return number.longValue();
        }
        if (cached instanceof java.util.Map<?, ?> map) {
            Object userId = map.get("userId");
            if (userId == null) {
                userId = map.get("id");
            }
            if (userId instanceof Number number) {
                return number.longValue();
            }
        }
        throw new UnauthorizedException("登录信息无效");
    }
}
