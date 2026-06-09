package com.example.javaee_ecomorder.biz.config;

import com.example.javaee_ecomorder.common.security.EcomPasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BizConfig {

    @Bean
    public EcomPasswordEncoder ecomPasswordEncoder() {
        return new EcomPasswordEncoder();
    }
}
