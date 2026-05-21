package com.example.javaee_ecomorder.aop;

import com.example.javaee_ecomorder.utils.IpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Order(2)
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("execution(* com.example..controller.*.*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        HttpServletRequest request = currentRequest();
        String url = request != null ? request.getRequestURI() : "N/A";
        String ip = request != null ? IpUtil.getIpAddr(request) : "unknown";
        String params = toJson(joinPoint.getArgs());
        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            long cost = System.currentTimeMillis() - start;
            log.info("[RequestLog] url={}, method={}.{}, ip={}, params={}, result={}, cost={}ms",
                    url, className, methodName, ip, params, toJson(result), cost);
        }
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    private String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return String.valueOf(obj);
        }
    }
}
