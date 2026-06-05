/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vnh.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class DoctorAppointmentDto {

    private Integer id;
    private String patientName;
    private String reason;
    private Long patientId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date appointmentDate;

    @JsonFormat(pattern = "HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date appointmentTime;

    // Default constructor
    public DoctorAppointmentDto() {
    }

    // Constructor with all fields
    public DoctorAppointmentDto(Integer id, String patientName, String reason, Date appointmentDate) {
        this.id = id;
        this.patientName = patientName;
        this.reason = reason;
        this.appointmentDate = appointmentDate;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getReason() {
        return reason;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public Date getAppointmentTime() {
        return appointmentTime;
    }
    public Long getPatientId(){
        return patientId;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setAppointmentTime(Date appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

}
