/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.vnh.services;

import com.vnh.pojo.Medicines;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Nguyen Hung
 */
public interface MedicineServices {

    List<Medicines> getMedicines(Map<String, String> params);

    Medicines getMedicineById(int id);

    // Thêm tham số MultipartFile để xử lý ảnh
    Medicines addOrUpdateMedicine(Medicines medicine, MultipartFile imageFile);

    void deleteMedicine(int id);

    List<Medicines> getExpiringMedicines(int daysThreshold);

    List<Medicines> getLowStockMedicines(int stockThreshold);
}