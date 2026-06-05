package com.vnh.services.impl;

import com.vnh.dto.MedicalRecordDTO;
import com.vnh.dto.PrescriptionItemDto;
import com.vnh.pojo.Appointments;
import com.vnh.pojo.MedicalRecords;
import com.vnh.pojo.Patients;
import com.vnh.pojo.Prescriptions;
import com.vnh.repositories.AppointmentRepository;
import com.vnh.repositories.MedicalRecordRepository;
import com.vnh.repositories.PatientRepository;
import com.vnh.services.MedicalRecordService;
import com.vnh.services.PaymentService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MedicalRecordServiceImpl implements MedicalRecordService {

    @Autowired private MedicalRecordRepository medicalRecordRepo;
    @Autowired private PatientRepository patientRepo;
    @Autowired private AppointmentRepository appointmentRepo;
    @Autowired private PaymentService paymentService;

    @Override
    public List<MedicalRecordDTO> getMedicalRecordsByUserId(int userId) {
        Patients patient = patientRepo.getPatientByUserId(userId);
        if (patient == null) {
            return List.of();
        }
        return getMedicalRecordDtosByPatientId(patient.getId());
    }

    @Override
    public List<MedicalRecordDTO> getMedicalRecordDtosByPatientId(int patientId) {
        List<MedicalRecords> records = medicalRecordRepo.getMedicalRecordsByPatientId(patientId);
        return records.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public MedicalRecordDTO createMedicalRecord(MedicalRecordDTO dto) {
        MedicalRecords mr = new MedicalRecords();
        mr.setDiagnosis(dto.getDiagnosis());
        mr.setSymptoms(dto.getSymptoms());
        mr.setTreatmentPlan(dto.getTreatmentPlan()); // SỬA LỖI: Lưu plan treatment
        mr.setCreatedAt(new Date());

        Patients patient = patientRepo.getPatientById(dto.getPatientId());
        Appointments appointment = appointmentRepo.getAppointmentById(dto.getAppointmentId());

        if (patient == null || appointment == null) {
            throw new IllegalArgumentException("Thông tin Bệnh nhân hoặc Lịch hẹn không hợp lệ.");
        }

        mr.setPatientId(patient);
        mr.setAppointmentId(appointment);

        medicalRecordRepo.save(mr);
        paymentService.createBill(appointment.getId());
        return toDto(mr);
    }

    @Override
    public List<MedicalRecords> getMedicalRecordsByPatientId(int patientId) {
        return medicalRecordRepo.getMedicalRecordsByPatientId(patientId);
    }

    @Override
    public MedicalRecords getMedicalRecordById(int id) {
        return medicalRecordRepo.getMedicalRecordById(id);
    }

    private MedicalRecordDTO toDto(MedicalRecords r) {
        List<PrescriptionItemDto> items = new ArrayList<>();
        
        // CẢI THIỆN: Truy cập Collection an toàn nhờ @Transactional
        if (r.getPrescriptionsCollection() != null && !r.getPrescriptionsCollection().isEmpty()) {
            Prescriptions p = r.getPrescriptionsCollection().iterator().next();
            if (p.getPrescriptionDetailsCollection() != null) {
                items = p.getPrescriptionDetailsCollection().stream().map(d -> PrescriptionItemDto.builder()
                        .medicineName(d.getMedicines() != null ? d.getMedicines().getName() : "Không rõ")
                        .quantity(d.getQuantity())
                        .instruction(d.getInstruction())
                        .build()).collect(Collectors.toList());
            }
        }

        return MedicalRecordDTO.builder()
                .id(r.getId())
                .symptoms(r.getSymptoms())
                .diagnosis(r.getDiagnosis())
                .treatmentPlan(r.getTreatmentPlan()) // SỬA LỖI: Trả về plan treatment
                .createdDate(r.getCreatedAt())
                .patientId(r.getPatientId() != null ? r.getPatientId().getId() : null)
                .appointmentId(r.getAppointmentId() != null ? r.getAppointmentId().getId() : null)
                .patientName(r.getPatientId() != null && r.getPatientId().getUserId() != null ? r.getPatientId().getUserId().getFullName() : "N/A")
                .prescriptionItems(items)
                .build();
    }
}
