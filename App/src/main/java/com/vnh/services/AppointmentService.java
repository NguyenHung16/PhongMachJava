package com.vnh.services;

import com.vnh.dto.DoctorAppointmentDto;
import com.vnh.pojo.Appointments;
import java.util.List;

public interface AppointmentService {
    List<Appointments> getAppointments();
    Appointments getAppointmentById(int id);
    void saveAppointment(Appointments appointment);
    List<Appointments> getAppointmentsByDoctorId(Integer doctorId); // Đổi ở đây
    List<Appointments> getAppointmentsByPatientId(Integer patientId);
}
