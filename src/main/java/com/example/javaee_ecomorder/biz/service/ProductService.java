package com.example.javaee_ecomorder.biz.service;

import com.example.javaee_ecomorder.common.dto.ProductAddDTO;
import com.example.javaee_ecomorder.common.dto.ProductQueryDTO;
import com.example.javaee_ecomorder.common.dto.ProductUpdateDTO;
import com.example.javaee_ecomorder.common.utils.PageResult;
import com.example.javaee_ecomorder.common.vo.ProductVO;

import java.util.List;

public interface ProductService {

    void addProduct(ProductAddDTO dto);

    void deleteProduct(Long id);

    void updateProduct(ProductUpdateDTO dto);

    ProductVO getProductById(Long id);

    PageResult<ProductVO> pageQuery(ProductQueryDTO query);

    void batchUpdateStatus(List<Long> ids, Integer status);
}
