package com.example.javaee_ecomorder.init;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

public class EcomReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String port = event.getApplicationContext()
                .getEnvironment().getProperty("server.port", "8080");
        String appName = event.getApplicationContext()
                .getEnvironment().getProperty("spring.application.name", "ecom-order");

        System.out.println("========================================");
        System.out.println("  " + appName + " 启动完成！");
        System.out.println("  端口: " + port);
        System.out.println("========================================");
    }
}
