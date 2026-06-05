package com.vnh.controllers;

import com.vnh.pojo.Appointments;
import com.vnh.pojo.Doctors;
import com.vnh.pojo.Medicines;
import com.vnh.pojo.Specialties;
import com.vnh.pojo.Users;
import com.vnh.services.AppointmentService;
import com.vnh.services.DoctorService;
import com.vnh.services.MedicineServices;
import com.vnh.services.SpecialtyService;
import com.vnh.services.UserServices;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock private MedicineServices medicineService;
    @Mock private UserServices userService;
    @Mock private AppointmentService appointmentService;
    @Mock private SpecialtyService specialtyService;
    @Mock private DoctorService doctorService;
    @InjectMocks private AdminController controller;

    @Test
    void dashboardReturnsDashboardView() {
        assertEquals("admin/dashboard", controller.dashboard(mock(Model.class)));
    }

    @Test
    void medicinePagesPopulateModelAndRedirectAfterSave() {
        Model model = mock(Model.class);
        Medicines medicine = new Medicines();
        when(medicineService.getMedicines(null)).thenReturn(List.of(medicine));
        when(medicineService.getMedicineById(3)).thenReturn(medicine);

        assertEquals("admin/medicines-list", controller.listMedicines(model));
        assertEquals("admin/medicines-form", controller.addMedicineForm(model));
        assertEquals("admin/medicines-form", controller.editMedicineForm(3, model));

        verify(model).addAttribute("medicines", List.of(medicine));
        verify(model, atLeastOnce()).addAttribute("medicine", medicine);
    }

    @Test
    void userPagesPopulateModelAndSaveUser() {
        Model model = mock(Model.class);
        Users user = new Users();
        when(userService.getUsers(null)).thenReturn(List.of(user));
        Map<String, String> params = Map.of("username", "admin");

        assertEquals("admin/users-list", controller.manageUsers(model));
        assertEquals("admin/user-form", controller.addUserForm(model));
        assertEquals("redirect:/admin/users", controller.saveUser(params, null));

        verify(model).addAttribute("users", List.of(user));
        verify(userService).addUser(params, null);
    }

    @Test
    void doctorPagesPopulateModelAndHandleSaveDelete() {
        Model model = mock(Model.class);
        Doctors doctor = new Doctors();
        Specialties specialty = new Specialties();
        when(doctorService.getDoctors()).thenReturn(List.of(doctor));
        when(doctorService.getDoctorById(4)).thenReturn(doctor);
        when(specialtyService.getSpecialties()).thenReturn(List.of(specialty));
        Map<String, String> params = Map.of("fullName", "Dr Test");

        assertEquals("admin/doctors-list", controller.listDoctors(model));
        assertEquals("admin/doctor-form", controller.addDoctorForm(model));
        assertEquals("admin/doctor-form", controller.editDoctorForm(4, model));
        assertEquals("redirect:/admin/doctors", controller.saveDoctor(params, null));
        assertEquals("redirect:/admin/doctors", controller.deleteDoctor(4));

        verify(doctorService).saveOrUpdate(params, null);
        verify(doctorService).deleteDoctor(4);
    }

    @Test
    void specialtyPagesPopulateModelAndHandleSaveDelete() {
        Model model = mock(Model.class);
        Specialties specialty = new Specialties();
        when(specialtyService.getSpecialties()).thenReturn(List.of(specialty));
        when(specialtyService.getSpecialtyById(5)).thenReturn(specialty);

        assertEquals("admin/specialties-list", controller.listSpecialties(model));
        assertEquals("admin/specialty-form", controller.addSpecialtyForm(model));
        assertEquals("admin/specialty-form", controller.editSpecialtyForm(5, model));
        assertEquals("redirect:/admin/specialties", controller.saveSpecialty(specialty));
        assertEquals("redirect:/admin/specialties", controller.deleteSpecialty(5));

        verify(specialtyService).saveOrUpdate(specialty);
        verify(specialtyService).deleteSpecialty(5);
    }

    @Test
    void appointmentPagesPopulateModelAndUpdateStatusWhenAppointmentExists() {
        Model model = mock(Model.class);
        Appointments appointment = new Appointments();
        when(appointmentService.getAppointments()).thenReturn(List.of(appointment));
        when(appointmentService.getAppointmentById(6)).thenReturn(appointment);

        assertEquals("admin/appointments-list", controller.manageAppointments(model));
        assertEquals("redirect:/admin/appointments", controller.updateAppointmentStatus(6, "COMPLETED"));
        assertEquals("COMPLETED", appointment.getStatus());
        verify(appointmentService).saveAppointment(appointment);

        assertEquals("redirect:/admin/appointments", controller.updateAppointmentStatus(7, "CANCELLED"));
    }
}
