package com.vnh.repositories;

import com.vnh.pojo.Patients;
import java.util.List;

public interface PatientRepository {
    List<Patients> getPatients(); // Thêm hàm này
    Patients getPatientById(int id);
    Patients getPatientByUserId(int userId);
    void addPatient(Patients p); // Đổi về void để khớp với logic implementation của tôi
    List<Patients> findByDoctorId(int doctorId);
}
