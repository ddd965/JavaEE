package com.example.javaee_ecomorder.entity;

import lombok.Data;

@Data
public class UserProfile {
    private Long id;
    private Long userId;
    private String realName;
    private String address;
    private Integer points;
}