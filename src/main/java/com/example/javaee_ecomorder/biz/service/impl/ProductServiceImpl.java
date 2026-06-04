package com.example.javaee_ecomorder.biz.service.impl;

import com.example.javaee_ecomorder.common.basic.CacheKeyPrefix;
import com.example.javaee_ecomorder.common.dto.ProductAddDTO;
import com.example.javaee_ecomorder.common.dto.ProductQueryDTO;
import com.example.javaee_ecomorder.common.dto.ProductUpdateDTO;
import com.example.javaee_ecomorder.common.entity.OrderItem;
import com.example.javaee_ecomorder.common.entity.Product;
import com.example.javaee_ecomorder.common.exception.BusinessException;
import com.example.javaee_ecomorder.common.mapper.OrderItemMapper;
import com.example.javaee_ecomorder.common.mapper.OrderMapper;
import com.example.javaee_ecomorder.common.mapper.ProductMapper;
import com.example.javaee_ecomorder.biz.service.ProductService;
import com.example.javaee_ecomorder.common.utils.PageResult;
import com.example.javaee_ecomorder.common.utils.RedisCacheUtil;
import com.example.javaee_ecomorder.common.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProduct(ProductAddDTO dto) {
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("商品价格必须大于0");
        }
        if (dto.getStock() < 0) {
            throw new BusinessException("库存不能为负�?);
        }
        if (!StringUtils.hasText(dto.getName()) || dto.getName().length() > 100) {
            throw new BusinessException("商品名称不能为空且长度≤100");
        }
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        product.setStatus(1);
        productMapper.insert(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存�?);
        }

        // 检查是否有订单商品引用该商�?
        List<OrderItem> orderItems = orderItemMapper.selectByProductId(id);
        if (orderItems != null && !orderItems.isEmpty()) {
            throw new BusinessException("该商品已被订单引用，无法删除");
        }

        productMapper.deleteById(id);
        redisCacheUtil.delete(CacheKeyPrefix.PRODUCT + id);
    }

    @Override
    @Transactional
    public void updateProduct(ProductUpdateDTO dto) {
        Product exist = productMapper.selectById(dto.getId());
        if (exist == null) {
            throw new BusinessException("商品不存�?);
        }
        if (dto.getPrice() != null && dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("价格必须大于0");
        }
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        productMapper.updateById(product);
        redisCacheUtil.delete(CacheKeyPrefix.PRODUCT + dto.getId());
    }

    @Override
    public ProductVO getProductById(Long id) {
        String cacheKey = CacheKeyPrefix.PRODUCT + id;
        ProductVO vo = (ProductVO) redisCacheUtil.get(cacheKey);
        if (vo != null) {
            log.info("命中缓存：商品{}", id);
            return vo;
        }
        Product product = productMapper.selectById(id);
        if (product == null) {
            redisCacheUtil.set(cacheKey, null, 5, TimeUnit.MINUTES);
            throw new BusinessException("商品不存�?);
        }
        vo = new ProductVO(product);
        redisCacheUtil.set(cacheKey, vo, 30, TimeUnit.MINUTES);
        return vo;
    }

    @Override
    public PageResult<ProductVO> pageQuery(ProductQueryDTO query) {
        if (query.getPageNum() == null || query.getPageNum() < 1) {
            query.setPageNum(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        int offset = (query.getPageNum() - 1) * query.getPageSize();
        List<Product> products = productMapper.selectByCondition(
                query.getName(), query.getCategory(), query.getMinPrice(),
                query.getStatus(), offset, query.getPageSize()
        );
        long total = productMapper.countByCondition(
                query.getName(), query.getCategory(), query.getMinPrice(), query.getStatus()
        );
        List<ProductVO> voList = products.stream()
                .map(ProductVO::new)
                .collect(Collectors.toList());
        return new PageResult<>(total, voList);
    }

    @Override
    @Transactional
    public void batchUpdateStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("商品ID列表不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值只能为0（下架）�?（上架）");
        }
        productMapper.batchUpdateStatus(ids, status);
        for (Long id : ids) {
            redisCacheUtil.delete(CacheKeyPrefix.PRODUCT + id);
        }
    }
}