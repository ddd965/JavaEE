package com.example.javaee_ecomorder.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductQueryDTO {
    private String name;       // 商品名称（模糊查询）
    private String category;   // 分类
    private BigDecimal minPrice; // 最低价
    private BigDecimal maxPrice; // 最高价（可选）
    private Integer status;    // 上架状态

    // 分页参数（默认值）
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}