package com.example.javaee_ecomorder.security;

import com.example.javaee_ecomorder.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void generateAndValidateToken() {
        List<String> authorities = List.of("ROLE_USER", "product:query");
        String token = jwtUtil.generateToken("zhangsan", authorities);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals("zhangsan", jwtUtil.getUsernameFromToken(token));
        assertTrue(jwtUtil.getAuthoritiesFromToken(token).contains("product:query"));
    }
}
