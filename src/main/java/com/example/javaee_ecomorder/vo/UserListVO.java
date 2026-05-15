package com.example.javaee_ecomorder.vo;

import lombok.Data;
import java.util.Date;

/**
 * 用户列表视图对象
 * 用于分页查询返回的用户概要信息
 */
@Data
public class UserListVO {
    private Long id;              // 用户ID
    private String username;      // 用户名
    private String email;         // 邮箱
    private String phone;         // 手机号
    private Date createTime;      // 注册时间
}
