package com.example.javaee_ecomorder.controller;


import com.example.javaee_ecomorder.dto.UserQueryDTO;
import com.example.javaee_ecomorder.dto.UserRegisterDTO;
import com.example.javaee_ecomorder.dto.UserUpdateDTO;
import com.example.javaee_ecomorder.service.UserService;
import com.example.javaee_ecomorder.utils.PageResult;
import com.example.javaee_ecomorder.utils.Result;
import com.example.javaee_ecomorder.vo.OrderVO;
import com.example.javaee_ecomorder.vo.UserListVO;
import com.example.javaee_ecomorder.vo.UserProfileVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid UserRegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @GetMapping("/{id}/profile")
    public Result<UserProfileVO> getUserWithProfile(@PathVariable Long id) {
        // 一对一关联查询
        UserProfileVO vo = userService.getUserWithProfile(id);
        return Result.success(vo);
    }

    @GetMapping("/{id}/orders")
    public Result<List<OrderVO>> getUserOrders(@PathVariable Long id) {
        // 一对多查询
        List<OrderVO> orders = userService.getUserOrders(id);
        return Result.success(orders);
    }

    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateDTO dto) {
        dto.setId(id);
        userService.updateUser(dto);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult<UserListVO>> pageQuery(UserQueryDTO query) {
        return Result.success(userService.pageQuery(query));
    }
}