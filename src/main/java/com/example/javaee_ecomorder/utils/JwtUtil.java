package com.example.javaee_ecomorder.utils;

import com.example.javaee_ecomorder.exception.BusinessException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    @Value("${jwt.secret:JavaEE-Ecom-Order-Secret-Key}")
    private String secret;

    @Value("${jwt.expire-hours:2}")
    private int expireHours;

    public String generateToken(Long userId, String username) {
        long expireAt = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(expireHours);
        String payload = userId + "|" + username + "|" + expireAt;
        String signature = hmacSha256(payload);
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8)) + "." + signature;
    }

    public TokenInfo parseToken(String token) {
        if (token == null || !token.contains(".")) {
            throw new BusinessException("无效令牌");
        }
        String[] parts = token.split("\\.", 2);
        String payload = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
        if (!hmacSha256(payload).equals(parts[1])) {
            throw new BusinessException("无效令牌");
        }
        String[] fields = payload.split("\\|", 3);
        long expireAt = Long.parseLong(fields[2]);
        if (System.currentTimeMillis() > expireAt) {
            throw new BusinessException("令牌已过期");
        }
        TokenInfo info = new TokenInfo();
        info.setUserId(Long.parseLong(fields[0]));
        info.setUsername(fields[1]);
        info.setExpireAt(expireAt);
        return info;
    }

    public long getExpireMillis() {
        return TimeUnit.HOURS.toMillis(expireHours);
    }

    private String hmacSha256(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new BusinessException("令牌生成失败");
        }
    }

    @Data
    public static class TokenInfo {
        private Long userId;
        private String username;
        private Long expireAt;
    }
}
