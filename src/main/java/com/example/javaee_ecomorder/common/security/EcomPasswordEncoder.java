package com.example.javaee_ecomorder.common.security;

import com.example.javaee_ecomorder.common.utils.PasswordUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security 容器专用：委托 PasswordUtil，兼容 Spring Security PasswordEncoder 接口
 */
public class EcomPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return PasswordUtil.encode(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return PasswordUtil.matches(rawPassword.toString(), encodedPassword);
    }

    public boolean isBcrypt(String encodedPassword) {
        return PasswordUtil.isBcrypt(encodedPassword);
    }
}
