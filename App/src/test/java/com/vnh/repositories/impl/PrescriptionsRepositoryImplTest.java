package com.vnh.repositories.impl;

import com.vnh.pojo.Prescriptions;
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
class PrescriptionsRepositoryImplTest {

    @Mock private LocalSessionFactoryBean sessionFactory;
    @Mock private SessionFactory hibernateSessionFactory;
    @Mock private Session session;
    @Mock private Query<Prescriptions> query;
    @InjectMocks private PrescriptionsRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        when(sessionFactory.getObject()).thenReturn(hibernateSessionFactory);
        when(hibernateSessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    void getPrescriptionsReturnsList() {
        List<Prescriptions> prescriptions = List.of(new Prescriptions());
        when(session.createQuery(anyString(), eq(Prescriptions.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(prescriptions);

        assertSame(prescriptions, repository.getPrescriptions());
    }

    @Test
    void getPrescriptionByIdReturnsPrescriptionOrNull() {
        Prescriptions prescription = new Prescriptions();
        when(session.createQuery(anyString(), eq(Prescriptions.class))).thenReturn(query);
        when(query.setParameter("id", 6)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(prescription);

        assertSame(prescription, repository.getPrescriptionById(6));

        when(session.createQuery(anyString(), eq(Prescriptions.class))).thenThrow(new RuntimeException("missing"));
        assertNull(repository.getPrescriptionById(6));
    }

    @Test
    void savePrescriptionPersistsNewPrescriptionAndMergesExisting() {
        Prescriptions newPrescription = new Prescriptions();
        repository.savePrescription(newPrescription);
        verify(session).persist(newPrescription);

        Prescriptions existing = new Prescriptions();
        existing.setId(4);
        repository.savePrescription(existing);
        verify(session).merge(existing);
    }
}
