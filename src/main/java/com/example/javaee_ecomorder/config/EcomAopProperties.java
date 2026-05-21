package com.example.javaee_ecomorder.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ecom.aop")
public class EcomAopProperties {
    private long performanceThresholdMs = 500;
    private boolean auditLogEnabled = true;
    private boolean cacheEnabled = true;
    private String tokenHeader = "Authorization";
}
