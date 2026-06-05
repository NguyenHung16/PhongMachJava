package com.vnh.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicalRecordDTO {
    private Integer id;
    private String symptoms;
    private String diagnosis;
    private String treatmentPlan; // Thêm trường này
    private Date createdDate;
    private String patientName;
    private Integer patientId;
    private Integer appointmentId;
    
    private List<PrescriptionItemDto> prescriptionItems;
}
