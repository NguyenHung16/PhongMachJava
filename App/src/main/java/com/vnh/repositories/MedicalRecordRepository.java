package com.vnh.repositories;

import com.vnh.pojo.MedicalRecords;
import java.util.List;

public interface MedicalRecordRepository {
    List<MedicalRecords> getMedicalRecords(); // Thêm hàm này
    MedicalRecords getMedicalRecordById(int id);
    void save(MedicalRecords medicalRecord);
    List<MedicalRecords> getMedicalRecordsByPatientId(int patientId);
}
