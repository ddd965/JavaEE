package com.example.javaee_ecomorder;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.javaee_ecomorder.mapper")  // 扫描所有 Mapper 接口
public class EcomOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcomOrderApplication.class, args);
    }
}
