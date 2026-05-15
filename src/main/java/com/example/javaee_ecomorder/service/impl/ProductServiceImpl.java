package com.example.javaee_ecomorder.service.impl;

import com.example.javaee_ecomorder.dto.ProductAddDTO;
import com.example.javaee_ecomorder.dto.ProductQueryDTO;
import com.example.javaee_ecomorder.dto.ProductUpdateDTO;
import com.example.javaee_ecomorder.entity.Product;
import com.example.javaee_ecomorder.exception.BusinessException;
import com.example.javaee_ecomorder.mapper.ProductMapper;
import com.example.javaee_ecomorder.service.ProductService;
import com.example.javaee_ecomorder.utils.PageResult;
import com.example.javaee_ecomorder.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    private RedisTemplate<String, Object> redisTemplate;

    private static final String PRODUCT_CACHE_PREFIX = "product:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProduct(ProductAddDTO dto) {
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("商品价格必须大于0");
        }
        if (dto.getStock() < 0) {
            throw new BusinessException("库存不能为负数");
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
            throw new BusinessException("商品不存在");
        }
        productMapper.deleteById(id);
        redisTemplate.delete(PRODUCT_CACHE_PREFIX + id);
    }

    @Override
    @Transactional
    public void updateProduct(ProductUpdateDTO dto) {
        Product exist = productMapper.selectById(dto.getId());
        if (exist == null) {
            throw new BusinessException("商品不存在");
        }
        if (dto.getPrice() != null && dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("价格必须大于0");
        }
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        productMapper.updateById(product);
        redisTemplate.delete(PRODUCT_CACHE_PREFIX + dto.getId());
    }

    @Override
    public ProductVO getProductById(Long id) {
        String cacheKey = PRODUCT_CACHE_PREFIX + id;
        ProductVO vo = (ProductVO) redisTemplate.opsForValue().get(cacheKey);
        if (vo != null) {
            log.info("命中缓存：商品{}", id);
            return vo;
        }
        Product product = productMapper.selectById(id);
        if (product == null) {
            redisTemplate.opsForValue().set(cacheKey, null, 5, TimeUnit.MINUTES);
            throw new BusinessException("商品不存在");
        }
        vo = new ProductVO(product);
        redisTemplate.opsForValue().set(cacheKey, vo, 30, TimeUnit.MINUTES);
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
            throw new BusinessException("状态值只能为0（下架）或1（上架）");
        }
        productMapper.batchUpdateStatus(ids, status);
        for (Long id : ids) {
            redisTemplate.delete(PRODUCT_CACHE_PREFIX + id);
        }
    }
}
