package com.example.javaee_ecomorder.mapper;

import com.example.javaee_ecomorder.entity.Order;
import com.example.javaee_ecomorder.vo.OrderVO;
import com.example.javaee_ecomorder.vo.OrderWithProductsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Mapper
public interface OrderMapper {

    int insert(Order order);

    int updateById(Order order);

    Order selectById(@Param("id") Long id);

    OrderWithProductsVO selectOrderWithProducts(@Param("orderId") Long orderId);

    List<OrderVO> selectOrderPage(@Param("userId") Long userId,
                                  @Param("status") Integer status,
                                  @Param("orderNo") String orderNo,
                                  @Param("minTotalPrice") BigDecimal minTotalPrice,
                                  @Param("maxTotalPrice") BigDecimal maxTotalPrice,
                                  @Param("startTime") Date startTime,
                                  @Param("endTime") Date endTime,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);

    long countOrderPage(@Param("userId") Long userId,
                        @Param("status") Integer status,
                        @Param("orderNo") String orderNo,
                        @Param("minTotalPrice") BigDecimal minTotalPrice,
                        @Param("maxTotalPrice") BigDecimal maxTotalPrice,
                        @Param("startTime") Date startTime,
                        @Param("endTime") Date endTime);
}
