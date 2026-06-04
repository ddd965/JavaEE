package com.example.javaee_ecomorder.common.context;

import lombok.Data;

import java.util.List;

@Data
public class UserInfo {
    private Long userId;
    private String username;
    private List<String> permissions;
}
