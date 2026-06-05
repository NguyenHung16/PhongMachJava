package com.vnh.mapper;

import com.vnh.dto.PatientDto;
import com.vnh.pojo.Patients;

public class PatientMapper {
    public static PatientDto toPatientDto(Patients patient) {
        if (patient == null) {
            return null;
        }
        return PatientDto.builder()
                .id(patient.getId())
                .patientCode(patient.getPatientCode())
                .fullName(patient.getUserId() != null ? patient.getUserId().getFullName() : null)
                .username(patient.getUserId() != null ? patient.getUserId().getUsername() : null)
                .email(patient.getUserId() != null ? patient.getUserId().getEmail() : null)
                .phoneNumber(patient.getUserId() != null ? patient.getUserId().getPhoneNumber() : null)
                .avatar(patient.getUserId() != null ? patient.getUserId().getAvatar() : null)
                .build();
    }
}
