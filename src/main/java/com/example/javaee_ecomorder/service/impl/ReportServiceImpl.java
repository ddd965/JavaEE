package com.example.javaee_ecomorder.service.impl;

import com.example.javaee_ecomorder.mapper.OrderMapper;
import com.example.javaee_ecomorder.mapper.UserMapper;
import com.example.javaee_ecomorder.service.ReportService;
import com.example.javaee_ecomorder.utils.PageResult;
import com.example.javaee_ecomorder.vo.OrderVO;
import com.example.javaee_ecomorder.vo.OrderWithProductsVO;
import com.example.javaee_ecomorder.vo.UserProfileVO;
import com.example.javaee_ecomorder.vo.UserWithOrdersVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;

    @Override
    public UserProfileVO getUserWithProfile(Long userId) {
        return userMapper.selectUserWithProfile(userId);
    }

    @Override
    public UserWithOrdersVO getUserWithOrders(Long userId) {
        return userMapper.selectUserWithOrders(userId);
    }

    @Override
    public OrderWithProductsVO getOrderWithProducts(Long orderId) {
        return orderMapper.selectOrderWithProducts(orderId);
    }

    @Override
    public PageResult<OrderVO> pageOrders(Long userId, Integer status, Date startTime, Date endTime, int page, int size) {
        int p = Math.max(page, 1);
        int s = Math.min(Math.max(size, 1), 200);
        int offset = (p - 1) * s;
        long total = orderMapper.countOrderPage(userId, status, null, null, null, startTime, endTime);
        List<OrderVO> records = orderMapper.selectOrderPage(userId, status, null, null, null, startTime, endTime, offset, s);
        return new PageResult<>(total, records);
    }
}
