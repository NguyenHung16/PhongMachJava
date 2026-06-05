package com.vnh.repositories.impl;

import com.vnh.pojo.Medicines;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaOrder;
import org.hibernate.query.criteria.JpaPath;
import org.hibernate.query.criteria.JpaPredicate;
import org.hibernate.query.criteria.JpaRoot;
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
class MedicineRepositoryImplTest {

    @Mock private LocalSessionFactoryBean sessionFactory;
    @Mock private SessionFactory hibernateSessionFactory;
    @Mock private Session session;
    @Mock private HibernateCriteriaBuilder criteriaBuilder;
    @Mock private JpaCriteriaQuery<Medicines> criteriaQuery;
    @Mock private JpaRoot<Medicines> root;
    @Mock private JpaPath namePath;
    @Mock private JpaPath idPath;
    @Mock private JpaPredicate predicate;
    @Mock private JpaOrder order;
    @Mock private Query<Medicines> query;
    @InjectMocks private MedicineRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        when(sessionFactory.getObject()).thenReturn(hibernateSessionFactory);
        when(hibernateSessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    void getMedicinesBuildsCriteriaQuery() {
        List<Medicines> medicines = List.of(new Medicines());
        when(session.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Medicines.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Medicines.class)).thenReturn(root);
        when(criteriaQuery.select(root)).thenReturn(criteriaQuery);
        when(criteriaQuery.orderBy(order)).thenReturn(criteriaQuery);
        when(criteriaBuilder.asc(idPath)).thenReturn(order);
        when(root.get("id")).thenReturn(idPath);
        when(session.createQuery(criteriaQuery)).thenReturn(query);
        when(query.getResultList()).thenReturn(medicines);

        assertSame(medicines, repository.getMedicines(Map.of()));
    }

    @Test
    void getMedicinesAppliesKeywordFilter() {
        List<Medicines> medicines = List.of(new Medicines());
        when(session.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Medicines.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Medicines.class)).thenReturn(root);
        when(criteriaQuery.select(root)).thenReturn(criteriaQuery);
        when(root.get("name")).thenReturn(namePath);
        when(criteriaBuilder.like(namePath, "%Para%")).thenReturn(predicate);
        when(criteriaQuery.where(predicate)).thenReturn(criteriaQuery);
        when(root.get("id")).thenReturn(idPath);
        when(criteriaBuilder.asc(idPath)).thenReturn(order);
        when(criteriaQuery.orderBy(order)).thenReturn(criteriaQuery);
        when(session.createQuery(criteriaQuery)).thenReturn(query);
        when(query.getResultList()).thenReturn(medicines);

        assertSame(medicines, repository.getMedicines(Map.of("kw", "Para")));
    }

    @Test
    void getMedicinesIgnoresNullAndEmptyKeyword() {
        List<Medicines> medicines = List.of(new Medicines());
        when(session.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Medicines.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Medicines.class)).thenReturn(root);
        when(criteriaQuery.select(root)).thenReturn(criteriaQuery);
        when(root.get("id")).thenReturn(idPath);
        when(criteriaBuilder.asc(idPath)).thenReturn(order);
        when(criteriaQuery.orderBy(order)).thenReturn(criteriaQuery);
        when(session.createQuery(criteriaQuery)).thenReturn(query);
        when(query.getResultList()).thenReturn(medicines);

        assertSame(medicines, repository.getMedicines(null));
        assertSame(medicines, repository.getMedicines(Map.of("kw", "")));
    }

    @Test
    void getMedicineByIdDelegatesSessionGet() {
        Medicines medicine = new Medicines();
        when(session.get(Medicines.class, 8)).thenReturn(medicine);

        assertSame(medicine, repository.getMedicineById(8));
    }

    @Test
    void addOrUpdateMedicinePersistsNewMedicineAndMergesExisting() {
        Medicines newMedicine = new Medicines();
        repository.addOrUpdateMedicine(newMedicine);
        verify(session).persist(newMedicine);

        Medicines existing = new Medicines();
        existing.setId(5);
        repository.addOrUpdateMedicine(existing);
        verify(session).merge(existing);
    }

    @Test
    void deleteMedicineRemovesLoadedMedicine() {
        Medicines medicine = new Medicines();
        when(session.get(Medicines.class, 3)).thenReturn(medicine);

        repository.deleteMedicine(3);

        verify(session).remove(medicine);
    }

    @Test
    void deleteMedicineDoesNothingWhenMissing() {
        MedicineRepositoryImpl spyRepository = spy(repository);
        doReturn(null).when(spyRepository).getMedicineById(3);

        spyRepository.deleteMedicine(3);

        verify(session, never()).remove(any());
    }

    @Test
    void getExpiringMedicinesReturnsLimitedList() {
        List<Medicines> medicines = List.of(new Medicines());
        when(session.createQuery(anyString(), eq(Medicines.class))).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(medicines);

        assertSame(medicines, repository.getExpiringMedicines(30));
    }

    @Test
    void getLowStockMedicinesReturnsList() {
        List<Medicines> medicines = List.of(new Medicines());
        when(session.createQuery(anyString(), eq(Medicines.class))).thenReturn(query);
        when(query.setParameter("threshold", 20)).thenReturn(query);
        when(query.getResultList()).thenReturn(medicines);

        assertSame(medicines, repository.getLowStockMedicines(20));
    }
}
