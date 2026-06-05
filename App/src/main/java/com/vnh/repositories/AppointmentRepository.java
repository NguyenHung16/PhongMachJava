/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.vnh.repositories;

import com.vnh.pojo.Appointments;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Nguyen Hung
 */
public interface AppointmentRepository {

    List<Appointments> getAppointments();

    Appointments getAppointmentById(int id);

    void saveAppointment(Appointments appointment);

    boolean existsActiveSlot(int doctorId, Date appointmentDate, Date appointmentTime);

    List<Appointments> findByDoctorId_Id(Integer doctorId);

    List<Appointments> findByPatientId_Id(Integer patientId);
}
