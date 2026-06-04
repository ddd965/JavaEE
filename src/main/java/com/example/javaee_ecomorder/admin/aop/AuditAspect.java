package com.example.javaee_ecomorder.admin.aop;

import com.example.javaee_ecomorder.admin.annotation.OperateLog;
import com.example.javaee_ecomorder.common.config.EcomAopProperties;
import com.example.javaee_ecomorder.common.context.UserContext;
import com.example.javaee_ecomorder.common.context.UserInfo;
import com.example.javaee_ecomorder.common.entity.AuditLog;
import com.example.javaee_ecomorder.common.mapper.AuditLogMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;

@Aspect
@Component
@Order(5)
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AuditLogMapper auditLogMapper;
    @Autowired
    private EcomAopProperties aopProperties;

    @AfterReturning(pointcut = "@annotation(operateLog)", returning = "result")
    public void recordAudit(JoinPoint joinPoint, OperateLog operateLog, Object result) {
        if (!aopProperties.isAuditLogEnabled()) {
            return;
        }
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            UserInfo user = resolveCurrentUser();
            AuditLog auditLog = new AuditLog();
            if (user != null) {
                auditLog.setUserId(user.getUserId());
                auditLog.setUsername(user.getUsername());
            }
            auditLog.setOperationType(operateLog.type());
            auditLog.setContent(operateLog.module() + ":" + signature.getName()
                    + ", args=" + toJson(joinPoint.getArgs()));
            auditLog.setResult(toJson(result));
            auditLog.setCreateTime(new Date());
            auditLogMapper.insert(auditLog);
        } catch (Exception e) {
            log.error("审计日志写入失败: {}", e.getMessage(), e);
        }
    }

    private UserInfo resolveCurrentUser() {
        UserInfo user = UserContext.getUser();
        if (user != null) {
            return user;
        }
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        HttpServletRequest request = attrs.getRequest();
        Object currentUser = request.getAttribute("currentUser");
        if (currentUser instanceof UserInfo userInfo) {
            return userInfo;
        }
        Object userId = request.getAttribute("userId");
        if (userId instanceof Number number) {
            UserInfo info = new UserInfo();
            info.setUserId(number.longValue());
            return info;
        }
        return null;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return String.valueOf(obj);
        }
    }
}
