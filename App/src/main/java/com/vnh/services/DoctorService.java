package com.vnh.services;

import com.vnh.pojo.Doctors;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface DoctorService {
    List<Doctors> getDoctors();
    List<Doctors> getDoctorsBySpecialtyId(int specialtyId);
    Doctors getDoctorById(int id);
    void saveOrUpdate(Map<String, String> params, MultipartFile avatar); // Tạo/Sửa bác sĩ kèm User
    void deleteDoctor(int id);
}
