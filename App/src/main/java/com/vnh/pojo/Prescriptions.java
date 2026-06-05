package com.vnh.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "prescriptions")
@NamedQueries({
    @NamedQuery(name = "Prescriptions.findAll", query = "SELECT p FROM Prescriptions p"),
    @NamedQuery(name = "Prescriptions.findById", query = "SELECT p FROM Prescriptions p WHERE p.id = :id"),
    @NamedQuery(name = "Prescriptions.findByPrescriptionDate", query = "SELECT p FROM Prescriptions p WHERE p.prescriptionDate = :prescriptionDate")})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Prescriptions implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "prescription_date")
    @Temporal(TemporalType.DATE)
    private Date prescriptionDate;

    @Lob
    @Size(max = 65535)
    @Column(name = "notes")
    private String notes;

    // Đã thay đổi FetchType.EAGER thành FetchType.LAZY
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "prescriptions", fetch = FetchType.LAZY)
    private Collection<PrescriptionDetails> prescriptionDetailsCollection;

    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JsonIgnore
    private Doctors doctorId;

    @JoinColumn(name = "medical_record_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @JsonIgnore
    private MedicalRecords medicalRecordId;

    public Prescriptions() {
    }

    public Prescriptions(Integer id) {
        this.id = id;
    }

    public Prescriptions(Integer id, Date prescriptionDate) {
        this.id = id;
        this.prescriptionDate = prescriptionDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(Date prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Collection<PrescriptionDetails> getPrescriptionDetailsCollection() {
        return prescriptionDetailsCollection;
    }

    public void setPrescriptionDetailsCollection(Collection<PrescriptionDetails> prescriptionDetailsCollection) {
        this.prescriptionDetailsCollection = prescriptionDetailsCollection;
    }

    public Doctors getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Doctors doctorId) {
        this.doctorId = doctorId;
    }

    public MedicalRecords getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(MedicalRecords medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // ... (unchanged)
        if (!(object instanceof Prescriptions)) {
            return false;
        }
        Prescriptions other = (Prescriptions) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.vnh.pojo.Prescriptions[ id=" + id + " ]";
    }
}
