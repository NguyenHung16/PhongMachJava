package com.vnh.controllers;

import com.vnh.services.MedicineServices;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@ControllerAdvice
public class IndexController {

    @Autowired
    private MedicineServices mediService;

    @RequestMapping("/")
    public String index(Model model, @RequestParam Map<String, String> params) {
        model.addAttribute("medicines", this.mediService.getMedicines(params));
        model.addAttribute("expiringMedicines", this.mediService.getExpiringMedicines(30));
        model.addAttribute("lowStockMedicines", this.mediService.getLowStockMedicines(20));
        return "index";
    }
}
