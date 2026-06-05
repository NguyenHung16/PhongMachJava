/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vnh.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.vnh.pojo.Medicines;
import com.vnh.repositories.MedicineRepository;
import com.vnh.services.MedicineServices;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Nguyen Hung
 */
@Service
public class MedicineServiceImpl implements MedicineServices{
    
    @Autowired
    private MedicineRepository mediRepo;
    @Autowired
    private Cloudinary cloudinary; // Thêm Cloudinary bean vào đây

    @Override
    public List<Medicines> getMedicines(Map<String, String> params) {
        return this.mediRepo.getMedicines(params);
    }

    @Override
    public Medicines getMedicineById(int id) {
        return this.mediRepo.getMedicineById(id);
    }

    // Sửa phương thức này để nhận thêm MultipartFile
    @Override
    public Medicines addOrUpdateMedicine(Medicines medicine, MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            try {
                Map res = this.cloudinary.uploader().upload(imageFile.getBytes(), 
                        ObjectUtils.asMap("resource_type", "auto"));
                medicine.setImageUrl(res.get("secure_url").toString());
            } catch (IOException ex) {
                // Xử lý lỗi khi tải ảnh
                System.err.println("Lỗi khi tải ảnh lên Cloudinary: " + ex.getMessage());
            }
        }
        return this.mediRepo.addOrUpdateMedicine(medicine);
    }

    @Override
    public void deleteMedicine(int id) {
        this.mediRepo.deleteMedicine(id);
    }

    @Override
    public List<Medicines> getExpiringMedicines(int daysThreshold) {
        return this.mediRepo.getExpiringMedicines(daysThreshold);
    }

    @Override
    public List<Medicines> getLowStockMedicines(int stockThreshold) {
        return this.mediRepo.getLowStockMedicines(stockThreshold);
    }
    
}