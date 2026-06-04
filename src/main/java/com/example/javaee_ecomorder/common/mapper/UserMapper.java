package com.example.javaee_ecomorder.common.mapper;

import com.example.javaee_ecomorder.common.dto.UserQueryDTO;
import com.example.javaee_ecomorder.common.entity.User;
import com.example.javaee_ecomorder.common.vo.UserListVO;
import com.example.javaee_ecomorder.common.vo.UserProfileVO;
import com.example.javaee_ecomorder.common.vo.UserWithOrdersVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    UserProfileVO selectUserWithProfile(@Param("userId") Long userId);

    UserWithOrdersVO selectUserWithOrders(@Param("userId") Long userId);

    User selectByUsername(@Param("username") String username);

    int countByUsername(@Param("username") String username);

    int countByUsernameExcludeId(@Param("username") String username, @Param("excludeId") Long excludeId);

    int insert(User user);

    int updateUser(User user);

    long countUserByQuery(@Param("query") UserQueryDTO query);

    List<UserListVO> selectUserPage(@Param("query") UserQueryDTO query, @Param("offset") int offset);

    int updatePassword(@Param("userId") Long userId, @Param("password") String password);

    int updateAccountStatus(@Param("userId") Long userId,
                            @Param("accountNonLocked") Boolean accountNonLocked,
                            @Param("failCount") Integer failCount);

    int updateLastLoginTime(@Param("userId") Long userId);
}
