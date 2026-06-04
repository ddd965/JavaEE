package com.example.javaee_ecomorder.common.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoginLog {
    private Long id;
    private Long userId;
    private String username;
    private Date loginTime;
    private String ip;
    private Integer status;
}
