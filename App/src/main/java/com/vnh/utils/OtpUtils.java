package com.vnh.utils;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class OtpUtils {

    // Lưu OTP: email -> {otp, thời gian hết hạn}
    private Map<String, long[]> otpStorage = new HashMap<>();
    private static final long OTP_EXPIRY = 5 * 60 * 1000; // 5 phút

    // Tạo OTP 6 số
    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        long expiry = System.currentTimeMillis() + OTP_EXPIRY;
        otpStorage.put(email, new long[]{Long.parseLong(otp), expiry});
        return otp;
    }

    // Xác nhận OTP
    public boolean verifyOtp(String email, String otp) {
        if (!otpStorage.containsKey(email)) return false;
        long[] data = otpStorage.get(email);
        boolean isValid = data[0] == Long.parseLong(otp)
                       && System.currentTimeMillis() < data[1];
        if (isValid) otpStorage.remove(email); // xóa sau khi dùng
        return isValid;
    }
}