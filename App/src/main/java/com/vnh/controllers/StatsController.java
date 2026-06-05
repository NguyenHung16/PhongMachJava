package com.vnh.controllers;

import com.vnh.services.StatsServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StatsController {

    @Autowired
    private StatsServices statsService;

    @GetMapping("/stats")
    public String stats(Model model,
            @RequestParam(required = false, defaultValue = "MONTH") String time,
            @RequestParam(required = false, defaultValue = "2025") int year) {

        // Thêm dữ liệu vào Model để truyền tới view
        model.addAttribute("patientsByGender", this.statsService.countPatientsByGender());
        model.addAttribute("patientsByAgeGroup", this.statsService.countPatientsByAgeGroup());
        model.addAttribute("patientsBySpecialty", this.statsService.countPatientsBySpecialty());
        model.addAttribute("servicesUsed", this.statsService.countServicesUsed());
        model.addAttribute("popularDiseases", this.statsService.countPopularDiseases());
        model.addAttribute("revenueByTime", this.statsService.getRevenueByTime(time, year));

        // Thêm các tham số để hiển thị trên form lọc
        model.addAttribute("currentYear", year);
        model.addAttribute("currentTime", time);

        // Trả về tên của view template (ví dụ: stats.html)
        return "stats";
    }
}
