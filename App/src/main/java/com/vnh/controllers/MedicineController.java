package com.vnh.controllers;

import com.vnh.pojo.Medicines;
import com.vnh.services.MedicineServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MedicineController {

    @Autowired
    private MedicineServices mediService;

    // Xử lý việc thêm hoặc cập nhật thuốc từ giao diện Admin
    @PostMapping("/admin/medicines/save")
    public String addOrUpdate(@ModelAttribute(value = "medicine") Medicines m,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        this.mediService.addOrUpdateMedicine(m, imageFile);
        return "redirect:/admin/medicines"; // Chuyển hướng về danh sách thuốc trong Admin
    }
    
    // API xóa thuốc (nếu cần)
    @GetMapping("/admin/medicines/delete/{id}")
    public String delete(@PathVariable int id) {
        this.mediService.deleteMedicine(id);
        return "redirect:/admin/medicines";
    }
}
