package com.example.javaee_ecomorder.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderVO {
    private Long orderId;
    private String orderNo;
    private BigDecimal totalPrice;
    private Integer status;
    private Date createTime;
}
