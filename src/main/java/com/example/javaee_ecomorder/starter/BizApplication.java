package com.example.javaee_ecomorder.starter;

import com.example.javaee_ecomorder.init.EcomContextInitializer;
import com.example.javaee_ecomorder.init.EcomReadyListener;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@EnableAutoConfiguration
@ComponentScan(
        basePackages = "com.example.javaee_ecomorder",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.example\\.javaee_ecomorder\\.admin\\..*"
        )
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
