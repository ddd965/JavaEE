package com.example.javaee_ecomorder.controller;

import com.example.javaee_ecomorder.annotation.CacheRedis;
import com.example.javaee_ecomorder.annotation.OperateLog;
import com.example.javaee_ecomorder.annotation.PerfMonitor;
import com.example.javaee_ecomorder.annotation.RequireLogin;
import com.example.javaee_ecomorder.annotation.RequirePermission;
import com.example.javaee_ecomorder.dto.ProductAddDTO;
import com.example.javaee_ecomorder.dto.ProductQueryDTO;
import com.example.javaee_ecomorder.dto.ProductUpdateDTO;
import com.example.javaee_ecomorder.service.ProductService;
import com.example.javaee_ecomorder.utils.PageResult;
import com.example.javaee_ecomorder.utils.Result;
import com.example.javaee_ecomorder.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/products")   // 资源路径
@Validated                   // 开启方法级别参数校验
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 新增商品
     * POST /api/products
     * @param dto 商品新增DTO（含名称、价格、库存、分类）
     * @return 统一响应
     */
    @PostMapping
    public Result<Void> add(@RequestBody @Valid ProductAddDTO dto) {
        productService.addProduct(dto);
        return Result.success();
    }

    /**
     * 修改商品
     * PUT /api/products/{id}
     * @param id 商品ID（路径变量）
     * @param dto 商品修改DTO
     * @return 统一响应
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable @NotNull Long id,
                               @RequestBody @Valid ProductUpdateDTO dto) {
        dto.setId(id);   // 将路径中的id设置到DTO中
        productService.updateProduct(dto);
        return Result.success();
    }

    /**
     * 删除商品
     * DELETE /api/products/{id}
     * @param id 商品ID
     * @return 统一响应
     */
    @DeleteMapping("/{id}")
    @RequireLogin
    @RequirePermission("product:delete")
    @OperateLog(module = "product", type = "DELETE")
    @PerfMonitor(threshold = 500)
    public Result<Void> delete(@PathVariable @NotNull Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    /**
     * 根据ID查询商品详情（会走Redis缓存）
     * GET /api/products/{id}
     * @param id 商品ID
     * @return 商品VO
     */
    @GetMapping("/{id}")
    @RequireLogin
    @CacheRedis(key = "'product:' + #id", ttl = 1800)
    @PerfMonitor
    public Result<ProductVO> getById(@PathVariable @NotNull Long id) {
        ProductVO vo = productService.getProductById(id);
        return Result.success(vo);
    }

    /**
     * 条件分页查询商品列表
     * GET /api/products/page?pageNum=1&pageSize=5&name=手机&category=数码
     * @param query 查询条件封装（包含分页参数）
     * @return 分页结果（总记录数 + 当前页数据）
     */
    @GetMapping("/page")
    public Result<PageResult<ProductVO>> pageQuery(ProductQueryDTO query) {
        // 参数校验：pageNum和pageSize的最小值由前端保证，也可以在DTO中加@Min注解
        if (query.getPageNum() < 1) query.setPageNum(1);
        if (query.getPageSize() < 1) query.setPageSize(10);
        PageResult<ProductVO> page = productService.pageQuery(query);
        return Result.success(page);
    }
}