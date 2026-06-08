package com.example.javaee_ecomorder.common.dto;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建订单的请�?DTO
 * 用于接收前端提交的订单数据（用户ID + 购物车商品列表）
 */
@Data
public class OrderCreateDTO {

    /**
     * 用户ID（必填）
     */
    @NotNull(message = "用户ID不能为空")
    @Positive(message = "用户ID必须是正数")
    private Long userId;

    /**
     * 收货地址（可选，若为空则使用用户默认地址�?
     */
    private String address;

    /**
     * 订单备注（可选）
     */
    private String remark;

    /**
     * 订单商品明细列表（至少包含一个商品）
     */
    @NotNull(message = "订单商品列表不能为空")
    @Valid   // 触发嵌套校验（对 list 中的每个 OrderItemDTO 进行校验�?
    private List<OrderItemDTO> items;

    /**
     * 订单商品明细内部�?
     */
    @Data
    public static class OrderItemDTO {

        /**
         * 商品ID
         */
        @NotNull(message = "商品ID不能为空")
        @Positive(message = "用户ID必须是正数")
        private Long productId;

        /**
         * 购买数量
         */
        @NotNull(message = "商品数量不能为空")
        @Min(value = 1, message = "购买数量至少为1")
        private Integer quantity;

        /**
         * 下单时的商品单价（用于金额计算，防止价格变动�?
         * 前端传递或后端根据商品ID查询填充，建议由后端查询填充，前端可不传
         */
        private BigDecimal price;
    }
}
