package com.example.javaee_ecomorder.biz.service;

import com.example.javaee_ecomorder.common.dto.CartAddDTO;
import com.example.javaee_ecomorder.common.dto.CartUpdateDTO;
import com.example.javaee_ecomorder.common.vo.CartVO;

public interface CartService {

    void addProduct(CartAddDTO dto);

    void clearCart(Long userId);

    void updateQuantity(CartUpdateDTO dto);

    void removeProduct(Long userId, Long productId);

    CartVO getCart(Long userId);
}