package com.vnh.controllers;

import com.vnh.dto.ApiResponse;
import com.vnh.dto.AppointmentDto;
import com.vnh.dto.DoctorDto;
import com.vnh.dto.PatientDto;
import com.vnh.dto.SpecialtyDto;
import com.vnh.pojo.Appointments;
import com.vnh.pojo.Doctors;
import com.vnh.pojo.Patients;
import com.vnh.pojo.Specialties;
import com.vnh.pojo.Users;
import com.vnh.services.AppointmentService;
import com.vnh.services.DoctorService;
import com.vnh.services.PatientService;
import com.vnh.services.SpecialtyService;
import com.vnh.services.UserServices;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiDoctorControllerTest {

    @Mock private DoctorService doctorService;
    @Mock private SpecialtyService specialtyService;
    @Mock private AppointmentService appointmentService;
    @Mock private UserServices userService;
    @Mock private PatientService patientService;
    @InjectMocks private ApiDoctorController controller;

    @Test
    void getSpecialtiesReturnsSpecialtyDtos() {
        Specialties specialty = new Specialties();
        specialty.setId(2);
        specialty.setName("Cardiology");
        specialty.setDescription("Heart care");
        when(specialtyService.getSpecialties()).thenReturn(List.of(specialty));

        ResponseEntity<ApiResponse<List<SpecialtyDto>>> response = controller.getSpecialties();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getStatus());
        assertEquals(1, response.getBody().getData().size());
        assertEquals("Cardiology", response.getBody().getData().get(0).getName());
        assertEquals("Heart care", response.getBody().getData().get(0).getDescription());
    }

    @Test
    void getDoctorsFiltersBySpecialtyWhenSpecialtyIdExists() {
        Doctors doctor = doctor("Dr Specialty", 7);
        when(doctorService.getDoctorsBySpecialtyId(2)).thenReturn(List.of(doctor));

        ResponseEntity<ApiResponse<List<DoctorDto>>> response = controller.getDoctors(2);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getData().size());
        assertEquals("Dr Specialty", response.getBody().getData().get(0).getFullName());
        verify(doctorService).getDoctorsBySpecialtyId(2);
    }

    @Test
    void getDoctorsReturnsAllWhenNoSpecialtyFilter() {
        when(doctorService.getDoctors()).thenReturn(List.of(doctor("Dr All", 8)));

        ResponseEntity<ApiResponse<List<DoctorDto>>> response = controller.getDoctors(null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Dr All", response.getBody().getData().get(0).getFullName());
    }

    @Test
    void getMyAppointmentsRejectsMissingOrInvalidDoctor() {
        assertEquals(401, controller.getMyAppointments(null).getStatusCode().value());

        Principal principal = () -> "patient";
        Users patientUser = user(3, "patient", "PATIENT");
        when(userService.getUserByUsername("patient")).thenReturn(patientUser);
        assertEquals(403, controller.getMyAppointments(principal).getStatusCode().value());

        Users doctorUser = user(4, "doctor", "DOCTOR");
        when(userService.getUserByUsername("doctor")).thenReturn(doctorUser);
        when(doctorService.getDoctorById(4)).thenReturn(null);
        assertEquals(404, controller.getMyAppointments(() -> "doctor").getStatusCode().value());
    }

    @Test
    void getMyAppointmentsReturnsDoctorAppointments() {
        Users doctorUser = user(5, "doctor", "DOCTOR");
        Doctors doctor = doctor("Dr Test", 5);
        Patients patient = patient("Patient Test", 10);
        Appointments appointment = new Appointments();
        appointment.setId(11);
        appointment.setDoctorId(doctor);
        appointment.setPatientId(patient);
        appointment.setStatus("CONFIRMED");

        when(userService.getUserByUsername("doctor")).thenReturn(doctorUser);
        when(doctorService.getDoctorById(5)).thenReturn(doctor);
        when(appointmentService.getAppointmentsByDoctorId(5)).thenReturn(List.of(appointment));

        ResponseEntity<ApiResponse<List<AppointmentDto>>> response = controller.getMyAppointments(() -> "doctor");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Patient Test", response.getBody().getData().get(0).getPatientName());
    }

    @Test
    void getMyPatientsHandlesAuthBranchesAndReturnsPatients() {
        assertEquals(401, controller.getMyPatients(null).getStatusCode().value());

        Users nonDoctor = user(6, "user", "PATIENT");
        when(userService.getUserByUsername("user")).thenReturn(nonDoctor);
        assertEquals(403, controller.getMyPatients(() -> "user").getStatusCode().value());

        Users doctorUser = user(7, "doctor", "DOCTOR");
        Doctors doctor = doctor("Dr Patient", 7);
        Patients patient = patient("Patient One", 20);
        when(userService.getUserByUsername("doctor")).thenReturn(doctorUser);
        when(doctorService.getDoctorById(7)).thenReturn(doctor);
        when(patientService.findByDoctorId(7)).thenReturn(List.of(patient));

        ResponseEntity<ApiResponse<List<PatientDto>>> response = controller.getMyPatients(() -> "doctor");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Patient One", response.getBody().getData().get(0).getFullName());
    }

    @Test
    void confirmAppointmentUpdatesStatusWhenAppointmentExists() {
        Appointments appointment = new Appointments();
        appointment.setId(12);
        when(appointmentService.getAppointmentById(12)).thenReturn(appointment);

        ResponseEntity<ApiResponse<String>> response = controller.confirmAppointment(12);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("CONFIRMED", appointment.getStatus());
        verify(appointmentService).saveAppointment(appointment);
        assertEquals(400, controller.confirmAppointment(99).getStatusCode().value());
    }

    private static Users user(int id, String username, String role) {
        Users user = new Users();
        user.setId(id);
        user.setUsername(username);
        user.setRole(role);
        user.setFullName(username);
        return user;
    }

    private static Doctors doctor(String fullName, int id) {
        Users user = user(id, fullName, "DOCTOR");
        Doctors doctor = new Doctors();
        doctor.setId(id);
        doctor.setUserId(user);
        return doctor;
    }

    private static Patients patient(String fullName, int id) {
        Users user = user(id, fullName, "PATIENT");
        Patients patient = new Patients();
        patient.setId(id);
        patient.setUserId(user);
        return patient;
    }
}
