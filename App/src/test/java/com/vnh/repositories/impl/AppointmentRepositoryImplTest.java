package com.vnh.repositories.impl;

import com.vnh.pojo.Appointments;
import java.util.Date;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentRepositoryImplTest {

    @Mock private LocalSessionFactoryBean sessionFactory;
    @Mock private SessionFactory hibernateSessionFactory;
    @Mock private Session session;
    @Mock private Query<Appointments> appointmentsQuery;
    @Mock private Query<Long> countQuery;
    @InjectMocks private AppointmentRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        when(sessionFactory.getObject()).thenReturn(hibernateSessionFactory);
        when(hibernateSessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    void getAppointmentsReturnsQueryResult() {
        List<Appointments> appointments = List.of(new Appointments());
        when(session.createQuery(anyString(), eq(Appointments.class))).thenReturn(appointmentsQuery);
        when(appointmentsQuery.getResultList()).thenReturn(appointments);

        assertSame(appointments, repository.getAppointments());
    }

    @Test
    void getAppointmentByIdReturnsSingleResult() {
        Appointments appointment = new Appointments();
        when(session.createQuery(anyString(), eq(Appointments.class))).thenReturn(appointmentsQuery);
        when(appointmentsQuery.setParameter("id", 7)).thenReturn(appointmentsQuery);
        when(appointmentsQuery.getSingleResult()).thenReturn(appointment);

        assertSame(appointment, repository.getAppointmentById(7));
    }

    @Test
    void saveAppointmentPersistsNewAppointmentAndMergesExistingOne() {
        Appointments newAppointment = new Appointments();
        repository.saveAppointment(newAppointment);
        verify(session).persist(newAppointment);

        reset(session);
        when(sessionFactory.getObject()).thenReturn(hibernateSessionFactory);
        when(hibernateSessionFactory.getCurrentSession()).thenReturn(session);
        Appointments existing = new Appointments();
        existing.setId(5);

        repository.saveAppointment(existing);

        verify(session).merge(existing);
    }

    @Test
    void existsActiveSlotReturnsTrueWhenCountPositive() {
        Date appointmentDate = new Date();
        Date appointmentTime = new Date();
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter("doctorId", 4)).thenReturn(countQuery);
        when(countQuery.setParameter(eq("appointmentDate"), eq(appointmentDate))).thenReturn(countQuery);
        when(countQuery.setParameter(eq("appointmentTime"), eq(appointmentTime))).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(2L);

        assertTrue(repository.existsActiveSlot(4, appointmentDate, appointmentTime));
    }

    @Test
    void existsActiveSlotReturnsFalseWhenCountZero() {
        Date appointmentDate = new Date();
        Date appointmentTime = new Date();
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter("doctorId", 4)).thenReturn(countQuery);
        when(countQuery.setParameter(eq("appointmentDate"), eq(appointmentDate))).thenReturn(countQuery);
        when(countQuery.setParameter(eq("appointmentTime"), eq(appointmentTime))).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(0L);

        assertFalse(repository.existsActiveSlot(4, appointmentDate, appointmentTime));
    }

    @Test
    void findByDoctorIdReturnsBookedAppointments() {
        List<Appointments> appointments = List.of(new Appointments());
        when(session.createQuery(anyString(), eq(Appointments.class))).thenReturn(appointmentsQuery);
        when(appointmentsQuery.setParameter("doctorId", 11)).thenReturn(appointmentsQuery);
        when(appointmentsQuery.getResultList()).thenReturn(appointments);

        assertSame(appointments, repository.findByDoctorId_Id(11));
        ArgumentCaptor<String> hqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(hqlCaptor.capture(), eq(Appointments.class));
        assertTrue(hqlCaptor.getValue().contains("a.appointmentDate = CURRENT_DATE"));
        assertTrue(hqlCaptor.getValue().contains("UPPER(TRIM(a.status)) <> 'COMPLETED'"));
    }

    @Test
    void findByPatientIdReturnsHistory() {
        List<Appointments> appointments = List.of(new Appointments());
        when(session.createQuery(anyString(), eq(Appointments.class))).thenReturn(appointmentsQuery);
        when(appointmentsQuery.setParameter("patientId", 8)).thenReturn(appointmentsQuery);
        when(appointmentsQuery.getResultList()).thenReturn(appointments);

        assertSame(appointments, repository.findByPatientId_Id(8));
    }
}
