package com.vnh.controllers;

import com.vnh.dto.*;
import com.vnh.pojo.*;
import com.vnh.services.*;
import com.vnh.utils.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin // Đảm bảo ReactJS có thể gọi API mà không bị chặn CORS
public class ApiDoctorController {

    @Autowired private DoctorService doctorService;
    @Autowired private SpecialtyService specialtyService;
    @Autowired private AppointmentService appointmentService;
    @Autowired private UserServices userService;
    @Autowired private PatientService patientService;

    @GetMapping("/specialties")
    public ResponseEntity<ApiResponse<List<SpecialtyDto>>> getSpecialties() {
        List<SpecialtyDto> dtos = specialtyService.getSpecialties().stream()
                .map(DtoMapper::toSpecialtyDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    // Đảm bảo đường dẫn là /api/doctors/my-appointments
    @GetMapping("/my-appointments")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getMyAppointments(Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        
        Users user = userService.getUserByUsername(principal.getName());
        if (user == null || !"DOCTOR".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(403, "Bạn không có quyền bác sĩ"));
        }

        Doctors doctor = doctorService.getDoctorById(user.getId());
        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(404, "Không tìm thấy hồ sơ bác sĩ"));
        }

        List<Appointments> list = appointmentService.getAppointmentsByDoctorId(doctor.getId());
        List<AppointmentDto> dtos = list.stream().map(DtoMapper::toAppointmentDto).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/patients")
    public ResponseEntity<ApiResponse<List<PatientDto>>> getMyPatients(Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        
        Users user = userService.getUserByUsername(principal.getName());
        if (user == null || !"DOCTOR".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(403, "Bạn không có quyền bác sĩ"));
        }
        Doctors doctor = doctorService.getDoctorById(user.getId());
        if (doctor == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        List<Patients> list = patientService.findByDoctorId(doctor.getId());
        List<PatientDto> dtos = list.stream().map(DtoMapper::toPatientDto).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DoctorDto>>> getDoctors(@RequestParam(required = false) Integer specialtyId) {
        List<Doctors> list;
        if (specialtyId != null) {
            list = doctorService.getDoctorsBySpecialtyId(specialtyId);
        } else {
            list = doctorService.getDoctors();
        }
        List<DoctorDto> dtos = list.stream().map(DtoMapper::toDoctorDto).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @PutMapping("/appointments/{appointmentId}/confirm")
    public ResponseEntity<ApiResponse<String>> confirmAppointment(@PathVariable int appointmentId) {
        Appointments app = appointmentService.getAppointmentById(appointmentId);
        if (app != null) {
            app.setStatus("CONFIRMED");
            appointmentService.saveAppointment(app);
            return ResponseEntity.ok(ApiResponse.success("Đã xác nhận lịch hẹn thành công"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(404, "Không tìm thấy lịch hẹn"));
    }
}
