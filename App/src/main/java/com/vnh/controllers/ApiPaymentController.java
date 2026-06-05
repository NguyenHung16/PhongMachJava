package com.vnh.controllers;

import com.vnh.dto.ApiResponse;
import com.vnh.dto.AppointmentDto;
import com.vnh.dto.BillDto;
import com.vnh.pojo.Appointments;
import com.vnh.pojo.Bills;
import com.vnh.repositories.BillRepository;
import com.vnh.services.PaymentService;
import com.vnh.utils.DtoMapper;
import com.vnh.utils.VNPayUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class ApiPaymentController {

    @Autowired private PaymentService paymentService;
    @Autowired private BillRepository billRepository;
    @Autowired private Environment env;

    @GetMapping("/vnpay-url/{billId}")
    public ResponseEntity<ApiResponse<String>> getVNPayUrl(@PathVariable int billId, HttpServletRequest request) {
        try {
            Bills bill = billRepository.getBillById(billId);
            if (bill == null) return ResponseEntity.badRequest().body(ApiResponse.error(400, "Không tìm thấy hóa đơn"));

            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_OrderInfo = "Thanh toan hoa don kham benh #" + billId;
            String vnp_TxnRef = String.valueOf(billId) + "_" + System.currentTimeMillis();
            String vnp_IpAddr = VNPayUtils.getIpAddress(request);
            String vnp_TmnCode = env.getProperty("vnp.tmn_code");
            
            long amount = bill.getTotalAmount().longValue() * 100;
            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", env.getProperty("vnp.return_url"));
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List fieldNames = new ArrayList(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            
            String queryUrl = query.toString();
            String vnp_SecureHash = VNPayUtils.hmacSHA512(env.getProperty("vnp.hash_secret"), hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = env.getProperty("vnp.pay_url") + "?" + queryUrl;

            return ResponseEntity.ok(ApiResponse.success(paymentUrl));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error(500, e.getMessage()));
        }
    }

    @GetMapping("/vnpay-url/by-appointment/{appointmentId}")
    public ResponseEntity<ApiResponse<String>> getVNPayUrlByAppointment(@PathVariable int appointmentId, HttpServletRequest request) {
        Bills bill = paymentService.createBill(appointmentId);
        if (bill == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Không tìm thấy hóa đơn"));
        }
        return getVNPayUrl(bill.getId(), request);
    }

    @PostMapping("/appointments/{appointmentId}/deposit")
    public ResponseEntity<ApiResponse<AppointmentDto>> payAppointmentDeposit(@PathVariable int appointmentId) {
        Appointments appointment = paymentService.processAppointmentDeposit(appointmentId);
        if (appointment == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(404, "Khong tim thay lich hen"));
        }
        return ResponseEntity.ok(ApiResponse.success(DtoMapper.toAppointmentDto(appointment)));
    }

    @PostMapping("/bills/{billId}/pay")
    public ResponseEntity<ApiResponse<BillDto>> payBill(@PathVariable int billId) {
        boolean paid = paymentService.processPayment(billId, "MOCK");
        if (!paid) {
            return ResponseEntity.badRequest().body(ApiResponse.error(404, "Khong tim thay hoa don"));
        }
        Bills bill = billRepository.getBillById(billId);
        return ResponseEntity.ok(ApiResponse.success(DtoMapper.toBillDto(bill)));
    }
}
