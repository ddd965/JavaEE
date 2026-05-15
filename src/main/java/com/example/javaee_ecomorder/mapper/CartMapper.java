package com.example.javaee_ecomorder.mapper;

import com.example.javaee_ecomorder.entity.Cart;
import com.example.javaee_ecomorder.vo.CartProductVO;
import com.example.javaee_ecomorder.vo.UserCartVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CartMapper {

    /**
     * 新增购物车记录（如果已存在则更新数量，由应用层控制或使用 on duplicate key）
     */
    int insert(Cart cart);

    /**
     * 更新购物车商品数量
     */
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 删除购物车中某个商品
     */
    int deleteById(@Param("id") Long id);

    /**
     * 清空用户购物车
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和商品ID查询购物车项（用于判断是否存在）
     */
    Cart selectByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 根据购物车ID查询
     */
    Cart selectById(@Param("id") Long id);

    /**
     * 查询用户购物车中的所有商品（带商品详情，多表关联查询 - 一对一/多对一）
     * 返回 CartProductVO 列表，每个VO包含购物车信息和关联的商品信息
     */
    List<CartProductVO> selectCartProductsByUserId(@Param("userId") Long userId);

    /**
     * 查询用户购物车总记录数
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 批量删除购物车项（用于下单后清空选中的购物车）
     */
    int batchDelete(@Param("ids") List<Long> ids);

    /**
     * 查询用户完整购物车信息（包含用户姓名和商品列表，一对多映射）
     * 该方法通过 resultMap 将用户信息和其购物车商品列表聚合为 UserCartVO
     */
    UserCartVO selectUserCartWithItems(@Param("userId") Long userId);
}
