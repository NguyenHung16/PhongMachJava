/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vnh.dto;

import java.util.List;

/**
 *
 * @author Nguyen Hung
 */
public class PrescriptionRequest {
     private int medicalRecordId;
    private List<MedicineRequest> medicines;

    public int getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(int medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public List<MedicineRequest> getMedicines() {
        return medicines;
    }

    public void setMedicines(List<MedicineRequest> medicines) {
        this.medicines = medicines;
    }
}
