package com.example.javaee_ecomorder.biz.aop;

import com.example.javaee_ecomorder.common.annotation.RequireLogin;
import com.example.javaee_ecomorder.common.annotation.RequirePermission;
import com.example.javaee_ecomorder.common.context.UserContext;
import com.example.javaee_ecomorder.common.context.UserInfo;
import com.example.javaee_ecomorder.common.exception.PermissionDeniedException;
import com.example.javaee_ecomorder.common.exception.UnauthorizedException;
import com.example.javaee_ecomorder.common.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.List;

@Aspect
@Component
@Order(1)
public class BizPermissionAspect {

    @Autowired
    private JwtUtil jwtUtil;

    @Around("@annotation(com.example.javaee_ecomorder.common.annotation.RequireLogin) "
            + "|| @annotation(com.example.javaee_ecomorder.common.annotation.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // 获取当前请求
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) {
                throw new UnauthorizedException("未登录，请先登录");
            }

            HttpServletRequest request = attrs.getRequest();
            String token = request.getHeader("Authorization");

            if (token == null || !token.startsWith("Bearer ")) {
                throw new UnauthorizedException("未登录，请先登录");
            }

            String jwt = token.substring(7);

            // 验证 token
            if (!jwtUtil.validateToken(jwt)) {
                throw new UnauthorizedException("登录已过期，请重新登录");
            }

            // 获取权限
            List<String> authorities = jwtUtil.getAuthoritiesFromToken(jwt);
            String username = jwtUtil.getUsernameFromToken(jwt);

            // 检查 @RequirePermission
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);

            if (requirePermission != null) {
                validatePermissions(authorities, requirePermission.value());
            }

            // 设置用户上下文
            UserInfo userInfo = new UserInfo();
            userInfo.setUsername(username);
            userInfo.setPermissions(authorities.stream().filter(a -> a.contains(":")).toList());
            UserContext.setUser(userInfo);
            request.setAttribute("currentUser", userInfo);

            return joinPoint.proceed();

        } finally {
            UserContext.clear();
        }
    }

    private void validatePermissions(List<String> owned, String[] required) {
        if (owned == null || owned.isEmpty()) {
            throw new PermissionDeniedException("无操作权限");
        }
        for (String perm : required) {
            if (!owned.contains(perm)) {
                throw new PermissionDeniedException("缺少权限" + perm);
            }
        }
    }
}
