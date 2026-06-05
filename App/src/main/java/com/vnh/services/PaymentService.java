package com.vnh.services;

import com.vnh.pojo.Bills;
import com.vnh.pojo.Appointments;

public interface PaymentService {
    Bills createBill(int appointmentId);
    Appointments processAppointmentDeposit(int appointmentId);
    boolean processPayment(int billId, String method);
}
