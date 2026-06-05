package com.vnh.repositories.impl;

import com.vnh.pojo.MedicalRecords;
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
class MedicalRecordRepositoryImplTest {

    @Mock private LocalSessionFactoryBean sessionFactory;
    @Mock private SessionFactory hibernateSessionFactory;
    @Mock private Session session;
    @Mock private Query<MedicalRecords> query;
    @InjectMocks private MedicalRecordRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        when(sessionFactory.getObject()).thenReturn(hibernateSessionFactory);
        when(hibernateSessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    void getMedicalRecordsReturnsHistory() {
        List<MedicalRecords> records = List.of(new MedicalRecords());
        when(session.createQuery(anyString(), eq(MedicalRecords.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(records);

        assertSame(records, repository.getMedicalRecords());
    }

    @Test
    void getMedicalRecordByIdDelegatesSessionGet() {
        MedicalRecords record = new MedicalRecords();
        when(session.get(MedicalRecords.class, 7)).thenReturn(record);

        assertSame(record, repository.getMedicalRecordById(7));
    }

    @Test
    void savePersistsNewRecordAndMergesExistingOne() {
        MedicalRecords newRecord = new MedicalRecords();
        repository.save(newRecord);
        verify(session).persist(newRecord);

        MedicalRecords existing = new MedicalRecords();
        existing.setId(4);
        repository.save(existing);
        verify(session).merge(existing);
    }

    @Test
    void getMedicalRecordsByPatientIdReturnsList() {
        List<MedicalRecords> records = List.of(new MedicalRecords());
        when(session.createQuery(anyString(), eq(MedicalRecords.class))).thenReturn(query);
        when(query.setParameter("patientId", 2)).thenReturn(query);
        when(query.getResultList()).thenReturn(records);

        assertSame(records, repository.getMedicalRecordsByPatientId(2));
    }
}
