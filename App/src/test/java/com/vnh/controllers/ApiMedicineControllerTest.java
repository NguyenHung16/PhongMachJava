package com.vnh.controllers;

import com.vnh.dto.ApiResponse;
import com.vnh.dto.MedicineDto;
import com.vnh.pojo.Medicines;
import com.vnh.services.MedicineServices;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiMedicineControllerTest {

    @Mock private MedicineServices medicineService;
    @InjectMocks private ApiMedicineController controller;

    @Test
    void getMedicinesReturnsDtos() {
        Medicines medicine = medicine();
        Map<String, String> params = Map.of("kw", "para");
        when(medicineService.getMedicines(params)).thenReturn(List.of(medicine));

        ResponseEntity<ApiResponse<List<MedicineDto>>> response = controller.getMedicines(params);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Paracetamol", response.getBody().getData().get(0).getName());
    }

    @Test
    void getByIdHandlesFoundAndMissingMedicine() {
        when(medicineService.getMedicineById(1)).thenReturn(medicine());
        assertEquals(200, controller.getById(1).getStatusCode().value());

        when(medicineService.getMedicineById(2)).thenReturn(null);
        assertEquals(400, controller.getById(2).getStatusCode().value());
    }

    private static Medicines medicine() {
        Medicines medicine = new Medicines();
        medicine.setId(1);
        medicine.setName("Paracetamol");
        medicine.setUnit("viên");
        medicine.setPrice(5000.0);
        return medicine;
    }
}
