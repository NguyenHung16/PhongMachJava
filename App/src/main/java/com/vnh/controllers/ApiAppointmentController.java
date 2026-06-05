package com.vnh.controllers;

import com.vnh.dto.ApiResponse;
import com.vnh.dto.AppointmentDto;
import com.vnh.pojo.Appointments;
import com.vnh.pojo.Doctors;
import com.vnh.pojo.Patients;
import com.vnh.payload.AppointmentRequest;
import com.vnh.services.AppointmentService;
import com.vnh.services.DoctorService;
import com.vnh.services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
public class ApiAppointmentController {

    @Autowired private AppointmentService appointmentService;
    @Autowired private DoctorService doctorService;
    @Autowired private PatientService patientService;

    // Đặt lịch khám
    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentDto>> createAppointment(@RequestBody AppointmentRequest req) {
        try {
            Doctors doctor = doctorService.getDoctorById(req.getDoctorId());
            Patients patient = patientService.getPatientById(req.getPatientId());

            if (doctor == null || patient == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, "Bác sĩ hoặc Bệnh nhân không tồn tại"));
            }

            Appointments app = new Appointments();
            app.setDoctorId(doctor);
            app.setPatientId(patient);
            app.setReason(req.getReason());
            app.setAppointmentDate(req.getAppointmentDate());
            app.setAppointmentTime(req.getAppointmentTime());
            app.setStatus("AWAITING_DEPOSIT");
            app.setCreatedAt(new Date());

            appointmentService.saveAppointment(app);

            AppointmentDto dto = AppointmentDto.builder()
                    .id(app.getId())
                    .patientName(patient.getUserId().getFullName())
                    .patientId(patient.getId())
                    .doctorName(doctor.getUserId().getFullName())
                    .appointmentDate(app.getAppointmentDate())
                    .status(app.getStatus())
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(dto));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error(500, e.getMessage()));
        }
    }

    // Lấy danh sách lịch hẹn của bệnh nhân
    @GetMapping("/patient/{id}")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getByPatient(@PathVariable int id) {
        List<Appointments> list = appointmentService.getAppointmentsByPatientId(id);
        List<AppointmentDto> dtos = list.stream().map(a -> AppointmentDto.builder()
                .id(a.getId())
                .doctorName(a.getDoctorId().getUserId().getFullName())
                .appointmentDate(a.getAppointmentDate())
                .appointmentTime(a.getAppointmentTime())
                .status(a.getStatus())
                .reason(a.getReason())
                .build()).collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }
}
