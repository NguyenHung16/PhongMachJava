/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.vnh.repositories;

import com.vnh.pojo.Medicines;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Nguyen Hung
 */
public interface MedicineRepository {
    List<Medicines> getMedicines(Map<String, String> params);
    Medicines getMedicineById(int id);
    Medicines addOrUpdateMedicine(Medicines medicine);
    void deleteMedicine(int id);
    List<Medicines> getExpiringMedicines(int daysThreshold);
    List<Medicines> getLowStockMedicines(int stockThreshold);
}
