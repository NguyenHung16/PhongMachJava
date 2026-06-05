package com.vnh.repositories.impl;

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
class StatsRepositoryImplTest {

    @Mock private LocalSessionFactoryBean sessionFactory;
    @Mock private SessionFactory hibernateSessionFactory;
    @Mock private Session session;
    @Mock private Query<Object[]> query;
    @InjectMocks private StatsRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        when(sessionFactory.getObject()).thenReturn(hibernateSessionFactory);
        when(hibernateSessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    void revenueByTimeUsesRequestedGrouping() {
        List<Object[]> rows = List.<Object[]>of(new Object[] {"MONTH", 1L});
        when(session.createQuery(anyString(), eq(Object[].class))).thenReturn(query);
        when(query.setParameter("year", 2025)).thenReturn(query);
        when(query.getResultList()).thenReturn(rows);

        assertSame(rows, repository.revenueByTime(2025, "MONTH"));
        ArgumentCaptor<String> hqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(hqlCaptor.capture(), eq(Object[].class));
        assertTrue(hqlCaptor.getValue().contains("b.paymentStatus = 'PAID'"));
    }

    @Test
    void revenueByTimeUsesQuarterGrouping() {
        List<Object[]> rows = List.<Object[]>of(new Object[] {"QUARTER", 2L});
        when(session.createQuery(anyString(), eq(Object[].class))).thenReturn(query);
        when(query.setParameter("year", 2025)).thenReturn(query);
        when(query.getResultList()).thenReturn(rows);

        assertSame(rows, repository.revenueByTime(2025, "QUARTER"));
    }

    @Test
    void countPatientsByGenderReturnsList() {
        List<Object[]> rows = List.<Object[]>of(new Object[] {"F", 3L});
        when(session.createQuery(anyString(), eq(Object[].class))).thenReturn(query);
        when(query.getResultList()).thenReturn(rows);

        assertSame(rows, repository.countPatientsByGender());
    }

    @Test
    void countPatientsBySpecialtyReturnsList() {
        List<Object[]> rows = List.<Object[]>of(new Object[] {"Cardiology", 2L});
        when(session.createQuery(anyString(), eq(Object[].class))).thenReturn(query);
        when(query.getResultList()).thenReturn(rows);

        assertSame(rows, repository.countPatientsBySpecialty());
    }

    @Test
    void countPatientsByAgeGroupReturnsList() {
        List<Object[]> rows = List.<Object[]>of(new Object[] {"Bệnh nhân", 4L});
        when(session.createQuery(anyString(), eq(Object[].class))).thenReturn(query);
        when(query.getResultList()).thenReturn(rows);

        assertSame(rows, repository.countPatientsByAgeGroup());
    }

    @Test
    void topUsedServicesReturnsList() {
        List<Object[]> rows = List.<Object[]>of(new Object[] {"Service", 8L});
        when(session.createQuery(anyString(), eq(Object[].class))).thenReturn(query);
        when(query.getResultList()).thenReturn(rows);

        assertSame(rows, repository.topUsedServices());
    }

    @Test
    void popularDiseasesReturnsList() {
        List<Object[]> rows = List.<Object[]>of(new Object[] {"Flu", 6L});
        when(session.createQuery(anyString(), eq(Object[].class))).thenReturn(query);
        when(query.getResultList()).thenReturn(rows);

        assertSame(rows, repository.popularDiseases());
    }
}
