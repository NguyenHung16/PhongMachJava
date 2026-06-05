package com.vnh.repositories;

import com.vnh.pojo.Doctors;
import java.util.List;

public interface DoctorRepository {
    List<Doctors> getDoctors();
    List<Doctors> getDoctorsBySpecialtyId(int specialtyId);
    Doctors getDoctorById(int id);
    void saveOrUpdate(Doctors d); // Thêm hàm này
    void deleteDoctor(int id);     // Thêm hàm này
}
