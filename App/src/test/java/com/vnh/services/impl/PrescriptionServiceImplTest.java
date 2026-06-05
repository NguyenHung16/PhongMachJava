package com.vnh.services.impl;

import com.vnh.dto.MedicineRequest;
import com.vnh.dto.PrescriptionRequest;
import com.vnh.pojo.Appointments;
import com.vnh.pojo.Bills;
import com.vnh.pojo.Doctors;
import com.vnh.pojo.MedicalRecords;
import com.vnh.pojo.Medicines;
import com.vnh.pojo.PrescriptionDetails;
import com.vnh.pojo.Prescriptions;
import com.vnh.pojo.Users;
import com.vnh.repositories.AppointmentRepository;
import com.vnh.repositories.BillRepository;
import com.vnh.repositories.MedicalRecordRepository;
import com.vnh.repositories.MedicineRepository;
import com.vnh.repositories.PrescriptionDetailsRepository;
import com.vnh.repositories.PrescriptionsRepository;
import com.vnh.repositories.UserRepository;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceImplTest {

    @Mock private PrescriptionsRepository prescriptionsRepository;
    @Mock private UserRepository userRepository;
    @Mock private MedicalRecordRepository medicalRecordsRepository;
    @Mock private PrescriptionDetailsRepository prescriptionDetailsRepository;
    @Mock private MedicineRepository medicineRepository;
    @Mock private BillRepository billRepository;
    @Mock private AppointmentRepository appointmentRepository;
    @InjectMocks private PrescriptionServiceImpl service;

    @Test
    void createPrescriptionThrowsWhenUserMissing() {
        Principal principal = () -> "doctor1";
        when(userRepository.getUserByUsername("doctor1")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> service.createPrescription(new PrescriptionRequest(), principal));
    }

    @Test
    void createPrescriptionThrowsWhenUserIsNotDoctor() {
        Principal principal = () -> "doctor1";
        Users user = new Users();
        when(userRepository.getUserByUsername("doctor1")).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () -> service.createPrescription(new PrescriptionRequest(), principal));
    }

    @Test
    void createPrescriptionPersistsDetailsUpdatesBillAndCompletesAppointment() {
        Principal principal = () -> "doctor1";
        Users user = new Users();
        Doctors doctor = new Doctors();
        user.setDoctors(doctor);
        when(userRepository.getUserByUsername("doctor1")).thenReturn(user);

        Appointments appointment = new Appointments();
        appointment.setId(4);
        MedicalRecords medicalRecord = new MedicalRecords();
        medicalRecord.setAppointmentId(appointment);
        when(medicalRecordsRepository.getMedicalRecordById(10)).thenReturn(medicalRecord);

        doAnswer(invocation -> {
            Prescriptions p = invocation.getArgument(0);
            p.setId(77);
            return null;
        }).when(prescriptionsRepository).savePrescription(any(Prescriptions.class));

        Medicines validMedicine = new Medicines();
        validMedicine.setId(5);
        validMedicine.setPrice(10000.0);
        when(medicineRepository.getMedicineById(5)).thenReturn(validMedicine);
        when(medicineRepository.getMedicineById(99)).thenReturn(null);

        Bills bill = new Bills();
        bill.setId(8);
        when(billRepository.getBillByAppointmentId(4)).thenReturn(bill);

        PrescriptionRequest request = new PrescriptionRequest();
        request.setMedicalRecordId(10);
        MedicineRequest valid = medicineRequest(5, "2", "2 lần/ngày", "Sau ăn");
        MedicineRequest invalidDosage = medicineRequest(5, "abc", "1 lần/ngày", "Trước khi ngủ");
        MedicineRequest missing = medicineRequest(99, "1", "1 lần/ngày", "Bỏ qua");
        request.setMedicines(List.of(valid, invalidDosage, missing));

        Prescriptions result = service.createPrescription(request, principal);

        assertNotNull(result.getPrescriptionDate());
        assertSame(medicalRecord, result.getMedicalRecordId());
        assertSame(doctor, result.getDoctorId());

        ArgumentCaptor<PrescriptionDetails> captor = ArgumentCaptor.forClass(PrescriptionDetails.class);
        verify(prescriptionDetailsRepository, times(2)).SavePrescriptionDetails(captor.capture());
        List<PrescriptionDetails> savedDetails = captor.getAllValues();

        PrescriptionDetails first = savedDetails.get(0);
        assertEquals(77, first.getPrescriptionDetailsPK().getPrescriptionId());
        assertEquals(5, first.getPrescriptionDetailsPK().getMedicineId());
        assertEquals(2, first.getQuantity());
        assertEquals("Tần suất: 2 lần/ngày. Sau ăn", first.getInstruction());

        PrescriptionDetails second = savedDetails.get(1);
        assertEquals(0, second.getQuantity());
        assertEquals("Tần suất: 1 lần/ngày. Trước khi ngủ", second.getInstruction());
        assertEquals("COMPLETED", appointment.getStatus());
        verify(billRepository).updateTotalAmount(8, new BigDecimal("120000.0"));
        verify(appointmentRepository).saveAppointment(appointment);
    }

    @Test
    void addPrescriptionDelegatesToRepository() {
        Prescriptions prescription = new Prescriptions();

        assertSame(prescription, service.addPrescription(prescription));
        verify(prescriptionsRepository).savePrescription(prescription);
    }

    private MedicineRequest medicineRequest(int medicineId, String dosage, String frequency, String instructions) {
        MedicineRequest request = new MedicineRequest();
        request.setMedicineId(medicineId);
        request.setDosage(dosage);
        request.setFrequency(frequency);
        request.setInstructions(instructions);
        return request;
    }
}
