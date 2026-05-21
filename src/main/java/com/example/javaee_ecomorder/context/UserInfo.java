package com.example.javaee_ecomorder.context;

import lombok.Data;

import java.util.List;

@Data
public class UserInfo {
    private Long userId;
    private String username;
    private List<String> permissions;
}
