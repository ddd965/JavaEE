package com.example.javaee_ecomorder.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Order {
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalPrice;
    private Integer status; // 0待支付 1已支付 2已发货 3已完成 4已取消
    private Date createTime;
}