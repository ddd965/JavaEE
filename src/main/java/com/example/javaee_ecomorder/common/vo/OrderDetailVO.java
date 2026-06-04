package com.example.javaee_ecomorder.common.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单详情视图对象
 * 用于 /orders/{id} 接口返回完整的订单信息（含商品明细）
 */
@Data
public class OrderDetailVO {

    // ========== 订单基本信息 ==========
    private Long orderId;           // 订单ID
    private String orderNo;         // 订单�?
    private Long userId;            // 用户ID
    private String username;        // 用户名（冗余，避免二次查询）
    private BigDecimal totalPrice;  // 订单总金�?
    private Integer status;         // 订单状态：0待支�?1已支�?2已发�?3已完�?4已取�?
    private String statusDesc;      // 状态描述（业务层计算）
    private Date createTime;        // 下单时间
    private Date payTime;           // 支付时间
    private Date shipTime;          // 发货时间
    private Date finishTime;        // 完成时间

    // ========== 收货信息（通常来自用户扩展表或订单快照�?==========
    private String receiverName;    // 收货人姓�?
    private String receiverPhone;   // 收货人电�?
    private String receiverAddress; // 收货地址

    // ========== 支付信息 ==========
    private Integer payMethod;      // 支付方式�?微信 2支付�?3银行�?
    private String payMethodDesc;
    private BigDecimal payAmount;   // 实际支付金额（可能优惠后�?

    // ========== 订单商品明细（多对多�?==========
    private List<OrderProductVO> products;   // 商品列表

    /**
     * 订单中的商品视图（内部类或独立类�?
     */
    @Data
    public static class OrderProductVO {
        private Long productId;      // 商品ID
        private String productName;  // 商品名称
        private String productImage; // 商品图片URL
        private BigDecimal price;    // 下单时单�?
        private Integer quantity;    // 购买数量
        private BigDecimal subtotal; // 小计 = price * quantity
    }
}