package com.example.javaee_ecomorder.service;

import com.example.javaee_ecomorder.utils.PageResult;
import com.example.javaee_ecomorder.vo.OrderVO;
import com.example.javaee_ecomorder.vo.OrderWithProductsVO;
import com.example.javaee_ecomorder.vo.UserProfileVO;
import com.example.javaee_ecomorder.vo.UserWithOrdersVO;

import java.util.Date;

public interface ReportService {

    UserProfileVO getUserWithProfile(Long userId);

    UserWithOrdersVO getUserWithOrders(Long userId);

    OrderWithProductsVO getOrderWithProducts(Long orderId);

    PageResult<OrderVO> pageOrders(Long userId, Integer status, Date startTime, Date endTime, int page, int size);
}
