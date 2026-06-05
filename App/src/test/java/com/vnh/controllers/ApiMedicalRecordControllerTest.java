package com.vnh.controllers;

import com.vnh.dto.ApiResponse;
import com.vnh.dto.MedicalRecordDTO;
import com.vnh.dto.MedicineRequest;
import com.vnh.dto.PrescriptionRequest;
import com.vnh.services.MedicalRecordService;
import com.vnh.services.PrescriptionService;
import java.security.Principal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApiMedicalRecordControllerTest {

    @Mock private MedicalRecordService medicalRecordService;
    @Mock private PrescriptionService prescriptionService;
    @InjectMocks private ApiMedicalRecordController controller;

    @Test
    void createRecordReturnsCreatedMedicalRecord() {
        MedicalRecordDTO request = MedicalRecordDTO.builder()
                .patientId(2)
                .appointmentId(5)
                .diagnosis("Flu")
                .build();
        MedicalRecordDTO created = MedicalRecordDTO.builder()
                .id(9)
                .patientId(2)
                .appointmentId(5)
                .diagnosis("Flu")
                .build();
        when(medicalRecordService.createMedicalRecord(request)).thenReturn(created);

        ResponseEntity<ApiResponse<MedicalRecordDTO>> response = controller.createRecord(request);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(9, response.getBody().getData().getId());
        assertEquals(2, response.getBody().getData().getPatientId());
    }

    @Test
    void createRecordReturnsBadRequestWhenServiceThrows() {
        MedicalRecordDTO request = MedicalRecordDTO.builder().diagnosis("Broken").build();
        when(medicalRecordService.createMedicalRecord(request)).thenThrow(new RuntimeException("invalid"));

        ResponseEntity<ApiResponse<MedicalRecordDTO>> response = controller.createRecord(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("invalid", response.getBody().getMessage());
    }

    @Test
    void createPrescriptionHandlesSuccessAndException() {
        PrescriptionRequest request = new PrescriptionRequest();
        request.setMedicalRecordId(1);
        MedicineRequest item = new MedicineRequest();
        item.setMedicineId(2);
        request.setMedicines(List.of(item));
        Principal principal = () -> "doctor";

        ResponseEntity<ApiResponse<String>> success = controller.createPrescription(request, principal);

        assertEquals(201, success.getStatusCode().value());
        verify(prescriptionService).createPrescription(request, principal);

        PrescriptionRequest broken = new PrescriptionRequest();
        broken.setMedicalRecordId(2);
        when(prescriptionService.createPrescription(broken, null)).thenThrow(new RuntimeException("invalid prescription"));
        ResponseEntity<ApiResponse<String>> failure = controller.createPrescription(broken, null);

        assertEquals(400, failure.getStatusCode().value());
    }

    @Test
    void getPatientRecordsReturnsMedicalRecordDtos() {
        when(medicalRecordService.getMedicalRecordDtosByPatientId(2))
                .thenReturn(List.of(MedicalRecordDTO.builder().id(9).diagnosis("Flu").build()));

        ResponseEntity<ApiResponse<List<MedicalRecordDTO>>> response = controller.getPatientRecords(2);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().size());
        assertEquals("Flu", response.getBody().getData().get(0).getDiagnosis());
    }
}
