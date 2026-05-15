package com.example.javaee_ecomorder.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单列表视图对象（用于分页查询结果）
 * 展示订单的摘要信息，不包含订单商品明细
 */
@Data
public class OrderListVO {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单号（全局唯一）
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名（冗余字段，避免关联查询）
     */
    private String username;

    /**
     * 订单总金额
     */
    private BigDecimal totalPrice;

    /**
     * 订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消
     */
    private Integer status;

    /**
     * 状态描述（便于前端直接展示）
     */
    private String statusDesc;

    /**
     * 下单时间
     */
    private Date createTime;

    /**
     * 支付时间（可为空）
     */
    private Date payTime;

    /**
     * 发货时间（可为空）
     */
    private Date deliveryTime;

    /**
     * 完成时间（可为空）
     */
    private Date completeTime;

    /**
     * 构造方法：根据实体或其它来源设置状态描述
     * 可根据实际需要重写或通过Service层设置
     */
    public void setStatus(Integer status) {
        this.status = status;
        // 根据状态码设置描述
        switch (status) {
            case 0:
                this.statusDesc = "待支付";
                break;
            case 1:
                this.statusDesc = "已支付";
                break;
            case 2:
                this.statusDesc = "已发货";
                break;
            case 3:
                this.statusDesc = "已完成";
                break;
            case 4:
                this.statusDesc = "已取消";
                break;
            default:
                this.statusDesc = "未知状态";
        }
    }
}
