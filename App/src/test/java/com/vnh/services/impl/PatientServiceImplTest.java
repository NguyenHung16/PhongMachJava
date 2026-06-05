package com.vnh.services.impl;

import com.vnh.dto.PatientDto;
import com.vnh.pojo.Bills;
import com.vnh.pojo.Patients;
import com.vnh.pojo.Users;
import com.vnh.repositories.BillRepository;
import com.vnh.repositories.PatientRepository;
import com.vnh.repositories.UserRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock private PatientRepository patientRepo;
    @Mock private UserRepository userRepo;
    @Mock private BillRepository billRepo;
    @InjectMocks private PatientServiceImpl service;

    @Test
    void delegatesPatientAndBillOperations() {
        Patients patient = new Patients();
        Bills bill = new Bills();
        when(patientRepo.getPatients()).thenReturn(List.of(patient));
        when(patientRepo.getPatientById(1)).thenReturn(patient);
        when(patientRepo.findByDoctorId(2)).thenReturn(List.of(patient));
        when(patientRepo.getPatientByUserId(3)).thenReturn(patient);
        when(billRepo.getBillsByPatient(4)).thenReturn(List.of(bill));

        assertEquals(List.of(patient), service.getPatients());
        assertSame(patient, service.getPatientById(1));
        service.addPatient(patient);
        assertEquals(List.of(patient), service.findByDoctorId(2));
        assertSame(patient, service.getPatientByUserId(3));
        assertEquals(List.of(bill), service.getBillsByPatientId(4));
        verify(patientRepo).addPatient(patient);
    }

    @Test
    void getPatientByUsernameMapsPatientDtoWhenUserAndPatientExist() {
        Users user = new Users();
        user.setId(7);
        user.setFullName("Nguyễn Văn A");
        Patients patient = new Patients();
        patient.setId(5);
        patient.setPatientCode("P5");
        when(userRepo.getUserByUsername("patient")).thenReturn(user);
        when(patientRepo.getPatientByUserId(7)).thenReturn(patient);

        PatientDto dto = service.getPatientByUsername("patient");

        assertEquals(5, dto.getId());
        assertEquals("Nguyễn Văn A", dto.getFullName());
        assertEquals("P5", dto.getPatientCode());
    }

    @Test
    void getPatientByUsernameReturnsNullWhenMissingUserOrPatient() {
        when(userRepo.getUserByUsername("missing")).thenReturn(null);
        assertNull(service.getPatientByUsername("missing"));

        Users user = new Users();
        user.setId(8);
        when(userRepo.getUserByUsername("noPatient")).thenReturn(user);
        when(patientRepo.getPatientByUserId(8)).thenReturn(null);
        assertNull(service.getPatientByUsername("noPatient"));
    }
}
