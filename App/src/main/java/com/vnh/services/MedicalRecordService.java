package com.vnh.services;

import com.vnh.dto.MedicalRecordDTO;
import com.vnh.pojo.MedicalRecords;
import java.util.List;

public interface MedicalRecordService {
    List<MedicalRecordDTO> getMedicalRecordsByUserId(int userId);
    List<MedicalRecordDTO> getMedicalRecordDtosByPatientId(int patientId);
    MedicalRecordDTO createMedicalRecord(MedicalRecordDTO medicalRecordDto);
    List<MedicalRecords> getMedicalRecordsByPatientId(int patientId);
    MedicalRecords getMedicalRecordById(int id); // Thêm hàm này
}
