package com.example.javaee_ecomorder.controller;

import com.example.javaee_ecomorder.dto.CartAddDTO;
import com.example.javaee_ecomorder.dto.CartUpdateDTO;
import com.example.javaee_ecomorder.service.CartService;
import com.example.javaee_ecomorder.utils.Result;
import com.example.javaee_ecomorder.vo.CartVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public Result<Void> addToCart(@RequestBody @Valid CartAddDTO dto) {
        cartService.addProduct(dto);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> updateQuantity(@RequestBody @Valid CartUpdateDTO dto) {
        cartService.updateQuantity(dto);
        return Result.success();
    }

    @DeleteMapping("/remove")
    public Result<Void> removeProduct(@RequestParam Long userId, @RequestParam Long productId) {
        cartService.removeProduct(userId, productId);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<CartVO> getCart(@RequestParam Long userId) {
        CartVO cart = cartService.getCart(userId);
        return Result.success(cart);
    }
    @DeleteMapping("/clear")
    public Result<Void> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        return Result.success();
    }
}