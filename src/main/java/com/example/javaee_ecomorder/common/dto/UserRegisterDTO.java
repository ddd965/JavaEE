package com.example.javaee_ecomorder.common.dto;


import lombok.Data;
import jakarta.validation.constraints.*;

/**
 * 用户注册数据传输对象
 * 用于接收前端注册请求的JSON数据，并进行基础格式校验
 */
@Data
public class UserRegisterDTO {

    /**
     * 用户�?
     * - 不能为空
     * - 长度 4~20 个字�?
     * - 只能包含字母、数字、下划线
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4~20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    /**
     * 密码
     * - 不能为空
     * - 长度 6~18 个字�?
     * - 至少包含字母和数字（实际强度校验可在Service层做�?
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 18, message = "密码长度必须在6~18个字符之间")
    private String password;

    /**
     * 确认密码
     * - 不能为空
     * - 必须与password一致（一致性校验在Service层完成，DTO中仅作格式约束）
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    /**
     * 电子邮箱
     * - 非必填（可为空）
     * - 若填写必须符合邮箱格�?
     */
    @Email(message = "邮箱格式不正确 ")
    private String email;

    /**
     * 手机号码
     * - 非必填（可为空）
     * - 若填写必须符合中国大陆手机号格式
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}
