package com.example.javaee_ecomorder.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EcomPasswordEncoderTest {

    private final EcomPasswordEncoder encoder = new EcomPasswordEncoder();

    @Test
    void matchesBcryptPassword() {
        String encoded = encoder.encode("123456");
        assertTrue(encoder.matches("123456", encoded));
    }

    @Test
    void matchesLegacyMd5Password() {
        assertTrue(encoder.matches("123456", "e10adc3949ba59abbe56e057f20f883e"));
    }
}
