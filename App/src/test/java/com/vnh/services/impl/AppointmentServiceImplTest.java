package com.vnh.services.impl;

import com.vnh.pojo.Appointments;
import com.vnh.repositories.AppointmentRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock private AppointmentRepository appointmentRepository;
    @InjectMocks private AppointmentServiceImpl service;

    @Test
    void delegatesAppointmentOperationsToRepository() {
        Appointments appointment = new Appointments();
        when(appointmentRepository.getAppointments()).thenReturn(List.of(appointment));
        when(appointmentRepository.getAppointmentById(1)).thenReturn(appointment);
        when(appointmentRepository.findByDoctorId_Id(2)).thenReturn(List.of(appointment));
        when(appointmentRepository.findByPatientId_Id(3)).thenReturn(List.of(appointment));

        assertEquals(List.of(appointment), service.getAppointments());
        assertSame(appointment, service.getAppointmentById(1));
        service.saveAppointment(appointment);
        assertEquals(List.of(appointment), service.getAppointmentsByDoctorId(2));
        assertEquals(List.of(appointment), service.getAppointmentsByPatientId(3));
        verify(appointmentRepository).saveAppointment(appointment);
    }

    @Test
    void getAppointmentsByDoctorIdExcludesCompletedAppointments() {
        Appointments confirmed = new Appointments();
        confirmed.setStatus("CONFIRMED");
        Appointments completed = new Appointments();
        completed.setStatus(" completed ");
        when(appointmentRepository.findByDoctorId_Id(2)).thenReturn(List.of(confirmed, completed));

        assertEquals(List.of(confirmed), service.getAppointmentsByDoctorId(2));
    }
}
