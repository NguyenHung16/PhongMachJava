package com.vnh.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OtpUtilsTest {

    @Test
    void generatedOtpHasSixDigitsAndCanBeVerifiedOnce() {
        OtpUtils utils = new OtpUtils();

        String otp = utils.generateOtp("a@test.com");

        assertTrue(otp.matches("\\d{6}"));
        assertTrue(utils.verifyOtp("a@test.com", otp));
        assertFalse(utils.verifyOtp("a@test.com", otp));
    }

    @Test
    void verifyOtpReturnsFalseForMissingOrWrongOtp() {
        OtpUtils utils = new OtpUtils();

        assertFalse(utils.verifyOtp("missing@test.com", "123456"));
        String otp = utils.generateOtp("a@test.com");
        String wrongOtp = otp.equals("000000") ? "000001" : "000000";
        assertFalse(utils.verifyOtp("a@test.com", wrongOtp));
    }
}
