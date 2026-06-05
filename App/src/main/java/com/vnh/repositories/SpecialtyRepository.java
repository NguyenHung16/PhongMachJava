package com.vnh.repositories;

import com.vnh.pojo.Specialties;
import java.util.List;

public interface SpecialtyRepository {
    List<Specialties> getSpecialties();
    Specialties getSpecialtyById(int id);
    void saveOrUpdate(Specialties specialty);
    void deleteSpecialty(int id);
}
