package com.example.javaee_ecomorder.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 订单创建结果视图对象
 * 用于 Service 层返回给 Controller，最终包装为 Result 返回给前端
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单主键 ID（数据库自增）
     */
    private Long orderId;

    /**
     * 订单号（业务唯一标识，通常由分布式ID生成器生成）
     */
    private String orderNo;

    /**
     * 可选：订单总金额（便于前端展示）
     */
    // private BigDecimal totalPrice;
}