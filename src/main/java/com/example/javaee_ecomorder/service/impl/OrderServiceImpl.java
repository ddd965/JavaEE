package com.example.javaee_ecomorder.service.impl;

import com.example.javaee_ecomorder.common.OrderStatusEnum;
import com.example.javaee_ecomorder.dto.OrderCreateDTO;
import com.example.javaee_ecomorder.dto.OrderQueryDTO;
import com.example.javaee_ecomorder.dto.OrderUpdateStatusDTO;
import com.example.javaee_ecomorder.entity.Order;
import com.example.javaee_ecomorder.entity.OrderItem;
import com.example.javaee_ecomorder.entity.Product;
import com.example.javaee_ecomorder.exception.BusinessException;
import com.example.javaee_ecomorder.mapper.OrderItemMapper;
import com.example.javaee_ecomorder.mapper.OrderMapper;
import com.example.javaee_ecomorder.mapper.ProductMapper;
import com.example.javaee_ecomorder.service.OrderService;
import com.example.javaee_ecomorder.utils.OrderNoGenerator;
import com.example.javaee_ecomorder.utils.PageResult;
import com.example.javaee_ecomorder.vo.OrderDetailVO;
import com.example.javaee_ecomorder.vo.OrderListVO;
import com.example.javaee_ecomorder.vo.OrderVO;
import com.example.javaee_ecomorder.vo.OrderWithProductsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(OrderCreateDTO dto) {
        if (dto.getUserId() == null || dto.getUserId() <= 0) {
            throw new BusinessException("用户ID无效");
        }
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("订单商品不能为空");
        }
        for (OrderCreateDTO.OrderItemDTO item : dto.getItems()) {
            Product product = productMapper.selectByIdForUpdate(item.getProductId());
            if (product == null) {
                throw new BusinessException("商品ID " + item.getProductId() + " 不存在");
            }
            if (product.getStock() < item.getQuantity()) {
                throw new BusinessException("商品[" + product.getName() + "] 库存不足，当前库存：" + product.getStock());
            }
        }

        Order order = new Order();
        order.setOrderNo(OrderNoGenerator.generate());
        order.setUserId(dto.getUserId());
        order.setTotalPrice(calculateTotal(dto.getItems()));
        order.setStatus(0);
        order.setCreateTime(new Date());
        orderMapper.insert(order);

        for (OrderCreateDTO.OrderItemDTO item : dto.getItems()) {
            int rows = productMapper.decreaseStock(item.getProductId(), item.getQuantity());
            if (rows == 0) {
                throw new BusinessException("商品库存扣减失败，请重试");
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            BigDecimal price = productMapper.selectById(item.getProductId()).getPrice();
            orderItem.setSubtotal(price.multiply(BigDecimal.valueOf(item.getQuantity())));
            orderItemMapper.insert(orderItem);
        }

        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() == 0 || order.getStatus() == 1) {
            order.setStatus(4);
            orderMapper.updateById(order);
            List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
            for (OrderItem item : items) {
                productMapper.increaseStock(item.getProductId(), item.getQuantity());
            }
        } else {
            throw new BusinessException("当前订单状态不可取消");
        }
    }

    @Override
    public OrderDetailVO getOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        OrderWithProductsVO withProducts = orderMapper.selectOrderWithProducts(orderId);
        OrderDetailVO detail = new OrderDetailVO();
        detail.setOrderId(order.getId());
        detail.setOrderNo(order.getOrderNo());
        detail.setUserId(order.getUserId());
        detail.setTotalPrice(order.getTotalPrice());
        detail.setStatus(order.getStatus());
        detail.setStatusDesc(OrderStatusEnum.getDesc(order.getStatus()));
        detail.setCreateTime(order.getCreateTime());

        List<OrderDetailVO.OrderProductVO> products = new ArrayList<>();
        if (withProducts != null && withProducts.getProducts() != null) {
            for (com.example.javaee_ecomorder.vo.OrderProductVO p : withProducts.getProducts()) {
                if (p.getProductId() == null) {
                    continue;
                }
                OrderDetailVO.OrderProductVO row = new OrderDetailVO.OrderProductVO();
                row.setProductId(p.getProductId());
                row.setProductName(p.getProductName());
                row.setQuantity(p.getQuantity());
                row.setSubtotal(p.getSubtotal());
                Product prod = productMapper.selectById(p.getProductId());
                if (prod != null) {
                    row.setPrice(prod.getPrice());
                } else if (p.getQuantity() != null && p.getQuantity() > 0 && p.getSubtotal() != null) {
                    row.setPrice(p.getSubtotal().divide(BigDecimal.valueOf(p.getQuantity()), 2, RoundingMode.HALF_UP));
                }
                products.add(row);
            }
        }
        detail.setProducts(products);
        return detail;
    }

    @Override
    public PageResult<OrderListVO> pageQuery(OrderQueryDTO query) {
        int pageNum = query.getPageNum() == null ? 1 : query.getPageNum();
        int pageSize = query.getPageSize() == null ? 10 : query.getPageSize();
        int offset = (pageNum - 1) * pageSize;
        BigDecimal minPrice = query.getMinTotalPrice() == null ? null
                : BigDecimal.valueOf(query.getMinTotalPrice());
        BigDecimal maxPrice = query.getMaxTotalPrice() == null ? null
                : BigDecimal.valueOf(query.getMaxTotalPrice());
        List<OrderVO> orders = orderMapper.selectOrderPage(
                query.getUserId(),
                query.getStatus(),
                query.getOrderNo(),
                minPrice,
                maxPrice,
                query.getStartTime(),
                query.getEndTime(),
                offset,
                pageSize);
        long total = orderMapper.countOrderPage(
                query.getUserId(),
                query.getStatus(),
                query.getOrderNo(),
                minPrice,
                maxPrice,
                query.getStartTime(),
                query.getEndTime());
        List<OrderListVO> voList = orders.stream().map(this::toOrderListVO).collect(Collectors.toList());
        return new PageResult<>(total, voList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(OrderUpdateStatusDTO dto) {
        Order order = orderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() == 4) {
            throw new BusinessException("已取消订单不可修改状态");
        }
        order.setStatus(dto.getStatus());
        orderMapper.updateById(order);
    }

    private OrderListVO toOrderListVO(OrderVO o) {
        OrderListVO v = new OrderListVO();
        v.setOrderId(o.getOrderId());
        v.setOrderNo(o.getOrderNo());
        v.setTotalPrice(o.getTotalPrice());
        v.setCreateTime(o.getCreateTime());
        v.setStatus(o.getStatus());
        return v;
    }

    private BigDecimal calculateTotal(List<OrderCreateDTO.OrderItemDTO> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderCreateDTO.OrderItemDTO item : items) {
            BigDecimal price = productMapper.selectById(item.getProductId()).getPrice();
            total = total.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total;
    }
}
