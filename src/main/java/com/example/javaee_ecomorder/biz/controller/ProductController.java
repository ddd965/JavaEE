package com.example.javaee_ecomorder.biz.controller;

import com.example.javaee_ecomorder.common.annotation.RequireLogin;
import com.example.javaee_ecomorder.common.annotation.RequirePermission;
import com.example.javaee_ecomorder.common.dto.ProductAddDTO;
import com.example.javaee_ecomorder.common.dto.ProductQueryDTO;
import com.example.javaee_ecomorder.common.dto.ProductUpdateDTO;
import com.example.javaee_ecomorder.biz.service.ProductService;
import com.example.javaee_ecomorder.common.utils.PageResult;
import com.example.javaee_ecomorder.common.utils.Result;
import com.example.javaee_ecomorder.common.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/products")
@Validated
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    @RequireLogin
    @RequirePermission("product:add")
    public Result<Void> add(@RequestBody @Valid ProductAddDTO dto) {
        productService.addProduct(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    @RequireLogin
    @RequirePermission("product:update")
    public Result<Void> update(@PathVariable @NotNull Long id,
                               @RequestBody @Valid ProductUpdateDTO dto) {
        dto.setId(id);
        productService.updateProduct(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @RequireLogin
    @RequirePermission("product:delete")
    public Result<Void> delete(@PathVariable @NotNull Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<ProductVO> getById(@PathVariable @NotNull Long id) {
        ProductVO vo = productService.getProductById(id);
        return Result.success(vo);
    }

    @GetMapping("/page")
    public Result<PageResult<ProductVO>> pageQuery(ProductQueryDTO query) {
        if (query.getPageNum() < 1) query.setPageNum(1);
        if (query.getPageSize() < 1) query.setPageSize(10);
        PageResult<ProductVO> page = productService.pageQuery(query);
        return Result.success(page);
    }
}
