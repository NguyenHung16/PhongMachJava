package com.vnh.services.impl;

import com.vnh.pojo.Appointments;
import com.vnh.pojo.Bills;
import com.vnh.pojo.Patients;
import com.vnh.repositories.AppointmentRepository;
import com.vnh.repositories.BillRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock private BillRepository billRepo;
    @Mock private AppointmentRepository appointmentRepo;
    @InjectMocks private PaymentServiceImpl service;

    @Test
    void createBillReturnsNullWhenAppointmentMissing() {
        when(appointmentRepo.getAppointmentById(3)).thenReturn(null);

        assertNull(service.createBill(3));
        verifyNoInteractions(billRepo);
    }

    @Test
    void createBillCreatesBillWithDefaults() {
        Appointments appointment = new Appointments();
        Patients patient = new Patients();
        appointment.setPatientId(patient);
        when(appointmentRepo.getAppointmentById(3)).thenReturn(appointment);
        when(billRepo.addBill(any(Bills.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bills created = service.createBill(3);

        ArgumentCaptor<Bills> captor = ArgumentCaptor.forClass(Bills.class);
        verify(billRepo).addBill(captor.capture());
        Bills bill = captor.getValue();

        assertSame(bill, created);
        assertSame(appointment, bill.getAppointmentId());
        assertSame(patient, bill.getPatientId());
        assertEquals(new BigDecimal("100000"), bill.getTotalAmount());
        assertEquals("UNPAID", bill.getPaymentStatus());
        assertNotNull(bill.getBillDate());
    }

    @Test
    void createBillReturnsExistingBillWhenAppointmentAlreadyHasBill() {
        Appointments appointment = new Appointments();
        Bills existingBill = new Bills();
        existingBill.setId(9);
        when(appointmentRepo.getAppointmentById(3)).thenReturn(appointment);
        when(billRepo.getBillByAppointmentId(3)).thenReturn(existingBill);

        assertSame(existingBill, service.createBill(3));
        verify(billRepo, never()).addBill(any());
    }

    @Test
    void processAppointmentDepositConfirmsAppointment() {
        Appointments appointment = new Appointments();
        appointment.setStatus("AWAITING_DEPOSIT");
        when(appointmentRepo.getAppointmentById(3)).thenReturn(appointment);

        Appointments confirmed = service.processAppointmentDeposit(3);

        assertSame(appointment, confirmed);
        assertEquals("CONFIRMED", appointment.getStatus());
        verify(appointmentRepo).saveAppointment(appointment);
    }

    @Test
    void processAppointmentDepositReturnsNullWhenAppointmentMissing() {
        when(appointmentRepo.getAppointmentById(3)).thenReturn(null);

        assertNull(service.processAppointmentDeposit(3));
        verify(appointmentRepo, never()).saveAppointment(any());
    }

    @Test
    void processPaymentDelegatesToBillRepository() {
        when(billRepo.updateStatus(5, "PAID")).thenReturn(true);

        assertTrue(service.processPayment(5, "bank"));
        verify(billRepo).updateStatus(5, "PAID");
    }
}
