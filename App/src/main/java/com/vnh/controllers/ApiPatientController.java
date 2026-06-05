package com.vnh.controllers;

import com.vnh.dto.ApiResponse;
import com.vnh.dto.AppointmentDto;
import com.vnh.dto.BillDto;
import com.vnh.dto.MedicalRecordDTO;
import com.vnh.dto.PatientDto;
import com.vnh.pojo.Appointments;
import com.vnh.pojo.Bills;
import com.vnh.services.AppointmentService;
import com.vnh.services.MedicalRecordService;
import com.vnh.services.PatientService;
import com.vnh.services.UserServices;
import com.vnh.utils.DtoMapper;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patients")
public class ApiPatientController {

    @Autowired private MedicalRecordService medicalRecordsService;
    @Autowired private AppointmentService appointmentService;
    @Autowired private PatientService patientService;
    @Autowired private UserServices userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PatientDto>> getMyPatientProfile(Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        PatientDto patient = patientService.getPatientByUsername(principal.getName());
        if (patient != null) return ResponseEntity.ok(ApiResponse.success(patient));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(404, "Khong tim thay ho so benh nhan"));
    }

    @GetMapping("/{patientId}/medical-records")
    public ResponseEntity<ApiResponse<List<MedicalRecordDTO>>> getMedicalRecords(@PathVariable Integer patientId) {
        List<MedicalRecordDTO> dtos = medicalRecordsService.getMedicalRecordDtosByPatientId(patientId);
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/my-appointments")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getMyAppointments(Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        PatientDto patient = patientService.getPatientByUsername(principal.getName());
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(404, "Khong tim thay ho so benh nhan"));
        }
        List<Appointments> appointments = appointmentService.getAppointmentsByPatientId(patient.getId());
        List<AppointmentDto> dtos = appointments.stream().map(DtoMapper::toAppointmentDto).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/my-bills")
    public ResponseEntity<ApiResponse<List<BillDto>>> getMyBills(Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        PatientDto patient = patientService.getPatientByUsername(principal.getName());
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(404, "Khong tim thay ho so benh nhan"));
        }
        List<Bills> bills = patientService.getBillsByPatientId(patient.getId());
        List<BillDto> dtos = bills.stream().map(DtoMapper::toBillDto).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/{patientId}/bills")
    public ResponseEntity<ApiResponse<List<BillDto>>> getBillsForPatient(@PathVariable Integer patientId, Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        PatientDto patient = patientService.getPatientByUsername(principal.getName());
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(404, "Khong tim thay ho so benh nhan"));
        }
        if (!patient.getId().equals(patientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(403, "Ban chi duoc xem hoa don cua chinh minh"));
        }
        List<Bills> bills = patientService.getBillsByPatientId(patientId);
        List<BillDto> dtos = bills.stream().map(DtoMapper::toBillDto).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }
}
