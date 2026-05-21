package com.example.javaee_ecomorder.entity;

import lombok.Data;

import java.util.Date;

@Data
public class AuditLog {
    private Long id;
    private Long userId;
    private String username;
    private String operationType;
    private String content;
    private String result;
    private Date createTime;
}
