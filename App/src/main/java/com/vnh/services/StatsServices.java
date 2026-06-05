/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.vnh.services;

import java.util.List;

/**
 *
 * @author Nguyen Hung
 */
public interface StatsServices {

    List<Object[]> countPatientsByGender();

    List<Object[]> countPatientsByAgeGroup();

    List<Object[]> countPatientsBySpecialty();

    List<Object[]> countServicesUsed();

    List<Object[]> countPopularDiseases();

    List<Object[]> getRevenueByTime(String time, int year);
}
