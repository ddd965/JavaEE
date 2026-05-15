package com.example.javaee_ecomorder.service;

import com.example.javaee_ecomorder.dto.OrderCreateDTO;
import com.example.javaee_ecomorder.dto.OrderQueryDTO;
import com.example.javaee_ecomorder.dto.OrderUpdateStatusDTO;
import com.example.javaee_ecomorder.utils.PageResult;
import com.example.javaee_ecomorder.vo.OrderDetailVO;
import com.example.javaee_ecomorder.vo.OrderListVO;

public interface OrderService {

    Long createOrder(OrderCreateDTO dto);

    OrderDetailVO getOrderDetail(Long orderId);

    PageResult<OrderListVO> pageQuery(OrderQueryDTO query);

    void updateOrderStatus(OrderUpdateStatusDTO dto);

    void cancelOrder(Long orderId);
}
