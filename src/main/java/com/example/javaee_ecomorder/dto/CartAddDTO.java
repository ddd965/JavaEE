package com.example.javaee_ecomorder.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 购物车添加商品请求DTO
 * 用于接收前端传递的添加购物车参数
 */
@Data
public class CartAddDTO {

    /**
     * 用户ID（必填，不能为空）
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 商品ID（必填，不能为空）
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 添加数量（必填，最小值为1）
     */
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于等于1")
    private Integer quantity;

    /**
     * 可选：备注信息（如规格、颜色等）
     * 非必填，默认可为null
     */
    private String remark;
}
