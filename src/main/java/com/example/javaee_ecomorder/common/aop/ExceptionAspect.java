package com.example.javaee_ecomorder.common.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(3)
public class ExceptionAspect {

    private static final Logger log = LoggerFactory.getLogger(ExceptionAspect.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("execution(* com.example..controller.*.*(..))")
    public void controllerPointcut() {
    }

    @AfterThrowing(pointcut = "controllerPointcut()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String location = signature.getDeclaringTypeName() + "." + signature.getName();
        String params = toJson(joinPoint.getArgs());
        log.error("[ExceptionLog] type={}, location={}, params={}, message={}",
                ex.getClass().getSimpleName(), location, params, ex.getMessage(), ex);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return String.valueOf(obj);
        }
    }
}
