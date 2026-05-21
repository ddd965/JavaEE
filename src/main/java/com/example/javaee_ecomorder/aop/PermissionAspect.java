package com.example.javaee_ecomorder.aop;

import com.example.javaee_ecomorder.annotation.RequireLogin;
import com.example.javaee_ecomorder.annotation.RequirePermission;
import com.example.javaee_ecomorder.common.CacheKeyPrefix;
import com.example.javaee_ecomorder.config.EcomAopProperties;
import com.example.javaee_ecomorder.context.UserContext;
import com.example.javaee_ecomorder.context.UserInfo;
import com.example.javaee_ecomorder.exception.PermissionDeniedException;
import com.example.javaee_ecomorder.exception.UnauthorizedException;
import com.example.javaee_ecomorder.utils.RedisCacheUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Aspect
@Component
@Order(1)
public class PermissionAspect {

    @Autowired
    private RedisCacheUtil redisCacheUtil;
    @Autowired
    private EcomAopProperties aopProperties;

    @Around("@annotation(com.example.javaee_ecomorder.annotation.RequireLogin) "
            + "|| @annotation(com.example.javaee_ecomorder.annotation.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            String token = resolveToken();
            if (!StringUtils.hasText(token)) {
                throw new UnauthorizedException("未登录，请先登录");
            }
            Object cached = redisCacheUtil.get(CacheKeyPrefix.TOKEN + token);
            if (cached == null) {
                throw new UnauthorizedException("登录已过期，请重新登录");
            }
            UserInfo userInfo = toUserInfo(cached);
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);
            if (requirePermission != null) {
                validatePermissions(userInfo, requirePermission.value());
            }
            UserContext.setUser(userInfo);
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                attrs.getRequest().setAttribute("currentUser", userInfo);
                attrs.getRequest().setAttribute("userId", userInfo.getUserId());
            }
            return joinPoint.proceed();
        } finally {
            UserContext.clear();
        }
    }

    private String resolveToken() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        HttpServletRequest request = attrs.getRequest();
        String token = request.getHeader(aopProperties.getTokenHeader());
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return token;
    }

    @SuppressWarnings("unchecked")
    private UserInfo toUserInfo(Object cached) {
        UserInfo info = new UserInfo();
        if (cached instanceof UserInfo userInfo) {
            return userInfo;
        }
        if (cached instanceof Map<?, ?> map) {
            Object userId = map.get("userId");
            if (userId == null) {
                userId = map.get("id");
            }
            if (userId instanceof Number number) {
                info.setUserId(number.longValue());
            }
            Object username = map.get("username");
            if (username != null) {
                info.setUsername(username.toString());
            }
            Object permissions = map.get("permissions");
            if (permissions instanceof List<?> list) {
                info.setPermissions(list.stream().map(Object::toString).toList());
            }
            return info;
        }
        if (cached instanceof Number number) {
            info.setUserId(number.longValue());
            return info;
        }
        throw new UnauthorizedException("登录信息无效");
    }

    private void validatePermissions(UserInfo userInfo, String[] required) {
        List<String> owned = userInfo.getPermissions();
        if (owned == null || owned.isEmpty()) {
            throw new PermissionDeniedException("无操作权限");
        }
        for (String perm : required) {
            if (!owned.contains(perm)) {
                throw new PermissionDeniedException("缺少权限：" + perm);
            }
        }
    }
}
