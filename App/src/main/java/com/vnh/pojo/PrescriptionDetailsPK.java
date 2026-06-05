/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vnh.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author Nguyen Hung
 */
@Embeddable
public class PrescriptionDetailsPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "prescription_id")
    private int prescriptionId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "medicine_id")
    private int medicineId;

    public PrescriptionDetailsPK() {
    }

    public PrescriptionDetailsPK(int prescriptionId, int medicineId) {
        this.prescriptionId = prescriptionId;
        this.medicineId = medicineId;
    }

    public int getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(int prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) prescriptionId;
        hash += (int) medicineId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PrescriptionDetailsPK)) {
            return false;
        }
        PrescriptionDetailsPK other = (PrescriptionDetailsPK) object;
        if (this.prescriptionId != other.prescriptionId) {
            return false;
        }
        if (this.medicineId != other.medicineId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.vnh.pojo.PrescriptionDetailsPK[ prescriptionId=" + prescriptionId + ", medicineId=" + medicineId + " ]";
    }
    
}
