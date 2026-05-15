package com.example.javaee_ecomorder.mapper;

import com.example.javaee_ecomorder.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OrderItemMapper {

    /**
     * 新增订单明细
     * @param orderItem 订单明细对象
     * @return 影响行数
     */
    int insert(OrderItem orderItem);

    /**
     * 批量新增订单明细（用于一次下单多个商品）
     * @param list 订单明细列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<OrderItem> list);

    /**
     * 根据ID修改订单明细（一般较少单独修改，预留）
     * @param orderItem 修改后的明细
     * @return 影响行数
     */
    int updateById(OrderItem orderItem);

    /**
     * 根据ID删除订单明细
     * @param id 明细ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据订单ID删除该订单下所有明细（用于取消订单时清理）
     * @param orderId 订单ID
     * @return 影响行数
     */
    int deleteByOrderId(Long orderId);

    /**
     * 根据ID查询单个订单明细
     * @param id 明细ID
     * @return 订单明细实体
     */
    OrderItem selectById(Long id);

    /**
     * 根据订单ID查询所有明细（一对多中的“多”）
     * @param orderId 订单ID
     * @return 明细列表
     */
    List<OrderItem> selectByOrderId(Long orderId);

    /**
     * 根据商品ID查询所有包含该商品的明细（用于统计等）
     * @param productId 商品ID
     * @return 明细列表
     */
    List<OrderItem> selectByProductId(Long productId);
}