package com.example.javaee_ecomorder.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderProductVO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal subtotal;
}
