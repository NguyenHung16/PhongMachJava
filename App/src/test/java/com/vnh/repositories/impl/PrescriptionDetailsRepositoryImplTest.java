package com.vnh.repositories.impl;

import com.vnh.pojo.PrescriptionDetails;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionDetailsRepositoryImplTest {

    @Mock private LocalSessionFactoryBean sessionFactory;
    @Mock private SessionFactory hibernateSessionFactory;
    @Mock private Session session;
    @InjectMocks private PrescriptionDetailsRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        when(sessionFactory.getObject()).thenReturn(hibernateSessionFactory);
        when(hibernateSessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    void savePrescriptionDetailsPersistsEntity() {
        PrescriptionDetails details = new PrescriptionDetails();

        repository.SavePrescriptionDetails(details);

        verify(session).persist(details);
    }
}
