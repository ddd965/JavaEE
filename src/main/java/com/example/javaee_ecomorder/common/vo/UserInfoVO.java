package com.example.javaee_ecomorder.common.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户信息视图对象（含一对一关联的个人资料）
 * 用于前端展示用户详情页、个人信息等场景
 */
@Data
@NoArgsConstructor
public class UserInfoVO {

    // 用户基本信息（来�?user 表）
    private Long id;                // 用户ID
    private String username;        // 用户�?    private String email;           // 邮箱
    private String phone;           // 手机�?    private Date createTime;        // 注册时间

    // 用户扩展信息（来�?user_profile 表，一对一关系�?    private String realName;        // 真实姓名
    private String address;         // 收货地址
    private Integer points;         // 积分

    /**
     * 便捷构造方法（用于 Service 层快速创�?VO�?     * @param id 用户ID
     * @param username 用户�?     * @param email 邮箱
     * @param phone 手机�?     * @param createTime 注册时间
     * @param realName 真实姓名
     * @param address 收货地址
     * @param points 积分
     */
    public UserInfoVO(Long id, String username, String email, String phone,
                      Date createTime, String realName, String address, Integer points) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.createTime = createTime;
        this.realName = realName;
        this.address = address;
        this.points = points;
    }
}
