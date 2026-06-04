package com.example.javaee_ecomorder.common.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

/**
 * 用户信息修改 DTO
 * 用于接收前端提交的修改用户资料请�?
 */
@Data
public class UserUpdateDTO {

    /**
     * 用户ID（由路径传递，不从请求体获取，但为了Service方便会统一设置�?
     * 不使用校验注解，因为Controller会单独校验非�?
     */
    private Long id;

    /**
     * 用户名（可修改，需唯一�?
     */
    @NotBlank(message = "用户名不能为�?)
    @Length(min = 4, max = 20, message = "用户名长度必须在4~20之间")
    private String username;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正�?)
    private String email;

    /**
     * 手机�?
     */
    @NotBlank(message = "手机号不能为�?)
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 真实姓名（来�?user_profile 表）
     */
    @NotBlank(message = "真实姓名不能为空")
    @Length(min = 2, max = 20, message = "真实姓名长度必须�?~20之间")
    private String realName;

    /**
     * 收货地址（来�?user_profile 表）
     */
    @NotBlank(message = "收货地址不能为空")
    @Length(max = 200, message = "收货地址不能超过200�?)
    private String address;
}
