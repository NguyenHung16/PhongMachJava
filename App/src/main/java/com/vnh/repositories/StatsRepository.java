package com.vnh.repositories;

import java.util.List;

public interface StatsRepository {
    List<Object[]> revenueByTime(int year, String time); // Đồng bộ tham số và tên
    List<Object[]> countPatientsByGender();
    List<Object[]> countPatientsByAgeGroup();
    List<Object[]> countPatientsBySpecialty();
    List<Object[]> topUsedServices();  // Đồng bộ tên
    List<Object[]> popularDiseases();  // Đồng bộ tên
}
