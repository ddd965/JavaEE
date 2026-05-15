package com.example.javaee_ecomorder.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemVO {

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品单价
     */
    private BigDecimal price;

    /**
     * 商品图片URL（可选）
     */
    private String imageUrl;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 该商品小计（单价 × 数量）
     */
    private BigDecimal subtotal;

    /**
     * 库存（用于前端展示是否充足）
     */
    private Integer stock;

    /**
     * 是否被选中（用于前端复选框，默认true）
     */
    private Boolean selected = true;
}