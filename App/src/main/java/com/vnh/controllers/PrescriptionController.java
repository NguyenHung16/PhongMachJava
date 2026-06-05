package com.vnh.controllers;

import com.vnh.dto.PrescriptionRequest;
import com.vnh.pojo.Prescriptions;
import com.vnh.services.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @PostMapping("/prescriptions/create")
    public ResponseEntity<?> createPrescription(@RequestBody PrescriptionRequest prescriptionRequest, Principal principal) {
        try {
            if (prescriptionRequest.getMedicalRecordId() <= 0) {
                return ResponseEntity.badRequest().body("ID hồ sơ bệnh án không hợp lệ.");
            }
            if (prescriptionRequest.getMedicines() == null || prescriptionRequest.getMedicines().isEmpty()) {
                return ResponseEntity.badRequest().body("Đơn thuốc phải có ít nhất một loại thuốc.");
            }

            Prescriptions newPrescription = prescriptionService.createPrescription(prescriptionRequest, principal);
            
            // Trả về đơn thuốc đã tạo với mã trạng thái 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(newPrescription);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi hệ thống: " + ex.getMessage());
        }
    }
}