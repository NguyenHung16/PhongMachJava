package com.vnh.controllers;

import com.vnh.dto.ApiResponse;
import com.vnh.dto.MedicalRecordDTO;
import com.vnh.dto.PrescriptionRequest;
import com.vnh.services.MedicalRecordService;
import com.vnh.services.PrescriptionService;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiMedicalRecordController {

    @Autowired private MedicalRecordService medicalRecordService;
    @Autowired private PrescriptionService prescriptionService;

    // 1. Bác sĩ tạo bệnh án (Gửi chẩn đoán)
    @PostMapping("/medical-records")
    public ResponseEntity<ApiResponse<MedicalRecordDTO>> createRecord(@RequestBody MedicalRecordDTO recordDto) {
        try {
            MedicalRecordDTO created = medicalRecordService.createMedicalRecord(recordDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    // 2. Bác sĩ kê đơn thuốc (MỚI)
    @PostMapping("/prescriptions")
    public ResponseEntity<ApiResponse<String>> createPrescription(@RequestBody PrescriptionRequest req, Principal principal) {
        try {
            prescriptionService.createPrescription(req, principal);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Kê đơn thuốc thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    // 3. Xem lịch sử bệnh án (Bác sĩ xem cho bệnh nhân)
    @GetMapping("/medical-records/by-patient/{patientId}")
    public ResponseEntity<ApiResponse<List<MedicalRecordDTO>>> getPatientRecords(@PathVariable int patientId) {
        List<MedicalRecordDTO> records = medicalRecordService.getMedicalRecordDtosByPatientId(patientId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }
}
