package com.example.javaee_ecomorder.biz.controller;

import com.example.javaee_ecomorder.common.annotation.RequireLogin;
import com.example.javaee_ecomorder.common.annotation.RequirePermission;
import com.example.javaee_ecomorder.common.dto.OrderCreateDTO;
import com.example.javaee_ecomorder.common.dto.OrderQueryDTO;
import com.example.javaee_ecomorder.common.dto.OrderUpdateStatusDTO;
import com.example.javaee_ecomorder.biz.service.OrderService;
import com.example.javaee_ecomorder.common.utils.PageResult;
import com.example.javaee_ecomorder.common.utils.Result;
import com.example.javaee_ecomorder.common.vo.OrderDetailVO;
import com.example.javaee_ecomorder.common.vo.OrderListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单（涉及库存扣减、生成订单明细，事务操作�?
     * POST /api/orders
     */
    @PostMapping
    public Result<Long> createOrder(@RequestBody @Valid OrderCreateDTO dto) {
        Long orderId = orderService.createOrder(dto);
        return Result.success(orderId);
    }

    /**
     * 查询订单详情（包含订单商品明细，多对多查询）
     * GET /api/orders/{id}
     */
    @GetMapping("/{id}")
    public Result<OrderDetailVO> getOrderDetail(@PathVariable Long id) {
        OrderDetailVO detail = orderService.getOrderDetail(id);
        return Result.success(detail);
    }

    /**
     * 条件分页查询订单列表（支持按用户、状态、时间范围）
     * GET /api/orders/page?userId=1&status=0&pageNum=1&pageSize=10
     */
    @GetMapping("/page")
    public Result<PageResult<OrderListVO>> pageQuery(OrderQueryDTO query) {
        PageResult<OrderListVO> page = orderService.pageQuery(query);
        return Result.success(page);
    }

    /**
     * 更新订单状态（例如：支付、发货、确认收货）
     * PUT /api/orders/{id}/status
     */
    @PutMapping("/{id}/status")
    @RequireLogin
    @RequirePermission("order:manage")
    public Result<Void> updateStatus(@PathVariable Long id,
                                     @RequestBody @Valid OrderUpdateStatusDTO dto) {
        dto.setOrderId(id);
        orderService.updateOrderStatus(dto);
        return Result.success();
    }

    /**
     * 取消订单（需要校验状态）
     * DELETE /api/orders/{id}
     */
    @DeleteMapping("/{id}")
    @RequireLogin
    @RequirePermission("order:manage")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.success();
    }
}