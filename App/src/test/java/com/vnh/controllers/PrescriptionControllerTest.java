package com.vnh.controllers;

import com.vnh.dto.MedicineRequest;
import com.vnh.dto.PrescriptionRequest;
import com.vnh.pojo.Prescriptions;
import com.vnh.services.PrescriptionService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrescriptionControllerTest {

    @Mock private PrescriptionService prescriptionService;
    @InjectMocks private PrescriptionController controller;

    @Test
    void createPrescriptionValidatesRequest() {
        PrescriptionRequest invalidRecord = new PrescriptionRequest();
        invalidRecord.setMedicalRecordId(0);
        assertEquals(400, controller.createPrescription(invalidRecord, () -> "doctor").getStatusCode().value());

        PrescriptionRequest emptyMedicines = new PrescriptionRequest();
        emptyMedicines.setMedicalRecordId(1);
        emptyMedicines.setMedicines(List.of());
        assertEquals(400, controller.createPrescription(emptyMedicines, () -> "doctor").getStatusCode().value());
    }

    @Test
    void createPrescriptionReturnsCreatedPrescription() {
        PrescriptionRequest request = validRequest();
        Prescriptions prescription = new Prescriptions();
        prescription.setId(5);
        when(prescriptionService.createPrescription(eq(request), any())).thenReturn(prescription);

        ResponseEntity<?> response = controller.createPrescription(request, () -> "doctor");

        assertEquals(201, response.getStatusCode().value());
    }

    @Test
    void createPrescriptionHandlesServiceExceptions() {
        PrescriptionRequest illegal = validRequest();
        when(prescriptionService.createPrescription(eq(illegal), any())).thenThrow(new IllegalArgumentException("invalid"));
        assertEquals(400, controller.createPrescription(illegal, () -> "doctor").getStatusCode().value());

        PrescriptionRequest broken = validRequest();
        when(prescriptionService.createPrescription(eq(broken), any())).thenThrow(new RuntimeException("db"));
        assertEquals(500, controller.createPrescription(broken, () -> "doctor").getStatusCode().value());
    }

    private static PrescriptionRequest validRequest() {
        MedicineRequest item = new MedicineRequest();
        item.setMedicineId(1);
        item.setDosage("2");
        item.setFrequency("2 lần/ngày");
        PrescriptionRequest request = new PrescriptionRequest();
        request.setMedicalRecordId(10);
        request.setMedicines(List.of(item));
        return request;
    }
}
