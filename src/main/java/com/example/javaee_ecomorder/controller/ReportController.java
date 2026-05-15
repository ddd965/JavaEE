package com.example.javaee_ecomorder.controller;

import com.example.javaee_ecomorder.service.ReportService;
import com.example.javaee_ecomorder.utils.PageResult;
import com.example.javaee_ecomorder.utils.Result;
import com.example.javaee_ecomorder.vo.OrderVO;
import com.example.javaee_ecomorder.vo.OrderWithProductsVO;
import com.example.javaee_ecomorder.vo.UserProfileVO;
import com.example.javaee_ecomorder.vo.UserWithOrdersVO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/user/{userId}/with-profile")
    public Result<UserProfileVO> userWithProfile(@PathVariable Long userId) {
        return Result.success(reportService.getUserWithProfile(userId));
    }

    @GetMapping("/user/{userId}/with-orders")
    public Result<UserWithOrdersVO> userWithOrders(@PathVariable Long userId) {
        return Result.success(reportService.getUserWithOrders(userId));
    }

    @GetMapping("/order/{orderId}/with-products")
    public Result<OrderWithProductsVO> orderWithProducts(@PathVariable Long orderId) {
        return Result.success(reportService.getOrderWithProducts(orderId));
    }

    @GetMapping("/orders/page")
    public Result<PageResult<OrderVO>> orderPage(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(reportService.pageOrders(userId, status, startTime, endTime, page, size));
    }
}
