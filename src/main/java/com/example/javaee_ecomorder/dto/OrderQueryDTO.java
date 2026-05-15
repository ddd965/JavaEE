package com.example.javaee_ecomorder.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import java.util.Date;

/**
 * 订单查询参数 DTO
 * 用于接收前端传递的条件分页查询参数
 */
@Data
public class OrderQueryDTO {

    // ========== 业务查询条件 ==========
    /**
     * 用户ID（可选，查询指定用户的订单）
     */
    private Long userId;

    /**
     * 订单状态（可选）
     * 0-待支付 1-已支付 2-已发货 3-已完成 4-已取消
     */
    private Integer status;

    /**
     * 订单号（可选，模糊查询）
     */
    private String orderNo;

    /**
     * 下单开始时间（可选，格式：yyyy-MM-dd HH:mm:ss）
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 下单结束时间（可选）
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 最小订单金额（可选）
     */
    @Min(value = 0, message = "最小金额不能小于0")
    private Double minTotalPrice;

    /**
     * 最大订单金额（可选）
     */
    @Min(value = 0, message = "最大金额不能小于0")
    private Double maxTotalPrice;

    // ========== 分页参数 ==========
    /**
     * 当前页码（从1开始），默认1
     */
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    /**
     * 每页条数，默认10
     */
    @Min(value = 1, message = "每页条数最小为1")
    private Integer pageSize = 10;
}