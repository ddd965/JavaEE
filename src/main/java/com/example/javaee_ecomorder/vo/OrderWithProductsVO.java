package com.example.javaee_ecomorder.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderWithProductsVO {
    private Long orderId;
    private String orderNo;
    private BigDecimal totalPrice;
    private List<OrderProductVO> products;
}
