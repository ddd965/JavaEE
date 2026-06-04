package com.example.javaee_ecomorder.common.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Order {
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalPrice;
    private Integer status; // 0待支�?1已支�?2已发�?3已完�?4已取�?
    private Date createTime;
}