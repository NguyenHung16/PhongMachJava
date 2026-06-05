package com.vnh.controllers;

import com.vnh.dto.ApiResponse;
import com.vnh.dto.BillDto;
import com.vnh.dto.AppointmentDto;
import com.vnh.dto.MedicalRecordDTO;
import com.vnh.dto.PatientDto;
import com.vnh.dto.PrescriptionItemDto;
import com.vnh.pojo.Appointments;
import com.vnh.pojo.Bills;
import com.vnh.pojo.Doctors;
import com.vnh.pojo.Patients;
import com.vnh.pojo.Users;
import com.vnh.services.AppointmentService;
import com.vnh.services.MedicalRecordService;
import com.vnh.services.PatientService;
import com.vnh.services.UserServices;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiPatientControllerTest {

    @Mock private MedicalRecordService medicalRecordsService;
    @Mock private AppointmentService appointmentService;
    @Mock private PatientService patientService;
    @Mock private UserServices userService;
    @InjectMocks private ApiPatientController controller;

    @Test
    void getMyAppointmentsUsesPatientIdFromProfile() {
        PatientDto patient = PatientDto.builder()
                .id(42)
                .fullName("Test Patient")
                .patientCode("P-42")
                .build();
        when(patientService.getPatientByUsername("patient1")).thenReturn(patient);
        when(appointmentService.getAppointmentsByPatientId(42)).thenReturn(List.of());

        ResponseEntity<ApiResponse<List<AppointmentDto>>> response = controller.getMyAppointments(() -> "patient1");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(appointmentService).getAppointmentsByPatientId(42);
    }

    @Test
    void getMedicalRecordsReturnsDtosWithPrescriptions() {
        MedicalRecordDTO record = MedicalRecordDTO.builder()
                .id(3)
                .diagnosis("Flu")
                .prescriptionItems(List.of(PrescriptionItemDto.builder()
                        .medicineName("Paracetamol")
                        .quantity(2)
                        .instruction("After meal")
                        .build()))
                .build();
        when(medicalRecordsService.getMedicalRecordDtosByPatientId(42)).thenReturn(List.of(record));

        ResponseEntity<ApiResponse<List<MedicalRecordDTO>>> response = controller.getMedicalRecords(42);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().get(0).getPrescriptionItems().size());
        assertEquals("Paracetamol", response.getBody().getData().get(0).getPrescriptionItems().get(0).getMedicineName());
    }

    @Test
    void getMyBillsUsesPatientIdFromProfile() {
        PatientDto patient = PatientDto.builder()
                .id(42)
                .fullName("Test Patient")
                .patientCode("P-42")
                .build();
        Bills bill = new Bills();
        bill.setId(7);
        when(patientService.getPatientByUsername("patient1")).thenReturn(patient);
        when(patientService.getBillsByPatientId(42)).thenReturn(List.of(bill));

        ResponseEntity<ApiResponse<List<BillDto>>> response = controller.getMyBills(() -> "patient1");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().size());
        verify(patientService).getBillsByPatientId(42);
    }

    @Test
    void getBillsForPatientRejectsOtherPatientId() {
        PatientDto patient = PatientDto.builder()
                .id(42)
                .fullName("Test Patient")
                .patientCode("P-42")
                .build();
        when(patientService.getPatientByUsername("patient1")).thenReturn(patient);

        ResponseEntity<ApiResponse<List<BillDto>>> response = controller.getBillsForPatient(99, () -> "patient1");

        assertEquals(403, response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(patientService, never()).getBillsByPatientId(99);
    }

    @Test
    void getMyPatientProfileHandlesSuccessMissingPrincipalAndMissingPatient() {
        assertEquals(401, controller.getMyPatientProfile(null).getStatusCode().value());

        when(patientService.getPatientByUsername("missing")).thenReturn(null);
        assertEquals(404, controller.getMyPatientProfile(() -> "missing").getStatusCode().value());

        PatientDto patient = PatientDto.builder().id(42).fullName("Test Patient").patientCode("P-42").build();
        when(patientService.getPatientByUsername("patient1")).thenReturn(patient);
        ResponseEntity<ApiResponse<PatientDto>> response = controller.getMyPatientProfile(() -> "patient1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("P-42", response.getBody().getData().getPatientCode());
    }

    @Test
    void getMyAppointmentsHandlesUnauthorizedMissingPatientAndMapsAppointments() {
        assertEquals(401, controller.getMyAppointments(null).getStatusCode().value());

        when(patientService.getPatientByUsername("missing")).thenReturn(null);
        assertEquals(404, controller.getMyAppointments(() -> "missing").getStatusCode().value());

        PatientDto patient = PatientDto.builder().id(42).fullName("Test Patient").build();
        when(patientService.getPatientByUsername("patient2")).thenReturn(patient);
        when(appointmentService.getAppointmentsByPatientId(42)).thenReturn(List.of(appointment()));

        ResponseEntity<ApiResponse<List<AppointmentDto>>> response = controller.getMyAppointments(() -> "patient2");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Dr Test", response.getBody().getData().get(0).getDoctorName());
    }

    @Test
    void getMyBillsHandlesUnauthorizedMissingPatientAndMapsBills() {
        assertEquals(401, controller.getMyBills(null).getStatusCode().value());

        when(patientService.getPatientByUsername("missing")).thenReturn(null);
        assertEquals(404, controller.getMyBills(() -> "missing").getStatusCode().value());

        PatientDto patient = PatientDto.builder().id(42).fullName("Test Patient").build();
        Bills bill = new Bills();
        bill.setId(8);
        bill.setTotalAmount(new BigDecimal("150000"));
        bill.setPaymentStatus("PAID");
        when(patientService.getPatientByUsername("patient2")).thenReturn(patient);
        when(patientService.getBillsByPatientId(42)).thenReturn(List.of(bill));

        ResponseEntity<ApiResponse<List<BillDto>>> response = controller.getMyBills(() -> "patient2");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("PAID", response.getBody().getData().get(0).getPaymentStatus());
    }

    @Test
    void getBillsForPatientHandlesUnauthorizedMissingPatientAndSuccess() {
        assertEquals(401, controller.getBillsForPatient(42, null).getStatusCode().value());

        when(patientService.getPatientByUsername("missing")).thenReturn(null);
        assertEquals(404, controller.getBillsForPatient(42, () -> "missing").getStatusCode().value());

        PatientDto patient = PatientDto.builder().id(42).fullName("Test Patient").build();
        when(patientService.getPatientByUsername("patient2")).thenReturn(patient);
        when(patientService.getBillsByPatientId(42)).thenReturn(List.of());

        assertEquals(200, controller.getBillsForPatient(42, () -> "patient2").getStatusCode().value());
    }

    private static Appointments appointment() {
        Users doctorUser = new Users();
        doctorUser.setFullName("Dr Test");
        Doctors doctor = new Doctors();
        doctor.setUserId(doctorUser);
        Patients patient = new Patients();
        patient.setId(42);
        Appointments appointment = new Appointments();
        appointment.setId(9);
        appointment.setDoctorId(doctor);
        appointment.setPatientId(patient);
        appointment.setAppointmentDate(new Date());
        appointment.setStatus("CONFIRMED");
        return appointment;
    }
}
