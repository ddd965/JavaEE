package com.example.javaee_ecomorder.starter;

import com.example.javaee_ecomorder.init.EcomContextInitializer;
import com.example.javaee_ecomorder.init.EcomReadyListener;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.example.javaee_ecomorder.admin",
        "com.example.javaee_ecomorder.common"
})
@MapperScan("com.example.javaee_ecomorder.common.mapper")
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AdminApplication.class);
        app.setAdditionalProfiles("admin");
        app.setBannerMode(Banner.Mode.CONSOLE);
        app.addInitializers(new EcomContextInitializer());
        app.addListeners(new EcomReadyListener());
        app.run(args);
    }
}
