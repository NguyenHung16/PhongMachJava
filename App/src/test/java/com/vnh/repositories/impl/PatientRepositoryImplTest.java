package com.vnh.repositories.impl;

import com.vnh.pojo.Patients;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientRepositoryImplTest {

    @Mock private LocalSessionFactoryBean sessionFactory;
    @Mock private SessionFactory hibernateSessionFactory;
    @Mock private Session session;
    @Mock private Query<Patients> query;
    @InjectMocks private PatientRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        lenient().when(sessionFactory.getObject()).thenReturn(hibernateSessionFactory);
        lenient().when(hibernateSessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    void getPatientsReturnsList() {
        List<Patients> patients = List.of(new Patients());
        when(session.createQuery(anyString(), eq(Patients.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(patients);

        assertSame(patients, repository.getPatients());
    }

    @Test
    void getPatientByIdReturnsPatientAndNullOnFailure() {
        Patients patient = new Patients();
        when(session.createQuery(anyString(), eq(Patients.class))).thenReturn(query);
        when(query.setParameter("id", 7)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(patient);

        assertSame(patient, repository.getPatientById(7));

        when(session.createQuery(anyString(), eq(Patients.class))).thenThrow(new RuntimeException("missing"));
        assertNull(repository.getPatientById(7));
    }

    @Test
    void addPatientPersistsEntity() {
        Patients patient = new Patients();

        repository.addPatient(patient);

        verify(session).persist(patient);
    }

    @Test
    void findByDoctorIdReturnsPatients() {
        List<Patients> patients = List.of(new Patients());
        when(session.createQuery(anyString(), eq(Patients.class))).thenReturn(query);
        when(query.setParameter("docId", 11)).thenReturn(query);
        when(query.getResultList()).thenReturn(patients);

        assertSame(patients, repository.findByDoctorId(11));
    }

    @Test
    void getPatientByUserIdReturnsPatientAndNullOnFailure() {
        Patients patient = new Patients();
        when(session.createQuery(anyString(), eq(Patients.class))).thenReturn(query);
        when(query.setParameter("userId", 5)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(patient);

        assertSame(patient, repository.getPatientByUserId(5));

        when(session.createQuery(anyString(), eq(Patients.class))).thenThrow(new RuntimeException("missing"));
        assertNull(repository.getPatientByUserId(5));
    }
}
