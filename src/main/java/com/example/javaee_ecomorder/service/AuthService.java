package com.example.javaee_ecomorder.service;

import com.example.javaee_ecomorder.vo.LoginResultVO;

public interface AuthService {

    LoginResultVO login(String username, String password);

    void logout(String token);

    LoginResultVO refresh(String token);
}
