package com.vnh.services.impl;

import com.vnh.repositories.StatsRepository;
import com.vnh.services.StatsServices;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StatsServiceImpl implements StatsServices {

    @Autowired
    private StatsRepository statsRepo;

    @Override
    public List<Object[]> countPatientsByGender() {
        return statsRepo.countPatientsByGender();
    }

    @Override
    public List<Object[]> countPatientsByAgeGroup() {
        return statsRepo.countPatientsByAgeGroup();
    }

    @Override
    public List<Object[]> countPatientsBySpecialty() {
        return statsRepo.countPatientsBySpecialty();
    }

    @Override
    public List<Object[]> countServicesUsed() {
        return statsRepo.topUsedServices();
    }

    @Override
    public List<Object[]> countPopularDiseases() {
        return statsRepo.popularDiseases();
    }

    @Override
    public List<Object[]> getRevenueByTime(String time, int year) {
        return statsRepo.revenueByTime(year, time);
    }
}
