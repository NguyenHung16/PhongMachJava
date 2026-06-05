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
public class MedicineDto {
    private Integer id;
    private String name;
    private String dosageForm;
    private String manufacturer;
    private int stockQuantity;
    private String unit;
    private Date expiryDate;
    private Double price;
    private String imageUrl;
}
