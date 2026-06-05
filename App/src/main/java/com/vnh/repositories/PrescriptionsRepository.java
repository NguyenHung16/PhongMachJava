package com.vnh.repositories;

import com.vnh.pojo.Prescriptions;
import java.util.List;

public interface PrescriptionsRepository {
    List<Prescriptions> getPrescriptions(); // Thêm hàm này
    void savePrescription(Prescriptions prescription);
    Prescriptions getPrescriptionById(int id);
}
