package com.example.javaee_ecomorder.common.mapper;

import com.example.javaee_ecomorder.common.entity.UserProfile;
import com.example.javaee_ecomorder.common.vo.UserWithProfileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserProfileMapper {

    /**
     * 新增用户扩展信息
     */
    int insert(UserProfile userProfile);

    /**
     * 根据用户ID删除扩展信息
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 更新用户扩展信息
     */
    int update(UserProfile userProfile);

    /**
     * 根据用户ID查询扩展信息（单表）
     */
    UserProfile selectByUserId(@Param("userId") Long userId);

    /**
     * 一对一关联查询：查询用户及其扩展信息（JOIN user 表）
     * 返回自定�?VO，包�?user �?user_profile 字段
     */
    UserWithProfileVO selectUserWithProfile(@Param("userId") Long userId);
}
