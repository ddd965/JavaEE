package com.example.javaee_ecomorder.controller;

import com.example.javaee_ecomorder.service.UserService;
import com.example.javaee_ecomorder.utils.Result;
import com.example.javaee_ecomorder.vo.LoginResultVO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<LoginResultVO> login(@RequestBody @Validated LoginRequest request) {
        LoginResultVO result = userService.login(request.getUsername(), request.getPassword());
        return Result.success(result);
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
    }
}
