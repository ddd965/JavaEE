package com.example.javaee_ecomorder;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.example.javaee_ecomorder.biz",
        "com.example.javaee_ecomorder.admin",
        "com.example.javaee_ecomorder.common"
})
@MapperScan("com.example.javaee_ecomorder.common.mapper")
public class EcomOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcomOrderApplication.class, args);
    }
}
