package com.example.javaee_ecomorder.common.aop;

import com.example.javaee_ecomorder.admin.annotation.CacheRedis;
import com.example.javaee_ecomorder.common.config.EcomAopProperties;
import com.example.javaee_ecomorder.common.utils.RedisCacheUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Order(6)
public class CacheAspect {

    private static final String NULL_PLACEHOLDER = "__NULL__";

    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Autowired
    private RedisCacheUtil redisCacheUtil;
    @Autowired
    private EcomAopProperties aopProperties;

    @Around("@annotation(cacheRedis)")
    public Object cacheAround(ProceedingJoinPoint joinPoint, CacheRedis cacheRedis) throws Throwable {
        if (!aopProperties.isCacheEnabled()) {
            return joinPoint.proceed();
        }
        String cacheKey = buildCacheKey(joinPoint, cacheRedis.key());
        Object cached = redisCacheUtil.get(cacheKey);
        if (NULL_PLACEHOLDER.equals(cached)) {
            return null;
        }
        if (cached != null) {
            return cached;
        }
        Object result = joinPoint.proceed();
        if (result == null) {
            redisCacheUtil.set(cacheKey, NULL_PLACEHOLDER, cacheRedis.ttl(), TimeUnit.SECONDS);
        } else {
            redisCacheUtil.set(cacheKey, result, cacheRedis.ttl(), TimeUnit.SECONDS);
        }
        return result;
    }

    private String buildCacheKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = nameDiscoverer.getParameterNames(signature.getMethod());
        Object[] args = joinPoint.getArgs();
        EvaluationContext context = new StandardEvaluationContext();
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }
        for (int i = 0; i < args.length; i++) {
            context.setVariable("p" + i, args[i]);
            context.setVariable("a" + i, args[i]);
        }
        String resolved = parser.parseExpression(keyExpression).getValue(context, String.class);
        return "cache:" + (resolved != null ? resolved : keyExpression);
    }
}
