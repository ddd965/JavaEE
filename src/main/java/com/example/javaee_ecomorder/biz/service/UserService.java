package com.example.javaee_ecomorder.biz.service;

import com.example.javaee_ecomorder.common.dto.UserQueryDTO;
import com.example.javaee_ecomorder.common.dto.UserRegisterDTO;
import com.example.javaee_ecomorder.common.dto.UserUpdateDTO;
import com.example.javaee_ecomorder.common.utils.PageResult;
import com.example.javaee_ecomorder.common.entity.LoginLog;
import com.example.javaee_ecomorder.common.vo.OrderVO;
import com.example.javaee_ecomorder.common.vo.UserListVO;
import com.example.javaee_ecomorder.common.vo.UserProfileVO;

import java.util.List;

public interface UserService {

    UserProfileVO getUserWithProfile(Long userId);

    List<OrderVO> getUserOrders(Long userId);

    void updateUser(UserUpdateDTO dto);

    PageResult<UserListVO> pageQuery(UserQueryDTO query);

    void register(UserRegisterDTO dto);

    void updatePassword(Long userId, String oldPwd, String newPwd);

    void resetPassword(Long userId, String newPassword);

    void lockAccount(Long userId);

    void unlockAccount(Long userId);

    List<LoginLog> getLoginLogs(Long userId);
}
