package com.example.javaee_ecomorder.common.vo;

import lombok.Data;
import java.util.Date;

/**
 * 用户列表视图对象
 * 用于分页查询返回的用户概要信�?
 */
@Data
public class UserListVO {
    private Long id;              // 用户ID
    private String username;      // 用户�?
    private String email;         // 邮箱
    private String phone;         // 手机�?
    private Date createTime;      // 注册时间
    private Boolean enabled;           // 状态：启用/禁用
    private Boolean accountNonLocked;    // 锁定：正�?已锁�?
    private Integer failCount;         // 失败次数
    private Date lastLoginTime;        // 最后登录时�?

}