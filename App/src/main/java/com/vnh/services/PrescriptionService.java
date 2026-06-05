package com.vnh.services;

import com.vnh.dto.PrescriptionRequest;
import com.vnh.pojo.Prescriptions;
import java.security.Principal;

public interface PrescriptionService {
    Prescriptions createPrescription(PrescriptionRequest prescriptionRequest, Principal principal);
    Prescriptions addPrescription(Prescriptions p); // Thêm hàm này
}
