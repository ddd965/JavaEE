package com.example.javaee_ecomorder.vo;

import com.example.javaee_ecomorder.entity.Product;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;

/**
 * 商品视图对象（VO）
 * 用于Service层封装后返回给Controller，避免直接暴露Entity
 */
@Data
public class ProductVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;                // 商品ID
    private String name;            // 商品名称
    private BigDecimal price;       // 单价
    private Integer stock;          // 库存数量
    private String category;        // 分类
    private Integer status;         // 状态（0下架 1上架）
    private String statusDesc;      // 状态描述（非数据库字段，用于前端展示）
    private String createTime;      // 创建时间（格式化后的字符串，可选）

    /**
     * 无参构造
     */
    public ProductVO() {
    }

    /**
     * 从Product实体构造ProductVO
     * @param product Product实体
     */
    public ProductVO(Product product) {
        if (product != null) {
            this.id = product.getId();
            this.name = product.getName();
            this.price = product.getPrice();
            this.stock = product.getStock();
            this.category = product.getCategory();
            this.status = product.getStatus();
            // 根据status转换状态描述
            this.statusDesc = (product.getStatus() != null && product.getStatus() == 1) ? "上架" : "下架";
            // 如果Product有createTime字段，可以格式化后赋值
            // 这里假设Product实体有createTime字段，类型为java.util.Date
            // if (product.getCreateTime() != null) {
            //     this.createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(product.getCreateTime());
            // }
        }
    }
}
