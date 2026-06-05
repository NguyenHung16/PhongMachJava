package com.vnh.controllers;

import com.vnh.pojo.*;
import com.vnh.services.*;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private MedicineServices medicineService;
    @Autowired private UserServices userService;
    @Autowired private AppointmentService appointmentService;
    @Autowired private SpecialtyService specialtyService;
    @Autowired private DoctorService doctorService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) { return "admin/dashboard"; }

    // THUỐC
    @GetMapping("/medicines")
    public String listMedicines(Model model) {
        model.addAttribute("medicines", medicineService.getMedicines(null));
        return "admin/medicines-list";
    }

    @GetMapping("/medicines/add")
    public String addMedicineForm(Model model) {
        model.addAttribute("medicine", new Medicines());
        return "admin/medicines-form";
    }

    @GetMapping("/medicines/edit/{id}")
    public String editMedicineForm(@PathVariable int id, Model model) {
        model.addAttribute("medicine", medicineService.getMedicineById(id));
        return "admin/medicines-form";
    }

    // NGƯỜI DÙNG
    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getUsers(null));
        return "admin/users-list";
    }

    @GetMapping("/users/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new Users());
        return "admin/user-form";
    }

    @PostMapping("/users/save")
    public String saveUser(@RequestParam Map<String, String> params, @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        userService.addUser(params, avatar);
        return "redirect:/admin/users";
    }

    // BÁC SĨ
    @GetMapping("/doctors")
    public String listDoctors(Model model) {
        model.addAttribute("doctors", doctorService.getDoctors());
        return "admin/doctors-list";
    }

    @GetMapping("/doctors/add")
    public String addDoctorForm(Model model) {
        model.addAttribute("doctor", new Doctors());
        model.addAttribute("specialties", specialtyService.getSpecialties());
        return "admin/doctor-form";
    }

    @GetMapping("/doctors/edit/{id}")
    public String editDoctorForm(@PathVariable int id, Model model) {
        model.addAttribute("doctor", doctorService.getDoctorById(id));
        model.addAttribute("specialties", specialtyService.getSpecialties());
        return "admin/doctor-form";
    }

    @PostMapping("/doctors/save")
    public String saveDoctor(@RequestParam Map<String, String> params, @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        doctorService.saveOrUpdate(params, avatar);
        return "redirect:/admin/doctors";
    }

    @GetMapping("/doctors/delete/{id}")
    public String deleteDoctor(@PathVariable int id) {
        doctorService.deleteDoctor(id);
        return "redirect:/admin/doctors";
    }

    // CHUYÊN KHOA
    @GetMapping("/specialties")
    public String listSpecialties(Model model) {
        model.addAttribute("specialties", specialtyService.getSpecialties());
        return "admin/specialties-list";
    }

    @GetMapping("/specialties/add")
    public String addSpecialtyForm(Model model) {
        model.addAttribute("specialty", new Specialties());
        return "admin/specialty-form";
    }

    @GetMapping("/specialties/edit/{id}")
    public String editSpecialtyForm(@PathVariable int id, Model model) {
        model.addAttribute("specialty", specialtyService.getSpecialtyById(id));
        return "admin/specialty-form";
    }

    @PostMapping("/specialties/save")
    public String saveSpecialty(@ModelAttribute Specialties spec) {
        specialtyService.saveOrUpdate(spec);
        return "redirect:/admin/specialties";
    }

    @GetMapping("/specialties/delete/{id}")
    public String deleteSpecialty(@PathVariable int id) {
        specialtyService.deleteSpecialty(id);
        return "redirect:/admin/specialties";
    }

    // LỊCH HẸN
    @GetMapping("/appointments")
    public String manageAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.getAppointments());
        return "admin/appointments-list";
    }

    @GetMapping("/appointments/{id}/status")
    public String updateAppointmentStatus(@PathVariable int id, @RequestParam String status) {
        var app = appointmentService.getAppointmentById(id);
        if (app != null) { app.setStatus(status); appointmentService.saveAppointment(app); }
        return "redirect:/admin/appointments";
    }
}
