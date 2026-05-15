package com.example.javaee_ecomorder.service;

import com.example.javaee_ecomorder.dto.ProductAddDTO;
import com.example.javaee_ecomorder.dto.ProductQueryDTO;
import com.example.javaee_ecomorder.dto.ProductUpdateDTO;
import com.example.javaee_ecomorder.utils.PageResult;
import com.example.javaee_ecomorder.vo.ProductVO;

import java.util.List;

public interface ProductService {

    void addProduct(ProductAddDTO dto);

    void deleteProduct(Long id);

    void updateProduct(ProductUpdateDTO dto);

    ProductVO getProductById(Long id);

    PageResult<ProductVO> pageQuery(ProductQueryDTO query);

    void batchUpdateStatus(List<Long> ids, Integer status);
}
