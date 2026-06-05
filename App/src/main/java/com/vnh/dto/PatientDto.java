package com.vnh.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientDto {
    private Integer id;
    private String patientCode;
    private String fullName; // Thêm fullName từ User để tiện lợi
    private String username;
    private String email;
    private String phoneNumber;
    private String avatar;
}
