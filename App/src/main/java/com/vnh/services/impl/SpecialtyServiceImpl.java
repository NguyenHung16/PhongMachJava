package com.vnh.services.impl;

import com.vnh.pojo.Specialties;
import com.vnh.repositories.SpecialtyRepository;
import com.vnh.services.SpecialtyService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SpecialtyServiceImpl implements SpecialtyService {

    @Autowired
    private SpecialtyRepository specialtyRepo;

    @Override
    public List<Specialties> getSpecialties() {
        return specialtyRepo.getSpecialties();
    }

    @Override
    public Specialties getSpecialtyById(int id) {
        return specialtyRepo.getSpecialtyById(id);
    }

    @Override
    public void saveOrUpdate(Specialties specialty) {
        specialtyRepo.saveOrUpdate(specialty);
    }

    @Override
    public void deleteSpecialty(int id) {
        specialtyRepo.deleteSpecialty(id);
    }
}
