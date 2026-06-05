package com.vnh.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "appointments")
public class Appointments implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "appointment_date")
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date appointmentDate;
    
    @Column(name = "appointment_time")
    @Temporal(TemporalType.TIME)
    @JsonFormat(pattern = "HH:mm")
    private Date appointmentTime;
    
    @Size(max = 255)
    private String reason;

    @Size(min = 1, max = 20)
    private String status;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Doctors doctorId;

    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Patients patientId;

    @OneToMany(mappedBy = "appointmentId")
    @JsonIgnore
    private Collection<MedicalRecords> medicalRecordsCollection;

    @OneToMany(mappedBy = "appointmentId")
    @JsonIgnore
    private Collection<Bills> billsCollection;

    // Getters and Setters...
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }
    public Date getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(Date appointmentTime) { this.appointmentTime = appointmentTime; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Doctors getDoctorId() { return doctorId; }
    public void setDoctorId(Doctors doctorId) { this.doctorId = doctorId; }
    public Patients getPatientId() { return patientId; }
    public void setPatientId(Patients patientId) { this.patientId = patientId; }
}
