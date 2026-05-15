package com.example.javaee_ecomorder.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 购物车实体类
 * 对应数据库表：cart
 */
@Data
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 购物车项ID */
    private Long id;

    /** 用户ID（关联 user 表） */
    private Long userId;

    /** 商品ID（关联 product 表） */
    private Long productId;

    /** 商品数量 */
    private Integer quantity;

    /** 添加时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}