package com.vnh.services.impl;

import com.vnh.dto.MedicalRecordDTO;
import com.vnh.dto.PrescriptionItemDto;
import com.vnh.pojo.Appointments;
import com.vnh.pojo.MedicalRecords;
import com.vnh.pojo.Medicines;
import com.vnh.pojo.Patients;
import com.vnh.pojo.PrescriptionDetails;
import com.vnh.pojo.Prescriptions;
import com.vnh.pojo.Users;
import com.vnh.repositories.AppointmentRepository;
import com.vnh.repositories.MedicalRecordRepository;
import com.vnh.repositories.PatientRepository;
import com.vnh.services.PaymentService;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceImplTest {

    @Mock private MedicalRecordRepository medicalRecordRepo;
    @Mock private PatientRepository patientRepo;
    @Mock private AppointmentRepository appointmentRepo;
    @Mock private PaymentService paymentService;
    @InjectMocks private MedicalRecordServiceImpl service;

    @Test
    void getMedicalRecordsByUserIdMapsPrescriptions() {
        Users user = new Users();
        user.setFullName("Nguyen Hung");
        Patients resolvedPatient = new Patients();
        resolvedPatient.setId(4);
        Patients patient = new Patients();
        patient.setUserId(user);

        Medicines medicine = new Medicines();
        medicine.setName("Paracetamol");
        PrescriptionDetails detail = new PrescriptionDetails();
        detail.setMedicines(medicine);
        detail.setQuantity(2);
        detail.setInstruction("After meal");

        Prescriptions prescription = new Prescriptions();
        prescription.setPrescriptionDetailsCollection(List.of(detail));

        MedicalRecords record = new MedicalRecords();
        record.setId(10);
        record.setDiagnosis("Flu");
        record.setSymptoms("Cough");
        record.setTreatmentPlan("Rest");
        record.setCreatedAt(new Date());
        record.setPatientId(patient);
        record.setPrescriptionsCollection(List.of(prescription));

        when(patientRepo.getPatientByUserId(4)).thenReturn(resolvedPatient);
        when(medicalRecordRepo.getMedicalRecordsByPatientId(4)).thenReturn(List.of(record));

        List<MedicalRecordDTO> dtos = service.getMedicalRecordsByUserId(4);

        assertEquals(1, dtos.size());
        MedicalRecordDTO dto = dtos.get(0);
        assertEquals(10, dto.getId());
        assertEquals("Flu", dto.getDiagnosis());
        assertEquals("Cough", dto.getSymptoms());
        assertEquals("Rest", dto.getTreatmentPlan());
        assertEquals("Nguyen Hung", dto.getPatientName());
        assertEquals(1, dto.getPrescriptionItems().size());
        PrescriptionItemDto item = dto.getPrescriptionItems().get(0);
        assertEquals("Paracetamol", item.getMedicineName());
        assertEquals(2, item.getQuantity());
        assertEquals("After meal", item.getInstruction());
    }

    @Test
    void createMedicalRecordPersistsEntityWhenReferencesExist() {
        MedicalRecordDTO dto = MedicalRecordDTO.builder()
                .patientId(2)
                .appointmentId(5)
                .diagnosis("Flu")
                .symptoms("Cough")
                .treatmentPlan("Rest")
                .build();

        Patients patient = new Patients();
        patient.setId(2);
        Appointments appointment = new Appointments();
        appointment.setId(5);
        when(patientRepo.getPatientById(2)).thenReturn(patient);
        when(appointmentRepo.getAppointmentById(5)).thenReturn(appointment);

        MedicalRecordDTO created = service.createMedicalRecord(dto);

        ArgumentCaptor<MedicalRecords> captor = ArgumentCaptor.forClass(MedicalRecords.class);
        verify(medicalRecordRepo).save(captor.capture());
        MedicalRecords saved = captor.getValue();
        assertEquals("Flu", saved.getDiagnosis());
        assertEquals("Cough", saved.getSymptoms());
        assertEquals("Rest", saved.getTreatmentPlan());
        assertSame(patient, saved.getPatientId());
        assertSame(appointment, saved.getAppointmentId());
        assertNotNull(saved.getCreatedAt());
        assertEquals(2, created.getPatientId());
        assertEquals(5, created.getAppointmentId());
        assertEquals("Flu", created.getDiagnosis());
        verify(paymentService).createBill(5);
    }

    @Test
    void createMedicalRecordThrowsWhenReferencesMissing() {
        MedicalRecordDTO dto = MedicalRecordDTO.builder()
                .patientId(2)
                .appointmentId(5)
                .build();

        when(patientRepo.getPatientById(2)).thenReturn(null);
        when(appointmentRepo.getAppointmentById(5)).thenReturn(new Appointments());

        assertThrows(IllegalArgumentException.class, () -> service.createMedicalRecord(dto));
        verify(medicalRecordRepo, never()).save(any());
        verifyNoInteractions(paymentService);
    }

    @Test
    void getMedicalRecordsByPatientIdDelegates() {
        when(medicalRecordRepo.getMedicalRecordsByPatientId(7)).thenReturn(List.of(new MedicalRecords()));

        assertEquals(1, service.getMedicalRecordsByPatientId(7).size());
    }

    @Test
    void getMedicalRecordDtosByPatientIdMapsPrescriptionsForDoctorHistory() {
        Patients patient = new Patients();
        patient.setId(7);
        Users user = new Users();
        user.setFullName("Patient History");
        patient.setUserId(user);
        Medicines medicine = new Medicines();
        medicine.setName("Amoxicillin");
        PrescriptionDetails detail = new PrescriptionDetails();
        detail.setMedicines(medicine);
        detail.setQuantity(3);
        detail.setInstruction("After meal");
        Prescriptions prescription = new Prescriptions();
        prescription.setPrescriptionDetailsCollection(List.of(detail));
        MedicalRecords record = new MedicalRecords();
        record.setId(12);
        record.setPatientId(patient);
        record.setDiagnosis("Infection");
        record.setPrescriptionsCollection(List.of(prescription));
        when(medicalRecordRepo.getMedicalRecordsByPatientId(7)).thenReturn(List.of(record));

        List<MedicalRecordDTO> dtos = service.getMedicalRecordDtosByPatientId(7);

        assertEquals(1, dtos.size());
        assertEquals(7, dtos.get(0).getPatientId());
        assertEquals("Patient History", dtos.get(0).getPatientName());
        assertEquals(1, dtos.get(0).getPrescriptionItems().size());
        assertEquals("Amoxicillin", dtos.get(0).getPrescriptionItems().get(0).getMedicineName());
    }

    @Test
    void getMedicalRecordByIdDelegates() {
        MedicalRecords record = new MedicalRecords();
        when(medicalRecordRepo.getMedicalRecordById(7)).thenReturn(record);

        assertSame(record, service.getMedicalRecordById(7));
    }
}
