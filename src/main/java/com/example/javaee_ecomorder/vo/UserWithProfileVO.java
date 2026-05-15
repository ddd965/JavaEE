package com.example.javaee_ecomorder.vo;

import lombok.Data;

@Data
public class UserWithProfileVO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String realName;   // 来自 user_profile
    private String address;
    private Integer points;
}