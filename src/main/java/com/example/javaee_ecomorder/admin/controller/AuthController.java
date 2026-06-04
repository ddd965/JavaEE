package com.example.javaee_ecomorder.admin.controller;

import com.example.javaee_ecomorder.common.config.EcomAopProperties;
import com.example.javaee_ecomorder.admin.service.AuthService;
import com.example.javaee_ecomorder.common.utils.Result;
import com.example.javaee_ecomorder.common.vo.LoginResultVO;
import jakarta.servlet.http.HttpServletRequest;
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
    private AuthService authService;
    @Autowired
    private EcomAopProperties aopProperties;

    @PostMapping("/login")
    public Result<LoginResultVO> login(@RequestBody @Validated LoginRequest request) {
        LoginResultVO result = authService.login(request.getUsername(), request.getPassword());
        return Result.success(result);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String token = request.getHeader(aopProperties.getTokenHeader());
        authService.logout(token);
        return Result.success();
    }

    @PostMapping("/refresh")
    public Result<LoginResultVO> refresh(HttpServletRequest request) {
        String token = request.getHeader(aopProperties.getTokenHeader());
        return Result.success(authService.refresh(token));
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为�?)
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
    }
}
