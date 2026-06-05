package com.vnh.services.impl;

import com.vnh.pojo.Appointments;
import com.vnh.pojo.Bills;
import com.vnh.repositories.AppointmentRepository;
import com.vnh.repositories.BillRepository;
import com.vnh.services.PaymentService;
import java.math.BigDecimal;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private BillRepository billRepo;
    
    @Autowired
    private AppointmentRepository appointmentRepo;

    @Override
    @Transactional
    public Bills createBill(int appointmentId) {
        Appointments app = this.appointmentRepo.getAppointmentById(appointmentId);
        if (app == null) return null;

        Bills existingBill = this.billRepo.getBillByAppointmentId(appointmentId);
        if (existingBill != null) return existingBill;

        Bills bill = new Bills();
        bill.setAppointmentId(app);
        bill.setPatientId(app.getPatientId());
        bill.setBillDate(new Date());
        
        BigDecimal defaultFee = new BigDecimal("100000"); 
        
        bill.setTotalAmount(defaultFee);
        bill.setPaymentStatus("UNPAID");

        return this.billRepo.addBill(bill);
    }

    @Override
    @Transactional
    public Appointments processAppointmentDeposit(int appointmentId) {
        Appointments app = this.appointmentRepo.getAppointmentById(appointmentId);
        if (app == null) return null;

        app.setStatus("CONFIRMED");
        this.appointmentRepo.saveAppointment(app);
        return app;
    }

    @Override
    @Transactional
    public boolean processPayment(int billId, String method) {
        return this.billRepo.updateStatus(billId, "PAID");
    }
}
