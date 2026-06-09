package com.example.javaee_ecomorder.common.security;

import com.example.javaee_ecomorder.common.utils.EncryptUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 兼容历史 MD5 密码，新密码使用 BCrypt 加密
 */
@Component
public class EcomPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        return bcrypt.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null) {
            return false;
        }
        if (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$")) {
            return bcrypt.matches(rawPassword, encodedPassword);
        }
        return encodedPassword.equals(EncryptUtil.md5(rawPassword.toString()));
    }

    public boolean isBcrypt(String encodedPassword) {
        return encodedPassword != null
                && (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$"));
    }
}
