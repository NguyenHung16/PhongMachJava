package com.vnh.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentDto {
    private Integer id;
    private Date appointmentDate;
    private Date appointmentTime;
    private String reason;
    private String status;
    private String patientName;
    private Integer patientId;
    private String doctorName;
    private String specialtyName;
}
