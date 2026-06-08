package com.example.javaee_ecomorder.common.basic;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {

    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    SHIPPED(2, "已发货"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消");

    private final int code;
    private final String desc;

    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(Integer status) {
        if (status == null) {
            return "未知状态";
        }
        for (OrderStatusEnum value : values()) {
            if (value.code == status) {
                return value.desc;
            }
        }
        return "未知状态";
    }

    public static OrderStatusEnum of(Integer status) {
        if (status == null) {
            return null;
        }
        for (OrderStatusEnum value : values()) {
            if (value.code == status) {
                return value;
            }
        }
        return null;
    }
}
