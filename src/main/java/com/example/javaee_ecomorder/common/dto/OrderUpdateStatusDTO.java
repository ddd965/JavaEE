package com.example.javaee_ecomorder.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 订单状态更新请�?DTO
 * 用于 PUT /orders/{id}/status 接口
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderUpdateStatusDTO {

    /**
     * 订单ID（从路径中传入，不由前端JSON传递时也可以不包含此字段，
     * 但为了方�?service 层统一接收，通常仍保留，�?Controller 手动设置�?
     */
    @NotNull(message = "订单ID不能为空")
    @Min(value = 1, message = "订单ID必须为正整数")
    private Long orderId;

    /**
     * 目标状态码
     * 0-待支�? 1-已支�? 2-已发�? 3-已完�? 4-已取�?
     */
    @NotNull(message = "订单状态不能为空")
    @Min(value = 0, message = "订单状态码最小为0")
    @Max(value = 4, message = "订单状态码最大为4")
    private Integer status;
}
