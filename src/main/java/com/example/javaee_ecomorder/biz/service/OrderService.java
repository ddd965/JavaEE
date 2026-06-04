package com.example.javaee_ecomorder.biz.service;

import com.example.javaee_ecomorder.common.dto.OrderCreateDTO;
import com.example.javaee_ecomorder.common.dto.OrderQueryDTO;
import com.example.javaee_ecomorder.common.dto.OrderUpdateStatusDTO;
import com.example.javaee_ecomorder.common.utils.PageResult;
import com.example.javaee_ecomorder.common.vo.OrderDetailVO;
import com.example.javaee_ecomorder.common.vo.OrderListVO;

public interface OrderService {

    Long createOrder(OrderCreateDTO dto);

    OrderDetailVO getOrderDetail(Long orderId);

    PageResult<OrderListVO> pageQuery(OrderQueryDTO query);

    void updateOrderStatus(OrderUpdateStatusDTO dto);

    void cancelOrder(Long orderId);
}
