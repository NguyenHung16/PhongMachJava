package com.vnh.services;

import com.vnh.dto.PatientDto;
import com.vnh.pojo.Bills;
import com.vnh.pojo.Patients;
import java.util.List;

public interface PatientService {
    List<Patients> getPatients();
    Patients getPatientById(int id);
    void addPatient(Patients p); // Đổi thành void để khớp repo
    List<Patients> findByDoctorId(int doctorId);
    Patients getPatientByUserId(int userId);
    List<Bills> getBillsByPatientId(int patientId);
    PatientDto getPatientByUsername(String username);
}
