/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vnh.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author Nguyen Hung
 */
@Entity
@Table(name = "patients")
@NamedQueries({
    @NamedQuery(name = "Patients.findAll", query = "SELECT p FROM Patients p"),
    @NamedQuery(name = "Patients.findById", query = "SELECT p FROM Patients p WHERE p.id = :id"),
    @NamedQuery(name = "Patients.findByPatientCode", query = "SELECT p FROM Patients p WHERE p.patientCode = :patientCode")})
public class Patients implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "patient_code")
    private String patientCode;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "patientId")
    @JsonIgnore // Tránh đệ quy JSON
    private Collection<Appointments> appointmentsCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "patientId")
    @JsonIgnore // Tránh đệ quy JSON
    private Collection<MedicalRecords> medicalRecordsCollection;
    
    @MapsId
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Users userId;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "patientId")
    @JsonIgnore // Tránh đệ quy JSON
    private Collection<Bills> billsCollection;

    public Patients() {
    }

    public Patients(Integer id) {
        this.id = id;
    }

    public Patients(Integer id, String patientCode) {
        this.id = id;
        this.patientCode = patientCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

    public Collection<Appointments> getAppointmentsCollection() {
        return appointmentsCollection;
    }

    public void setAppointmentsCollection(Collection<Appointments> appointmentsCollection) {
        this.appointmentsCollection = appointmentsCollection;
    }

    public Collection<MedicalRecords> getMedicalRecordsCollection() {
        return medicalRecordsCollection;
    }

    public void setMedicalRecordsCollection(Collection<MedicalRecords> medicalRecordsCollection) {
        this.medicalRecordsCollection = medicalRecordsCollection;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users users) {
        this.userId = users;
    }

    public Collection<Bills> getBillsCollection() {
        return billsCollection;
    }

    public void setBillsCollection(Collection<Bills> billsCollection) {
        this.billsCollection = billsCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Patients)) {
            return false;
        }
        Patients other = (Patients) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.vnh.pojo.Patients[ id=" + id + " ]";
    }
    
}
