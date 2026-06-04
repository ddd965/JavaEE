package com.example.javaee_ecomorder.common.basic;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {

    PENDING(0, "待支�?),
    PAID(1, "已支�?),
    SHIPPED(2, "已发�?),
    COMPLETED(3, "已完�?),
    CANCELLED(4, "已取�?);

    private final int code;
    private final String desc;

    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(Integer status) {
        if (status == null) {
            return "未知状�?;
        }
        for (OrderStatusEnum value : values()) {
            if (value.code == status) {
                return value.desc;
            }
        }
        return "未知状�?;
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
