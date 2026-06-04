package com.example.javaee_ecomorder.common.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UserCartVO {
    private Long userId;
    private String username;
    private List<CartProductVO> items;   // 购物车中所有商�?
    private BigDecimal totalPrice;       // 总价（所有商品小计之和）
}