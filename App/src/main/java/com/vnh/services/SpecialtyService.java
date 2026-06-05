package com.vnh.services;

import com.vnh.pojo.Specialties;
import java.util.List;

public interface SpecialtyService {
    List<Specialties> getSpecialties();
    Specialties getSpecialtyById(int id);
    void saveOrUpdate(Specialties specialty);
    void deleteSpecialty(int id);
}
