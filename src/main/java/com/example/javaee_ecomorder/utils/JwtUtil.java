package com.example.javaee_ecomorder.utils;

import com.example.javaee_ecomorder.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret:JavaEE-Ecom-Order-Secret-Key-For-JWT-Signing-2024}")
    private String secret;

    @Value("${jwt.expiration:3600000}")
    private long expiration;

    public String generateToken(String username, List<String> authorities) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setSubject(username)
                .claim("authorities", authorities)
                .setIssuedAt(now)
                .setExpiration(expireAt)
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getAuthoritiesFromToken(String token) {
        Object authorities = parseClaims(token).get("authorities");
        if (authorities instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public long getExpiration() {
        return expiration;
    }

    public TokenInfo parseToken(String token) {
        if (!validateToken(token)) {
            throw new BusinessException("无效令牌");
        }
        Claims claims = parseClaims(token);
        TokenInfo info = new TokenInfo();
        info.setUsername(claims.getSubject());
        info.setExpireAt(claims.getExpiration().getTime());
        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) claims.get("authorities");
        info.setAuthorities(authorities);
        return info;
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey signingKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
            keyBytes = padded;
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Data
    public static class TokenInfo {
        private Long userId;
        private String username;
        private Long expireAt;
        private List<String> authorities;
    }
}
