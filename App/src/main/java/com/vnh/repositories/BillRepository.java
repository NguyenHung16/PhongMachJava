package com.vnh.repositories;

import com.vnh.pojo.Bills;
import java.math.BigDecimal;
import java.util.List;

public interface BillRepository {
    Bills addBill(Bills b);
    Bills getBillById(int id);
    Bills getBillByAppointmentId(int appointmentId);
    List<Bills> getBillsByPatient(int patientId);
    List<Bills> getUnpaidBillsByPatient(int patientId);
    boolean updateTotalAmount(int billId, BigDecimal totalAmount);
    boolean updateStatus(int billId, String status);
}
