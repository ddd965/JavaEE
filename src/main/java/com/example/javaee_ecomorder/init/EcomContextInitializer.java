package com.example.javaee_ecomorder.init;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class EcomContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        String[] activeProfiles = context.getEnvironment().getActiveProfiles();
        String redisHost = context.getEnvironment().getProperty("spring.data.redis.host", "localhost");
        String redisPort = context.getEnvironment().getProperty("spring.data.redis.port", "6379");

        System.out.println("[EcomInit] 启动模式: " + String.join(",", activeProfiles));
        System.out.println("[EcomInit] Redis连接: " + redisHost + ":" + redisPort);
    }
}
