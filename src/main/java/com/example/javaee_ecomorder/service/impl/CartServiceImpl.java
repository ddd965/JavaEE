package com.example.javaee_ecomorder.service.impl;

import com.example.javaee_ecomorder.dto.CartAddDTO;
import com.example.javaee_ecomorder.dto.CartUpdateDTO;
import com.example.javaee_ecomorder.entity.Cart;
import com.example.javaee_ecomorder.exception.BusinessException;
import com.example.javaee_ecomorder.mapper.CartMapper;
import com.example.javaee_ecomorder.service.CartService;
import com.example.javaee_ecomorder.vo.CartItemVO;
import com.example.javaee_ecomorder.vo.CartProductVO;
import com.example.javaee_ecomorder.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CartServiceImpl implements CartService {

    private static final String CART_CACHE_PREFIX = "cart:user:";

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private void evictCartCache(Long userId) {
        redisTemplate.delete(CART_CACHE_PREFIX + userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProduct(CartAddDTO dto) {
        Cart cart = new Cart();
        cart.setUserId(dto.getUserId());
        cart.setProductId(dto.getProductId());
        cart.setQuantity(dto.getQuantity());
        cart.setCreateTime(new Date());
        cartMapper.insert(cart);
        evictCartCache(dto.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearCart(Long userId) {
        cartMapper.deleteByUserId(userId);
        evictCartCache(userId);
    }

    @Override
    public void updateQuantity(CartUpdateDTO dto) {
        Cart cart = cartMapper.selectByUserIdAndProductId(dto.getUserId(), dto.getProductId());
        if (cart == null) {
            throw new BusinessException("购物车中不存在该商品");
        }
        if (dto.getQuantity() < 1) {
            throw new BusinessException("数量必须≥1");
        }
        cartMapper.updateQuantity(cart.getId(), dto.getQuantity());
        evictCartCache(dto.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeProduct(Long userId, Long productId) {
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null) {
            return;
        }
        cartMapper.deleteById(cart.getId());
        evictCartCache(userId);
    }

    @Override
    public CartVO getCart(Long userId) {
        String cacheKey = CART_CACHE_PREFIX + userId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof CartVO) {
            return (CartVO) cached;
        }
        List<CartProductVO> rows = cartMapper.selectCartProductsByUserId(userId);
        CartVO vo = buildCartVO(rows);
        redisTemplate.opsForValue().set(cacheKey, vo, 30, TimeUnit.MINUTES);
        return vo;
    }

    private CartVO buildCartVO(List<CartProductVO> rows) {
        if (rows == null || rows.isEmpty()) {
            CartVO empty = new CartVO();
            empty.setItems(List.of());
            empty.setTotalQuantity(0);
            empty.setTotalAmount(BigDecimal.ZERO);
            empty.setTotalSkuCount(0);
            return empty;
        }
        List<CartItemVO> items = new ArrayList<>();
        int totalQty = 0;
        BigDecimal totalAmt = BigDecimal.ZERO;
        for (CartProductVO cp : rows) {
            int qty = cp.getQuantity() != null ? cp.getQuantity() : 0;
            BigDecimal price = cp.getProductPrice() != null ? cp.getProductPrice() : BigDecimal.ZERO;
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(qty));

            CartItemVO item = new CartItemVO();
            item.setProductId(cp.getProductId());
            item.setProductName(cp.getProductName());
            item.setPrice(price);
            item.setImageUrl(cp.getProductImage());
            item.setQuantity(qty);
            item.setSubtotal(subtotal);
            item.setSelected(Boolean.TRUE);
            items.add(item);

            totalQty += qty;
            totalAmt = totalAmt.add(subtotal);
        }
        CartVO result = new CartVO();
        result.setItems(items);
        result.setTotalQuantity(totalQty);
        result.setTotalAmount(totalAmt);
        result.setTotalSkuCount(items.size());
        return result;
    }
}
