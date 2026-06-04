package com.example.javaee_ecomorder.common.mapper;

import com.example.javaee_ecomorder.common.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {

    List<Role> selectByUserId(@Param("userId") Long userId);
}
