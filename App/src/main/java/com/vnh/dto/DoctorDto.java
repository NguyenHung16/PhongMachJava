package com.vnh.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorDto {
    private Integer id;
    private String doctorCode;
    private String qualification;
    private String biography;
    private String fullName; // Từ Users
    private String username; // Từ Users
    private String email;    // Từ Users
    private String phoneNumber; // Từ Users
    private String avatar;   // Từ Users
    private SpecialtyDto specialty; // DTO của chuyên khoa
}
