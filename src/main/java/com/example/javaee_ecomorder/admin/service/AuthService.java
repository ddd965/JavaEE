package com.example.javaee_ecomorder.admin.service;

import com.example.javaee_ecomorder.common.vo.LoginResultVO;

public interface AuthService {

    LoginResultVO login(String username, String password);

    void logout(String token);

    LoginResultVO refresh(String token);
}
