package com.example.javaee_ecomorder.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartVO {

    /**
     * 购物车中的商品项列表
     */
    private List<CartItemVO> items;

    /**
     * 商品总数量（所有商品数量之和）
     */
    private Integer totalQuantity;

    /**
     * 购物车总金额（所有商品小计之和）
     */
    private BigDecimal totalAmount;

    /**
     * 购物车内商品种类数（去重后的商品个数）
     */
    private Integer totalSkuCount;
}