package com.example.javaee_ecomorder.aop;



import com.example.javaee_ecomorder.annotation.RequireLogin;

import com.example.javaee_ecomorder.annotation.RequirePermission;

import com.example.javaee_ecomorder.context.UserContext;

import com.example.javaee_ecomorder.context.UserInfo;

import com.example.javaee_ecomorder.exception.PermissionDeniedException;

import com.example.javaee_ecomorder.exception.UnauthorizedException;

import com.example.javaee_ecomorder.security.SecurityUser;

import org.aspectj.lang.ProceedingJoinPoint;

import org.aspectj.lang.annotation.Around;

import org.aspectj.lang.annotation.Aspect;

import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.core.annotation.Order;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.context.request.RequestContextHolder;

import org.springframework.web.context.request.ServletRequestAttributes;



import java.lang.reflect.Method;

import java.util.List;

import java.util.stream.Collectors;



@Aspect

@Component

@Order(1)

public class PermissionAspect {



    @Around("@annotation(com.example.javaee_ecomorder.annotation.RequireLogin) "

            + "|| @annotation(com.example.javaee_ecomorder.annotation.RequirePermission)")

    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {

        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()

                    || !(authentication.getPrincipal() instanceof SecurityUser securityUser)) {

                throw new UnauthorizedException("未登录，请先登录");

            }

            List<String> authorities = authentication.getAuthorities().stream()

                    .map(GrantedAuthority::getAuthority)

                    .collect(Collectors.toList());

            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

            RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);

            if (requirePermission != null) {

                validatePermissions(authorities, requirePermission.value());

            }

            UserInfo userInfo = new UserInfo();

            userInfo.setUserId(securityUser.getUserId());

            userInfo.setUsername(securityUser.getUsername());

            userInfo.setPermissions(authorities.stream().filter(a -> a.contains(":")).toList());

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



    private void validatePermissions(List<String> owned, String[] required) {

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


