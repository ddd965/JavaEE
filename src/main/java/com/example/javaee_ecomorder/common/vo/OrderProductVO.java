package com.example.javaee_ecomorder.common.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderProductVO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal subtotal;
}
