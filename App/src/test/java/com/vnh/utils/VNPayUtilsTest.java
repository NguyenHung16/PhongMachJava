package com.vnh.utils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VNPayUtilsTest {

    @Test
    void hashAllFieldsReturnsHexDigest() {
        Map<String, String> fields = new HashMap<>();
        fields.put("vnp_Amount", "100000");
        fields.put("vnp_OrderInfo", "Thanh toan");

        String hash = VNPayUtils.hashAllFields(fields, "secret");

        assertNotNull(hash);
        assertFalse(hash.isBlank());
        assertTrue(hash.matches("[0-9a-f]+"));
    }

    @Test
    void hmacSha512ReturnsEmptyStringWhenInputInvalid() {
        assertEquals("", VNPayUtils.hmacSHA512(null, "data"));
        assertEquals("", VNPayUtils.hmacSHA512("key", null));
    }

    @Test
    void getIpAddressUsesForwardedHeaderOrRemoteAddress() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-FORWARDED-FOR")).thenReturn("10.0.0.1");

        assertEquals("10.0.0.1", VNPayUtils.getIpAddress(request));

        when(request.getHeader("X-FORWARDED-FOR")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        assertEquals("127.0.0.1", VNPayUtils.getIpAddress(request));
    }

    @Test
    void getIpAddressReturnsInvalidMessageOnException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-FORWARDED-FOR")).thenThrow(new RuntimeException("boom"));

        assertTrue(VNPayUtils.getIpAddress(request).startsWith("Invalid IP:"));
    }
}
