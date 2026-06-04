package com.example.javaee_ecomorder.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PageResult<T> {
    private Long total;       // 总记录数
    private List<T> records;  // 当前页数�?
}