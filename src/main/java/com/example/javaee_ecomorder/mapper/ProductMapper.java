package com.example.javaee_ecomorder.mapper;

import com.example.javaee_ecomorder.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ProductMapper {

    // 插入商品（返回自增主键自动回填到 Product.id）
    int insert(Product product);

    // 根据主键更新
    int updateById(Product product);

    // 根据主键删除（物理删除）
    int deleteById(@Param("id") Long id);

    // 根据主键查询
    Product selectById(@Param("id") Long id);

    Product selectByIdForUpdate(@Param("id") Long id);

    int decreaseStock(@Param("id") Long id, @Param("quantity") int quantity);

    int increaseStock(@Param("id") Long id, @Param("quantity") int quantity);

    // 条件查询（名称模糊、分类、最低价、状态）用于分页
    List<Product> selectByCondition(@Param("name") String name,
                                    @Param("category") String category,
                                    @Param("minPrice") BigDecimal minPrice,
                                    @Param("status") Integer status,
                                    @Param("offset") int offset,
                                    @Param("limit") int limit);

    // 条件查询总记录数（用于分页）
    long countByCondition(@Param("name") String name,
                          @Param("category") String category,
                          @Param("minPrice") BigDecimal minPrice,
                          @Param("status") Integer status);

    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);
}