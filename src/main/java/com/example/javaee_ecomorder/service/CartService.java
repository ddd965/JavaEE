package com.example.javaee_ecomorder.service;

import com.example.javaee_ecomorder.dto.CartAddDTO;
import com.example.javaee_ecomorder.dto.CartUpdateDTO;
import com.example.javaee_ecomorder.vo.CartVO;

public interface CartService {

    void addProduct(CartAddDTO dto);

    void updateQuantity(CartUpdateDTO dto);

    void removeProduct(Long userId, Long productId);

    CartVO getCart(Long userId);
}
