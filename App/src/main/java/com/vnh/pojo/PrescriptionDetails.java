/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vnh.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 *
 * @author Nguyen Hung
 */
@Entity
@Table(name = "prescription_details")
@NamedQueries({
    @NamedQuery(name = "PrescriptionDetails.findAll", query = "SELECT p FROM PrescriptionDetails p"),
    @NamedQuery(name = "PrescriptionDetails.findByPrescriptionId", query = "SELECT p FROM PrescriptionDetails p WHERE p.prescriptionDetailsPK.prescriptionId = :prescriptionId"),
    @NamedQuery(name = "PrescriptionDetails.findByMedicineId", query = "SELECT p FROM PrescriptionDetails p WHERE p.prescriptionDetailsPK.medicineId = :medicineId"),
    @NamedQuery(name = "PrescriptionDetails.findByQuantity", query = "SELECT p FROM PrescriptionDetails p WHERE p.quantity = :quantity")})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PrescriptionDetails implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
    protected PrescriptionDetailsPK prescriptionDetailsPK;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "quantity")
    private int quantity;
    
    @Lob
    @Size(max = 65535)
    @Column(name = "instruction")
    private String instruction;
    
    @JoinColumn(name = "medicine_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JsonIgnore
    private Medicines medicines;
    
    @JoinColumn(name = "prescription_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JsonIgnore
    private Prescriptions prescriptions;

    public PrescriptionDetails() {
    }

    public PrescriptionDetails(PrescriptionDetailsPK prescriptionDetailsPK) {
        this.prescriptionDetailsPK = prescriptionDetailsPK;
    }

    public PrescriptionDetails(PrescriptionDetailsPK prescriptionDetailsPK, int quantity) {
        this.prescriptionDetailsPK = prescriptionDetailsPK;
        this.quantity = quantity;
    }

    public PrescriptionDetails(int prescriptionId, int medicineId) {
        this.prescriptionDetailsPK = new PrescriptionDetailsPK(prescriptionId, medicineId);
    }

    public PrescriptionDetailsPK getPrescriptionDetailsPK() {
        return prescriptionDetailsPK;
    }

    public void setPrescriptionDetailsPK(PrescriptionDetailsPK prescriptionDetailsPK) {
        this.prescriptionDetailsPK = prescriptionDetailsPK;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Medicines getMedicines() {
        return medicines;
    }

    public void setMedicines(Medicines medicines) {
        this.medicines = medicines;
    }

    public Prescriptions getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(Prescriptions prescriptions) {
        this.prescriptions = prescriptions;
    }
    
    // Thêm các getters tùy chỉnh để lấy dữ liệu cho frontend
    public String getMedicineName() {
        return this.medicines != null ? this.medicines.getName() : null;
    }
    
    public String getDosage() {
        return this.instruction;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (prescriptionDetailsPK != null ? prescriptionDetailsPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // ... (unchanged)
        if (!(object instanceof PrescriptionDetails)) {
            return false;
        }
        PrescriptionDetails other = (PrescriptionDetails) object;
        if ((this.prescriptionDetailsPK == null && other.prescriptionDetailsPK != null) || (this.prescriptionDetailsPK != null && !this.prescriptionDetailsPK.equals(other.prescriptionDetailsPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.vnh.pojo.PrescriptionDetails[ prescriptionDetailsPK=" + prescriptionDetailsPK + " ]";
    }
}