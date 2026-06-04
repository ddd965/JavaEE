package com.example.javaee_ecomorder.biz.service;

import com.example.javaee_ecomorder.common.utils.PageResult;
import com.example.javaee_ecomorder.common.vo.OrderVO;
import com.example.javaee_ecomorder.common.vo.OrderWithProductsVO;
import com.example.javaee_ecomorder.common.vo.UserProfileVO;
import com.example.javaee_ecomorder.common.vo.UserWithOrdersVO;

import java.util.Date;

public interface ReportService {

    UserProfileVO getUserWithProfile(Long userId);

    UserWithOrdersVO getUserWithOrders(Long userId);

    OrderWithProductsVO getOrderWithProducts(Long orderId);

    PageResult<OrderVO> pageOrders(Long userId, Integer status, Date startTime, Date endTime, int page, int size);
}
