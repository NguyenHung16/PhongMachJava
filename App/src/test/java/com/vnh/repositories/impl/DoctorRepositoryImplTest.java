package com.vnh.repositories.impl;

import com.vnh.pojo.Doctors;
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
class DoctorRepositoryImplTest {

    @Mock private LocalSessionFactoryBean sessionFactory;
    @Mock private SessionFactory hibernateSessionFactory;
    @Mock private Session session;
    @Mock private Query<Doctors> query;
    @InjectMocks private DoctorRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        when(sessionFactory.getObject()).thenReturn(hibernateSessionFactory);
        when(hibernateSessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    void getDoctorsReturnsList() {
        List<Doctors> doctors = List.of(new Doctors());
        when(session.createQuery(anyString(), eq(Doctors.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(doctors);

        assertSame(doctors, repository.getDoctors());
    }

    @Test
    void getDoctorByIdReturnsDoctor() {
        Doctors doctor = new Doctors();
        when(session.createQuery(anyString(), eq(Doctors.class))).thenReturn(query);
        when(query.setParameter("id", 6)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(doctor);

        assertSame(doctor, repository.getDoctorById(6));
    }

    @Test
    void getDoctorByIdReturnsNullOnException() {
        when(session.createQuery(anyString(), eq(Doctors.class))).thenThrow(new RuntimeException("missing"));

        assertNull(repository.getDoctorById(6));
    }

    @Test
    void getDoctorsBySpecialtyIdReturnsList() {
        List<Doctors> doctors = List.of(new Doctors());
        when(session.createQuery(anyString(), eq(Doctors.class))).thenReturn(query);
        when(query.setParameter("specialtyId", 2)).thenReturn(query);
        when(query.getResultList()).thenReturn(doctors);

        assertSame(doctors, repository.getDoctorsBySpecialtyId(2));
    }

    @Test
    void saveOrUpdateMergesDoctor() {
        Doctors doctor = new Doctors();

        repository.saveOrUpdate(doctor);

        verify(session).merge(doctor);
    }

    @Test
    void deleteDoctorRemovesLoadedDoctor() {
        Doctors doctor = new Doctors();
        when(session.createQuery(anyString(), eq(Doctors.class))).thenReturn(query);
        when(query.setParameter("id", 9)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(doctor);

        repository.deleteDoctor(9);

        verify(session).remove(doctor);
    }

    @Test
    void deleteDoctorDoesNothingWhenMissing() {
        DoctorRepositoryImpl spyRepository = spy(repository);
        doReturn(null).when(spyRepository).getDoctorById(9);

        spyRepository.deleteDoctor(9);

        verify(session, never()).remove(any());
    }
}
