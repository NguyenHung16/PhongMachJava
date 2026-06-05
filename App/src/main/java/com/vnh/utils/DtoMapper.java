package com.vnh.utils;

import com.vnh.dto.*;
import com.vnh.pojo.*;
import java.util.List;
import java.util.stream.Collectors;

public class DtoMapper {

    public static SpecialtyDto toSpecialtyDto(Specialties s) {
        if (s == null) return null;
        return SpecialtyDto.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .build();
    }

    public static DoctorDto toDoctorDto(Doctors d) {
        if (d == null) return null;
        return DoctorDto.builder()
                .id(d.getId())
                .doctorCode(d.getDoctorCode())
                .qualification(d.getQualification())
                .biography(d.getBiography())
                .fullName(d.getUserId() != null ? d.getUserId().getFullName() : null)
                .username(d.getUserId() != null ? d.getUserId().getUsername() : null)
                .email(d.getUserId() != null ? d.getUserId().getEmail() : null)
                .phoneNumber(d.getUserId() != null ? d.getUserId().getPhoneNumber() : null)
                .avatar(d.getUserId() != null ? d.getUserId().getAvatar() : null)
                .specialty(toSpecialtyDto(d.getSpecialtyId()))
                .build();
    }

    public static PatientDto toPatientDto(Patients p) {
        if (p == null) return null;
        return PatientDto.builder()
                .id(p.getId())
                .patientCode(p.getPatientCode())
                .fullName(p.getUserId() != null ? p.getUserId().getFullName() : null)
                .username(p.getUserId() != null ? p.getUserId().getUsername() : null)
                .email(p.getUserId() != null ? p.getUserId().getEmail() : null)
                .phoneNumber(p.getUserId() != null ? p.getUserId().getPhoneNumber() : null)
                .avatar(p.getUserId() != null ? p.getUserId().getAvatar() : null)
                .build();
    }

    public static MedicineDto toMedicineDto(Medicines m) {
        if (m == null) return null;
        return MedicineDto.builder()
                .id(m.getId())
                .name(m.getName())
                .dosageForm(m.getDosageForm())
                .manufacturer(m.getManufacturer())
                .stockQuantity(m.getStockQuantity())
                .unit(m.getUnit())
                .expiryDate(m.getExpiryDate())
                .price(m.getPrice())
                .imageUrl(m.getImageUrl())
                .build();
    }

    public static AppointmentDto toAppointmentDto(Appointments a) {
        if (a == null) return null;
        return AppointmentDto.builder()
                .id(a.getId())
                .appointmentDate(a.getAppointmentDate())
                .appointmentTime(a.getAppointmentTime())
                .reason(a.getReason())
                .status(a.getStatus())
                .patientName(a.getPatientId() != null && a.getPatientId().getUserId() != null ? a.getPatientId().getUserId().getFullName() : "N/A")
                .patientId(a.getPatientId() != null ? a.getPatientId().getId() : null)
                .doctorName(a.getDoctorId() != null && a.getDoctorId().getUserId() != null ? a.getDoctorId().getUserId().getFullName() : "N/A")
                .specialtyName(a.getDoctorId() != null && a.getDoctorId().getSpecialtyId() != null ? a.getDoctorId().getSpecialtyId().getName() : "N/A")
                .build();
    }

    public static BillDto toBillDto(Bills b) {
        if (b == null) return null;
        return BillDto.builder()
                .id(b.getId())
                .billDate(b.getBillDate())
                .totalAmount(b.getTotalAmount())
                .paymentStatus(b.getPaymentStatus())
                .appointmentId(b.getAppointmentId() != null ? b.getAppointmentId().getId() : null)
                .patientName(b.getPatientId() != null && b.getPatientId().getUserId() != null ? b.getPatientId().getUserId().getFullName() : "N/A")
                .build();
    }
}
