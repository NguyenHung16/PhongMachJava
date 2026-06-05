package com.vnh.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.vnh.pojo.Doctors;
import com.vnh.pojo.Specialties;
import com.vnh.pojo.Users;
import com.vnh.repositories.DoctorRepository;
import com.vnh.repositories.SpecialtyRepository;
import com.vnh.repositories.UserRepository;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {

    @Mock private DoctorRepository doctorRepo;
    @Mock private UserRepository userRepo;
    @Mock private SpecialtyRepository specialtyRepo;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private Cloudinary cloudinary;
    @InjectMocks private DoctorServiceImpl service;

    @Test
    void delegatesDoctorQueriesAndDelete() {
        Doctors doctor = new Doctors();
        when(doctorRepo.getDoctors()).thenReturn(List.of(doctor));
        when(doctorRepo.getDoctorsBySpecialtyId(2)).thenReturn(List.of(doctor));
        when(doctorRepo.getDoctorById(3)).thenReturn(doctor);

        assertEquals(List.of(doctor), service.getDoctors());
        assertEquals(List.of(doctor), service.getDoctorsBySpecialtyId(2));
        assertSame(doctor, service.getDoctorById(3));
        service.deleteDoctor(3);
        verify(doctorRepo).deleteDoctor(3);
    }

    @Test
    void saveOrUpdateCreatesDoctorUserWithDefaultPasswordAndSpecialty() {
        Map<String, String> params = baseParams();
        when(passwordEncoder.encode("123456")).thenReturn("encoded");
        when(userRepo.addUser(any(Users.class))).thenAnswer(invocation -> {
            Users user = invocation.getArgument(0);
            user.setId(10);
            return user;
        });
        when(doctorRepo.getDoctorById(10)).thenReturn(null);
        Specialties specialty = new Specialties();
        when(specialtyRepo.getSpecialtyById(5)).thenReturn(specialty);
        MultipartFile avatar = mock(MultipartFile.class);
        when(avatar.isEmpty()).thenReturn(true);

        service.saveOrUpdate(params, avatar);

        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
        verify(userRepo).addUser(userCaptor.capture());
        Users user = userCaptor.getValue();
        assertEquals("DOCTOR", user.getRole());
        assertEquals("encoded", user.getPassword());
        assertEquals("Bác sĩ A", user.getFullName());

        ArgumentCaptor<Doctors> doctorCaptor = ArgumentCaptor.forClass(Doctors.class);
        verify(doctorRepo).saveOrUpdate(doctorCaptor.capture());
        Doctors doctor = doctorCaptor.getValue();
        assertEquals(10, doctor.getId());
        assertEquals("DOC10", doctor.getDoctorCode());
        assertSame(specialty, doctor.getSpecialtyId());
    }

    @Test
    void saveOrUpdateUpdatesExistingDoctorAndUploadsAvatar() throws IOException {
        Map<String, String> params = baseParams();
        params.put("id", "8");
        Users user = new Users();
        user.setId(8);
        Doctors doctor = new Doctors();
        Uploader uploader = mock(Uploader.class);
        MultipartFile avatar = mock(MultipartFile.class);
        when(userRepo.getUserById(8)).thenReturn(user);
        when(avatar.isEmpty()).thenReturn(false);
        when(avatar.getBytes()).thenReturn(new byte[]{1});
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(Map.of("secure_url", "https://img.test/doc.png"));
        when(doctorRepo.getDoctorById(8)).thenReturn(doctor);
        when(specialtyRepo.getSpecialtyById(5)).thenReturn(new Specialties());

        service.saveOrUpdate(params, avatar);

        assertEquals("https://img.test/doc.png", user.getAvatar());
        verify(userRepo).updateUser(user);
        verify(doctorRepo).saveOrUpdate(doctor);
    }

    private Map<String, String> baseParams() {
        return new java.util.HashMap<>(Map.of(
                "fullName", "Bác sĩ A",
                "username", "doctor",
                "email", "doctor@test.com",
                "phoneNumber", "0909",
                "qualification", "Thạc sĩ",
                "biography", "Kinh nghiệm",
                "specialtyId", "5"
        ));
    }
}
