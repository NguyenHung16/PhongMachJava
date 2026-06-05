package com.vnh.controllers;

import com.vnh.dto.ApiResponse;
import com.vnh.dto.AppointmentDto;
import com.vnh.payload.AppointmentRequest;
import com.vnh.pojo.Appointments;
import com.vnh.pojo.Doctors;
import com.vnh.pojo.Patients;
import com.vnh.pojo.Users;
import com.vnh.services.AppointmentService;
import com.vnh.services.DoctorService;
import com.vnh.services.PatientService;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiAppointmentControllerTest {

    @Mock private AppointmentService appointmentService;
    @Mock private DoctorService doctorService;
    @Mock private PatientService patientService;
    @InjectMocks private ApiAppointmentController controller;

    @Test
    void createAppointmentStartsInAwaitingDepositStatus() {
        Users doctorUser = new Users();
        doctorUser.setFullName("Dr Test");
        Doctors doctor = new Doctors();
        doctor.setUserId(doctorUser);
        Users patientUser = new Users();
        patientUser.setFullName("Patient Test");
        Patients patient = new Patients();
        patient.setId(2);
        patient.setUserId(patientUser);

        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1);
        request.setPatientId(2);
        request.setAppointmentDate(new Date());
        request.setAppointmentTime(new Date());
        request.setReason("Checkup");

        when(doctorService.getDoctorById(1)).thenReturn(doctor);
        when(patientService.getPatientById(2)).thenReturn(patient);

        ResponseEntity<ApiResponse<AppointmentDto>> response = controller.createAppointment(request);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("AWAITING_DEPOSIT", response.getBody().getData().getStatus());
        assertEquals(2, response.getBody().getData().getPatientId());

        ArgumentCaptor<Appointments> captor = ArgumentCaptor.forClass(Appointments.class);
        verify(appointmentService).saveAppointment(captor.capture());
        assertEquals("AWAITING_DEPOSIT", captor.getValue().getStatus());
    }

    @Test
    void createAppointmentRejectsMissingDoctorOrPatientAndHandlesException() {
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1);
        request.setPatientId(2);

        when(doctorService.getDoctorById(1)).thenReturn(null);
        assertEquals(400, controller.createAppointment(request).getStatusCode().value());

        when(doctorService.getDoctorById(1)).thenThrow(new RuntimeException("db"));
        assertEquals(500, controller.createAppointment(request).getStatusCode().value());
    }

    @Test
    void getByPatientMapsAppointmentsToDtos() {
        Users doctorUser = new Users();
        doctorUser.setFullName("Dr Test");
        Doctors doctor = new Doctors();
        doctor.setUserId(doctorUser);
        Appointments appointment = new Appointments();
        appointment.setId(3);
        appointment.setDoctorId(doctor);
        appointment.setAppointmentDate(new Date());
        appointment.setAppointmentTime(new Date());
        appointment.setReason("Checkup");
        appointment.setStatus("CONFIRMED");
        when(appointmentService.getAppointmentsByPatientId(2)).thenReturn(List.of(appointment));

        ResponseEntity<ApiResponse<List<AppointmentDto>>> response = controller.getByPatient(2);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Dr Test", response.getBody().getData().get(0).getDoctorName());
    }
}
