package com.example.javaee_ecomorder.common.dto;

import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 商品修改数据传输对象
 * 用于接收前端传递的修改商品信息
 */
@Data
public class ProductUpdateDTO {

    /**
     * 商品ID（路径中获取，不在请求体中，但为了方便Service层统一，保留此字段�?
     * 注意：ID通常在Controller中从@PathVariable获取后设置到DTO�?
     */
    @NotNull(message = "商品ID不能为空")
    private Long id;

    /**
     * 商品名称（非必填，不修改则传null�?
     */
    @Size(min = 1, max = 100, message = "商品名称长度必须在1-100之间")
    private String name;

    /**
     * 商品价格（非必填，必须大�?�?
     */
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    private BigDecimal price;

    /**
     * 库存数量（非必填，不能为负数�?
     */
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    /**
     * 商品分类（非必填�?
     */
    @Size(max = 50, message = "分类名称长度不能超过50")
    private String category;

    /**
     * 商品状态（0-下架�?-上架，非必填�?
     */
    @Min(value = 0, message = "状态值必须为0或1")
    @Max(value = 1, message = "状态值必须为0或1")
    private Integer status;
}