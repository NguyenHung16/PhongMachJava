package com.vnh.repositories.impl;

import com.vnh.pojo.Bills;
import com.vnh.repositories.BillRepository;
import java.math.BigDecimal;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class BillRepositoryImpl implements BillRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Bills addBill(Bills b) {
        this.entityManager.persist(b);
        return b;
    }

    @Override
    public Bills getBillById(int id) {
        try {
            return this.entityManager.createQuery("SELECT b FROM Bills b JOIN FETCH b.patientId p JOIN FETCH p.userId WHERE b.id = :id", Bills.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Bills getBillByAppointmentId(int appointmentId) {
        try {
            return this.entityManager.createQuery("SELECT b FROM Bills b JOIN FETCH b.patientId p JOIN FETCH p.userId WHERE b.appointmentId.id = :appointmentId", Bills.class)
                    .setParameter("appointmentId", appointmentId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Bills> getBillsByPatient(int patientId) {
        return this.entityManager.createQuery("SELECT b FROM Bills b JOIN FETCH b.patientId p JOIN FETCH p.userId WHERE p.id = :patientId", Bills.class)
                .setParameter("patientId", patientId)
                .getResultList();
    }

    @Override
    public List<Bills> getUnpaidBillsByPatient(int patientId) {
        return this.entityManager.createQuery("SELECT b FROM Bills b JOIN FETCH b.patientId p JOIN FETCH p.userId WHERE p.id = :patientId AND b.paymentStatus = 'UNPAID'", Bills.class)
                .setParameter("patientId", patientId)
                .getResultList();
    }

    @Override
    public boolean updateTotalAmount(int billId, BigDecimal totalAmount) {
        Bills b = this.entityManager.find(Bills.class, billId);
        if (b != null) {
            b.setTotalAmount(totalAmount);
            this.entityManager.merge(b);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateStatus(int billId, String status) {
        Bills b = this.entityManager.find(Bills.class, billId);
        if (b != null) {
            b.setPaymentStatus(status);
            if ("PAID".equals(status)) {
                b.setBillDate(new java.util.Date());
            }
            this.entityManager.merge(b);
            return true;
        }
        return false;
    }
}
