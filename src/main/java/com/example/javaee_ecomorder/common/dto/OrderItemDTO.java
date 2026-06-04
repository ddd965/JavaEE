package com.example.javaee_ecomorder.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 订单项DTO - 用于创建订单时传递商品明�?
 * 对应前端传递的购物车商品信�?
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 商品名称（可选，便于前端展示，后端实际会从数据库查询�?
     */
    private String productName;

    /**
     * 购买数量
     */
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量至少�?")
    private Integer quantity;

    /**
     * 商品单价（下单时的快照价格，防止后续价格变动影响订单�?
     */
    @NotNull(message = "商品单价不能为空")
    @Min(value = 0, message = "商品单价不能为负�?)
    private BigDecimal price;

    /**
     * 小计金额（可选，可由后端计算，也可以前端传入�?
     * 如果前端传入，后端仍会重新校验一致�?
     */
    private BigDecimal subtotal;
}
