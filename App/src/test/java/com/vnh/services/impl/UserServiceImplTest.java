package com.vnh.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.vnh.pojo.Patients;
import com.vnh.pojo.Users;
import com.vnh.repositories.DoctorRepository;
import com.vnh.repositories.PatientRepository;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepo;
    @Mock private PatientRepository patientRepo;
    @Mock private DoctorRepository doctorRepo;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private Cloudinary cloudinary;
    @InjectMocks private UserServiceImpl service;

    @Test
    void delegatesUserQueriesAndChecksEmail() {
        Users user = new Users();
        Map<String, String> params = Map.of("role", "PATIENT");
        when(userRepo.getUsers(params)).thenReturn(List.of(user));
        when(userRepo.getUserByUsername("a")).thenReturn(user);
        when(userRepo.getUserById(1)).thenReturn(user);
        when(userRepo.findByEmail("a@test.com")).thenReturn(user);

        assertEquals(List.of(user), service.getUsers(params));
        assertSame(user, service.getUserByUsername("a"));
        assertSame(user, service.getUserById(1));
        assertTrue(service.existsByEmail("a@test.com"));
        assertFalse(service.existsByEmail("none@test.com"));
    }

    @Test
    void loadUserByUsernameAddsRolePrefixAndThrowsWhenMissing() {
        Users user = new Users();
        user.setUsername("doctor");
        user.setPassword("hash");
        user.setRole("DOCTOR");
        when(userRepo.getUserByUsername("doctor")).thenReturn(user);

        var details = service.loadUserByUsername("doctor");

        assertEquals("doctor", details.getUsername());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR")));
        when(userRepo.getUserByUsername("missing")).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("missing"));
    }

    @Test
    void addUserCreatesPatientProfileForPatientRole() {
        Users saved = new Users();
        saved.setId(9);
        saved.setRole("PATIENT");
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(userRepo.addUser(any(Users.class))).thenReturn(saved);

        Users result = service.addUser(baseParams("PATIENT"), null);

        assertSame(saved, result);
        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
        verify(userRepo).addUser(userCaptor.capture());
        assertEquals("encoded", userCaptor.getValue().getPassword());
        ArgumentCaptor<Patients> patientCaptor = ArgumentCaptor.forClass(Patients.class);
        verify(patientRepo).addPatient(patientCaptor.capture());
        assertEquals(9, patientCaptor.getValue().getId());
        assertEquals("P9", patientCaptor.getValue().getPatientCode());
    }

    @Test
    void addUserUploadsAvatarAndSkipsPatientForDoctorRole() throws IOException {
        Users saved = new Users();
        saved.setId(11);
        saved.setRole("DOCTOR");
        MultipartFile avatar = mock(MultipartFile.class);
        Uploader uploader = mock(Uploader.class);
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(avatar.isEmpty()).thenReturn(false);
        when(avatar.getBytes()).thenReturn(new byte[]{1});
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(Map.of("secure_url", "https://img.test/u.png"));
        when(userRepo.addUser(any(Users.class))).thenReturn(saved);

        service.addUser(baseParams("DOCTOR"), avatar);

        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
        verify(userRepo).addUser(userCaptor.capture());
        assertEquals("https://img.test/u.png", userCaptor.getValue().getAvatar());
        verifyNoInteractions(patientRepo);
    }

    @Test
    void authenticateUsesPasswordEncoder() {
        Users user = new Users();
        user.setPassword("hash");
        when(userRepo.getUserByUsername("a")).thenReturn(user);
        when(passwordEncoder.matches("secret", "hash")).thenReturn(true);

        assertTrue(service.authenticate("a", "secret"));
        assertFalse(service.authenticate("missing", "secret"));
    }

    @Test
    void updateUserUpdatesAllowedFieldsAndAvatar() throws IOException {
        Users existing = new Users();
        MultipartFile avatar = mock(MultipartFile.class);
        Uploader uploader = mock(Uploader.class);
        when(userRepo.getUserById(1)).thenReturn(existing);
        when(avatar.isEmpty()).thenReturn(false);
        when(avatar.getBytes()).thenReturn(new byte[]{1});
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(Map.of("secure_url", "https://img.test/new.png"));
        when(userRepo.updateUser(existing)).thenReturn(existing);

        Users result = service.updateUser(1, Map.of("fullName", "Tên mới", "email", "new@test.com", "phoneNumber", "0999"), avatar);

        assertSame(existing, result);
        assertEquals("Tên mới", existing.getFullName());
        assertEquals("new@test.com", existing.getEmail());
        assertEquals("0999", existing.getPhoneNumber());
        assertEquals("https://img.test/new.png", existing.getAvatar());
    }

    @Test
    void updateUserReturnsNullWhenUserMissingAndResetPasswordSkipsMissingEmail() {
        when(userRepo.getUserById(404)).thenReturn(null);
        assertNull(service.updateUser(404, Map.of("fullName", "A"), null));

        Users user = new Users();
        when(userRepo.findByEmail("a@test.com")).thenReturn(user);
        when(passwordEncoder.encode("new")).thenReturn("encoded");
        service.resetPassword("a@test.com", "new");
        assertEquals("encoded", user.getPassword());
        verify(userRepo).updateUser(user);

        service.resetPassword("missing@test.com", "new");
        verify(userRepo, times(1)).updateUser(any(Users.class));
    }

    private Map<String, String> baseParams(String role) {
        return Map.of(
                "fullName", "Nguyễn Văn A",
                "email", "a@test.com",
                "phoneNumber", "0909",
                "username", "user",
                "gender", "MALE",
                "password", "secret",
                "role", role
        );
    }
}
