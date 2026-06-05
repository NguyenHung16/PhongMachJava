package com.vnh.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class AppointmentRequest {
    private Integer doctorId;
    private Integer patientId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date appointmentDate;

    @JsonFormat(pattern = "HH:mm")
    private Date appointmentTime;

    private String reason;
    private String status;

    // Constructors
    public AppointmentRequest() {}

    // Getters and Setters
    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Date getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(Date appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}