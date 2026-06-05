package com.vnh.repositories.impl;

import com.vnh.pojo.Specialties;
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
class SpecialtyRepositoryImplTest {

    @Mock private LocalSessionFactoryBean sessionFactory;
    @Mock private SessionFactory hibernateSessionFactory;
    @Mock private Session session;
    @Mock private Query<Specialties> query;
    @InjectMocks private SpecialtyRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        when(sessionFactory.getObject()).thenReturn(hibernateSessionFactory);
        when(hibernateSessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    void getSpecialtiesReturnsList() {
        List<Specialties> specialties = List.of(new Specialties());
        when(session.createQuery(anyString(), eq(Specialties.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(specialties);

        assertSame(specialties, repository.getSpecialties());
    }

    @Test
    void getSpecialtyByIdDelegatesSessionGet() {
        Specialties specialty = new Specialties();
        when(session.get(Specialties.class, 3)).thenReturn(specialty);

        assertSame(specialty, repository.getSpecialtyById(3));
    }

    @Test
    void saveOrUpdatePersistsNewSpecialtyAndMergesExisting() {
        Specialties newSpecialty = new Specialties();
        repository.saveOrUpdate(newSpecialty);
        verify(session).persist(newSpecialty);

        Specialties existing = new Specialties();
        existing.setId(2);
        repository.saveOrUpdate(existing);
        verify(session).merge(existing);
    }

    @Test
    void deleteSpecialtyRemovesLoadedSpecialty() {
        Specialties specialty = new Specialties();
        when(session.get(Specialties.class, 5)).thenReturn(specialty);

        repository.deleteSpecialty(5);

        verify(session).remove(specialty);
    }

    @Test
    void deleteSpecialtyDoesNothingWhenMissing() {
        SpecialtyRepositoryImpl spyRepository = spy(repository);
        doReturn(null).when(spyRepository).getSpecialtyById(5);

        spyRepository.deleteSpecialty(5);

        verify(session, never()).remove(any());
    }
}
