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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
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
@Table(name = "doctors")
@NamedQueries({
    @NamedQuery(name = "Doctors.findAll", query = "SELECT d FROM Doctors d"),
    @NamedQuery(name = "Doctors.findById", query = "SELECT d FROM Doctors d WHERE d.id = :id"),
    @NamedQuery(name = "Doctors.findByDoctorCode", query = "SELECT d FROM Doctors d WHERE d.doctorCode = :doctorCode"),
    @NamedQuery(name = "Doctors.findByQualification", query = "SELECT d FROM Doctors d WHERE d.qualification = :qualification")})
public class Doctors implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "doctor_code")
    private String doctorCode;
    @Size(max = 100)
    @Column(name = "qualification")
    private String qualification;
    @Lob
    @Size(max = 65535)
    @Column(name = "biography")
    private String biography;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "doctorId")
    @JsonIgnore // Tránh đệ quy
    private Collection<Appointments> appointmentsCollection;
    
    @JoinColumn(name = "specialty_id", referencedColumnName = "id")
    @ManyToOne
    private Specialties specialtyId;
    
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Users userId;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "doctorId")
    @JsonIgnore // Tránh đệ quy
    private Collection<Prescriptions> prescriptionsCollection;

    public Doctors() {
    }

    public Doctors(Integer id) {
        this.id = id;
    }

    public Doctors(Integer id, String doctorCode) {
        this.id = id;
        this.doctorCode = doctorCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDoctorCode() {
        return doctorCode;
    }

    public void setDoctorCode(String doctorCode) {
        this.doctorCode = doctorCode;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public Collection<Appointments> getAppointmentsCollection() {
        return appointmentsCollection;
    }

    public void setAppointmentsCollection(Collection<Appointments> appointmentsCollection) {
        this.appointmentsCollection = appointmentsCollection;
    }

    public Specialties getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(Specialties specialtyId) {
        this.specialtyId = specialtyId;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users users) {
        this.userId = users;
    }
    
    public Users getUsers() {
        return userId;
    }

    public void setUsers(Users users) {
        this.userId = users;
    }

    public Collection<Prescriptions> getPrescriptionsCollection() {
        return prescriptionsCollection;
    }

    public void setPrescriptionsCollection(Collection<Prescriptions> prescriptionsCollection) {
        this.prescriptionsCollection = prescriptionsCollection;
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
        if (!(object instanceof Doctors)) {
            return false;
        }
        Doctors other = (Doctors) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.vnh.pojo.Doctors[ id=" + id + " ]";
    }
    
}
