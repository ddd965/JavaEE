package com.example.javaee_ecomorder.common.dto;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 购物车更新数�?DTO
 * 用于前端传递修改购物车中某商品数量的请求参�?
 */
@Data
public class CartUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 新的商品数量
     */
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "商品数量至少为1")
    private Integer quantity;
}
