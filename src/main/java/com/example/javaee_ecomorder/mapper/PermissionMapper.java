package com.example.javaee_ecomorder.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper {

    List<String> selectPermissionNamesByUserId(@Param("userId") Long userId);
}
