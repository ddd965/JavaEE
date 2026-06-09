package com.example.javaee_ecomorder.starter;

import com.example.javaee_ecomorder.init.EcomContextInitializer;
import com.example.javaee_ecomorder.init.EcomReadyListener;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = {
                "com.example.javaee_ecomorder.biz",
                "com.example.javaee_ecomorder.common"
        },
        exclude = {
                SecurityAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class
        }
)
@MapperScan("com.example.javaee_ecomorder.common.mapper")
public class BizApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BizApplication.class);
        app.setAdditionalProfiles("biz");
        app.setBannerMode(Banner.Mode.CONSOLE);
        app.addInitializers(new EcomContextInitializer());
        app.addListeners(new EcomReadyListener());
        app.run(args);
    }
}
