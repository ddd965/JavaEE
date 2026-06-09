package com.example.javaee_ecomorder.common.aop;

import com.example.javaee_ecomorder.common.annotation.PerfMonitor;
import com.example.javaee_ecomorder.common.config.EcomAopProperties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(4)
public class PerformanceAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceAspect.class);

    @Autowired
    private EcomAopProperties aopProperties;

    @Around("@annotation(perfMonitor)")
    public Object monitor(ProceedingJoinPoint joinPoint, PerfMonitor perfMonitor) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long cost = System.currentTimeMillis() - start;
            long threshold = perfMonitor.threshold() > 0
                    ? perfMonitor.threshold()
                    : aopProperties.getPerformanceThresholdMs();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String method = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
            if (cost > threshold) {
                log.warn("[PerfWarn] method={}, cost={}ms, threshold={}ms", method, cost, threshold);
            } else {
                log.debug("[Perf] method={}, cost={}ms", method, cost);
            }
        }
    }
}
