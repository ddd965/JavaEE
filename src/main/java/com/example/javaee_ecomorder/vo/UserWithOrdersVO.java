package com.example.javaee_ecomorder.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserWithOrdersVO {
    private Long userId;
    private String username;
    private List<OrderVO> orders;   // 一对多，订单列表
}
