package com.example.javaee_ecomorder.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码编码工具类（纯静态方法，不依赖Spring Security容器）
 */
public final class PasswordUtil {

    private static final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();

    private PasswordUtil() {
    }

    /**
     * 使用 BCrypt 加密密码
     */
    public static String encode(String rawPassword) {
        return BCRYPT.encode(rawPassword);
    }

    /**
     * 验证密码，兼容历史 MD5
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (encodedPassword == null) {
            return false;
        }
        if (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$")) {
            return BCRYPT.matches(rawPassword, encodedPassword);
        }
        return encodedPassword.equals(EncryptUtil.md5(rawPassword));
    }

    /**
     * 判断密码是否已使用 BCrypt 编码
     */
    public static boolean isBcrypt(String encodedPassword) {
        return encodedPassword != null
                && (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$"));
    }
}
