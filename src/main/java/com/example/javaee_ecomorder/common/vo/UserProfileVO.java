package com.example.javaee_ecomorder.common.vo;

import lombok.Data;
import java.util.Date;

/**
 * 用户及其个人资料视图对象（一对一关联）
 * 用于 /users/{id}/profile 接口返回
 */
@Data
public class UserProfileVO {
    // 用户基本信息（来�?user 表）
    private Long id;                 // 用户ID
    private String username;         // 用户�?
    private String email;            // 邮箱
    private String phone;            // 手机�?
    private Date createTime;         // 注册时间

    // 用户扩展信息（来�?user_profile 表，一对一�?
    private String realName;         // 真实姓名
    private String address;          // 收货地址（可扩展为多个地址，此处简化）
    private Integer points;          // 积分
}
