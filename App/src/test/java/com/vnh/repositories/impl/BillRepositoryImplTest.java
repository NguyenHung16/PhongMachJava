package com.vnh.repositories.impl;

import com.vnh.pojo.Bills;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillRepositoryImplTest {

    @Mock private EntityManager entityManager;
    @Mock private TypedQuery<Bills> billQuery;
    @InjectMocks private BillRepositoryImpl repository;

    @Test
    void addBillPersistsAndReturnsSameInstance() {
        Bills bill = new Bills();

        assertSame(bill, repository.addBill(bill));
        verify(entityManager).persist(bill);
    }

    @Test
    void getBillByIdReturnsBill() {
        Bills bill = new Bills();
        when(entityManager.createQuery(anyString(), eq(Bills.class))).thenReturn(billQuery);
        when(billQuery.setParameter("id", 4)).thenReturn(billQuery);
        when(billQuery.getSingleResult()).thenReturn(bill);

        assertSame(bill, repository.getBillById(4));
    }

    @Test
    void getBillByIdReturnsNullOnException() {
        when(entityManager.createQuery(anyString(), eq(Bills.class))).thenThrow(new RuntimeException("boom"));

        assertNull(repository.getBillById(4));
    }

    @Test
    void getBillByAppointmentIdDelegatesToDepositTypeQuery() {
        Bills bill = new Bills();
        when(entityManager.createQuery(anyString(), eq(Bills.class))).thenReturn(billQuery);
        when(billQuery.setParameter(anyString(), any())).thenReturn(billQuery);
        when(billQuery.getSingleResult()).thenReturn(bill);

        assertSame(bill, repository.getBillByAppointmentId(9));
    }

    @Test
    void getBillsByPatientReturnsList() {
        List<Bills> bills = List.of(new Bills());
        when(entityManager.createQuery(anyString(), eq(Bills.class))).thenReturn(billQuery);
        when(billQuery.setParameter("patientId", 12)).thenReturn(billQuery);
        when(billQuery.getResultList()).thenReturn(bills);

        assertSame(bills, repository.getBillsByPatient(12));
    }

    @Test
    void getUnpaidBillsByPatientReturnsList() {
        List<Bills> bills = List.of(new Bills());
        when(entityManager.createQuery(anyString(), eq(Bills.class))).thenReturn(billQuery);
        when(billQuery.setParameter("patientId", 12)).thenReturn(billQuery);
        when(billQuery.getResultList()).thenReturn(bills);

        assertSame(bills, repository.getUnpaidBillsByPatient(12));
    }

    @Test
    void updateStatusUpdatesBillWhenFound() {
        Bills bill = new Bills();
        bill.setPaymentStatus("UNPAID");
        when(entityManager.find(Bills.class, 2)).thenReturn(bill);

        assertTrue(repository.updateStatus(2, "PAID"));
        assertEquals("PAID", bill.getPaymentStatus());
        verify(entityManager).merge(bill);
    }

    @Test
    void updateStatusReturnsFalseWhenBillMissing() {
        when(entityManager.find(Bills.class, 2)).thenReturn(null);

        assertFalse(repository.updateStatus(2, "PAID"));
    }
}
