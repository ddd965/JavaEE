package com.example.javaee_ecomorder.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 加密工具类
 * 提供 MD5、SHA-256、Base64、AES、BCrypt 等常用加密/解密方法
 */
public class EncryptUtil {

    private static final PasswordEncoder BCryptEncoder = new BCryptPasswordEncoder();
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /**
     * MD5 散列（不可逆）
     * @param plainText 原始字符串
     * @return 32位十六进制 MD5 值
     */
    public static String md5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(plainText.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不存在", e);
        }
    }

    /**
     * SHA-256 散列（不可逆）
     * @param plainText 原始字符串
     * @return 64位十六进制 SHA-256 值
     */
    public static String sha256(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(plainText.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256算法不存在", e);
        }
    }

    /**
     * Base64 编码
     * @param data 原始字节数组
     * @return Base64 字符串
     */
    public static String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Base64 解码
     * @param base64Str Base64 字符串
     * @return 原始字节数组
     */
    public static byte[] base64Decode(String base64Str) {
        return Base64.getDecoder().decode(base64Str);
    }

    /**
     * 生成 AES 密钥（256位）
     * @return Base64 编码的密钥字符串
     */
    public static String generateAESKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(AES_ALGORITHM);
            kg.init(256, new SecureRandom());
            SecretKey secretKey = kg.generateKey();
            return base64Encode(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("生成AES密钥失败", e);
        }
    }

    /**
     * AES 加密
     * @param plainText 明文
     * @param base64Key  Base64 编码的 AES 密钥（128/192/256位）
     * @return Base64 编码的密文
     */
    public static String aesEncrypt(String plainText, String base64Key) {
        try {
            byte[] keyBytes = base64Decode(base64Key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return base64Encode(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES加密失败", e);
        }
    }

    /**
     * AES 解密
     * @param cipherText Base64 编码的密文
     * @param base64Key  Base64 编码的 AES 密钥
     * @return 明文字符串
     */
    public static String aesDecrypt(String cipherText, String base64Key) {
        try {
            byte[] keyBytes = base64Decode(base64Key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decrypted = cipher.doFinal(base64Decode(cipherText));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES解密失败", e);
        }
    }

    /**
     * BCrypt 密码加密（推荐用于用户密码存储）
     * @param rawPassword 原始密码
     * @return 加密后的哈希值（含盐）
     */
    public static String bcryptEncode(String rawPassword) {
        return BCryptEncoder.encode(rawPassword);
    }

    /**
     * BCrypt 密码验证
     * @param rawPassword      原始密码（用户输入）
     * @param encodedPassword 存储的加密哈希
     * @return 是否匹配
     */
    public static boolean bcryptMatch(String rawPassword, String encodedPassword) {
        return BCryptEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 字节数组转十六进制字符串
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    // 测试方法（可选）
    public static void main(String[] args) {
        String password = "123456";
        // BCrypt 测试
        String encoded = bcryptEncode(password);
        System.out.println("BCrypt 加密结果: " + encoded);
        System.out.println("密码验证: " + bcryptMatch(password, encoded));

        // AES 测试
        String key = generateAESKey();
        System.out.println("AES 密钥: " + key);
        String encrypted = aesEncrypt("Hello World", key);
        System.out.println("AES 加密: " + encrypted);
        String decrypted = aesDecrypt(encrypted, key);
        System.out.println("AES 解密: " + decrypted);

        // MD5 / SHA-256 测试
        System.out.println("MD5: " + md5(password));
        System.out.println("SHA-256: " + sha256(password));
    }
}
