package com.vnh.services.impl;

import com.vnh.pojo.Specialties;
import com.vnh.repositories.SpecialtyRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceImplTest {

    @Mock private SpecialtyRepository specialtyRepo;
    @InjectMocks private SpecialtyServiceImpl service;

    @Test
    void delegatesCrudToRepository() {
        Specialties specialty = new Specialties();
        when(specialtyRepo.getSpecialties()).thenReturn(List.of(specialty));
        when(specialtyRepo.getSpecialtyById(4)).thenReturn(specialty);

        assertEquals(List.of(specialty), service.getSpecialties());
        assertSame(specialty, service.getSpecialtyById(4));
        service.saveOrUpdate(specialty);
        service.deleteSpecialty(4);
        verify(specialtyRepo).saveOrUpdate(specialty);
        verify(specialtyRepo).deleteSpecialty(4);
    }
}
