package com.vnh.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillDto {
    private Integer id;
    private Date billDate;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private Integer appointmentId;
    private String patientName;
}
