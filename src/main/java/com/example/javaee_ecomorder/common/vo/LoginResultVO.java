package com.example.javaee_ecomorder.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录成功后的返回结果（VO�?
 * 包含认证令牌及用户基本信息，前端后续请求需携带令牌
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户�?
     */
    private String username;

    /**
     * 认证令牌（JWT �?UUID），前端需在请求头中携�?
     */
    private String token;

    /**
     * 令牌过期时间（毫秒时间戳），用于前端判断本地过期时间
     */
    private Long expireTime;

    /**
     * 用户角色（可选，用于前端权限控制�?
     */
    private String role;

    /**
     * 可选：用户头像或昵称等扩展字段
     */
    private String avatar;

    /**
     * 可选：登录时间（格�?yyyy-MM-dd HH:mm:ss�?
     */
    private String loginTime;
}
