package com.example.javaee_ecomorder.admin.controller;

import com.example.javaee_ecomorder.admin.annotation.RequirePermission;
import com.example.javaee_ecomorder.common.entity.LoginLog;
import com.example.javaee_ecomorder.biz.service.UserService;
import com.example.javaee_ecomorder.common.utils.Result;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserSecurityController {

    @Autowired
    private UserService userService;

    @PostMapping("/password")
    public Result<Void> updatePassword(@RequestBody @Validated PasswordUpdateRequest request) {
        userService.updatePassword(request.getUserId(), request.getOldPassword(), request.getNewPassword());
        return Result.success();
    }

    @PostMapping("/resetPassword")
    @RequirePermission("order:manage")
    public Result<Void> resetPassword(@RequestBody @Validated ResetPasswordRequest request) {
        userService.resetPassword(request.getUserId(), request.getNewPassword());
        return Result.success();
    }

    @PostMapping("/lock/{userId}")
    @RequirePermission("order:manage")
    public Result<Void> lockAccount(@PathVariable Long userId) {
        userService.lockAccount(userId);
        return Result.success();
    }

    @PostMapping("/unlock/{userId}")
    @RequirePermission("order:manage")
    public Result<Void> unlockAccount(@PathVariable Long userId) {
        userService.unlockAccount(userId);
        return Result.success();
    }

    @GetMapping("/loginLogs/{userId}")
    @RequirePermission("order:manage")
    public Result<List<LoginLog>> loginLogs(@PathVariable Long userId) {
        return Result.success(userService.getLoginLogs(userId));
    }

    @Data
    public static class PasswordUpdateRequest {
        @NotNull
        private Long userId;
        @NotBlank
        private String oldPassword;
        @NotBlank
        private String newPassword;
    }

    @Data
    public static class ResetPasswordRequest {
        @NotNull
        private Long userId;
        @NotBlank
        private String newPassword;
    }
}
