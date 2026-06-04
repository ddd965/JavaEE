package com.example.javaee_ecomorder.common.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CartProductVO {
    private Long cartId;
    private Long userId;
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private String productImage;   // 商品图片URL（如果有�?
    private Integer quantity;
    private BigDecimal subtotal;   // 小计 = productPrice * quantity
    private Date createTime;
}
