package com.vnh.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    @Test
    void generateAndValidateTokenRoundTripsUsername() throws Exception {
        String token = JwtUtils.generateToken("doctor1");

        assertNotNull(token);
        assertEquals("doctor1", JwtUtils.validateTokenAndGetUsername(token));
    }

    @Test
    void validateTokenReturnsNullForTamperedToken() throws Exception {
        String token = JwtUtils.generateToken("doctor1");
        String[] parts = token.split("\\.");
        String signature = parts[2];
        char first = signature.charAt(0);
        char replacement = first == 'a' ? 'b' : 'a';
        String tamperedSignature = replacement + signature.substring(1);
        String tampered = parts[0] + "." + parts[1] + "." + tamperedSignature;

        assertNull(JwtUtils.validateTokenAndGetUsername(tampered));
    }
}
