package com.example.javaee_ecomorder.mapper;

import com.example.javaee_ecomorder.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoginLogMapper {

    int insert(LoginLog log);

    List<LoginLog> selectByUserId(@Param("userId") Long userId);
}
