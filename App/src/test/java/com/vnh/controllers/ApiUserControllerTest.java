package com.vnh.controllers;

import com.vnh.dto.ApiResponse;
import com.vnh.pojo.Users;
import com.vnh.services.UserServices;
import java.security.Principal;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiUserControllerTest {

    @Mock private UserServices userServices;
    @InjectMocks private ApiUserController controller;

    @Test
    void createReturnsCreatedUser() {
        Users user = user(1, "patient", "PATIENT");
        Map<String, String> params = Map.of("username", "patient");
        when(userServices.addUser(params, null)).thenReturn(user);

        ResponseEntity<ApiResponse<Users>> response = controller.create(params, null);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("patient", response.getBody().getData().getUsername());
    }

    @Test
    void createReturnsServerErrorWhenServiceThrows() {
        Map<String, String> params = Map.of("username", "broken");
        when(userServices.addUser(params, null)).thenThrow(new RuntimeException("broken"));

        ResponseEntity<ApiResponse<Users>> response = controller.create(params, null);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("broken", response.getBody().getMessage());
    }

    @Test
    void updateProfileHandlesSuccessMissingUserAndException() {
        Map<String, String> params = Map.of("fullName", "Updated");
        when(userServices.updateUser(1, params, null)).thenReturn(user(1, "updated", "PATIENT"));
        assertEquals(200, controller.updateProfile(1, params, null).getStatusCode().value());

        when(userServices.updateUser(2, params, null)).thenReturn(null);
        assertEquals(400, controller.updateProfile(2, params, null).getStatusCode().value());

        when(userServices.updateUser(3, params, null)).thenThrow(new RuntimeException("db"));
        assertEquals(500, controller.updateProfile(3, params, null).getStatusCode().value());
    }

    @Test
    void loginHandlesSuccessUnauthorizedAndException() {
        Users credentials = new Users();
        credentials.setUsername("doctor");
        credentials.setPassword("secret");
        Users doctor = user(5, "doctor", "DOCTOR");

        when(userServices.authenticate("doctor", "secret")).thenReturn(true);
        when(userServices.getUserByUsername("doctor")).thenReturn(doctor);

        ResponseEntity<ApiResponse<Map<String, Object>>> response = controller.login(credentials);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody().getData().get("token"));

        Users bad = new Users();
        bad.setUsername("bad");
        bad.setPassword("wrong");
        when(userServices.authenticate("bad", "wrong")).thenReturn(false);
        assertEquals(401, controller.login(bad).getStatusCode().value());

        Users broken = new Users();
        broken.setUsername("broken");
        broken.setPassword("x");
        when(userServices.authenticate("broken", "x")).thenThrow(new RuntimeException("auth"));
        assertEquals(500, controller.login(broken).getStatusCode().value());
    }

    @Test
    void getProfileRequiresPrincipalAndReturnsUserMap() {
        assertEquals(401, controller.getProfile(null).getStatusCode().value());

        Principal principal = () -> "patient";
        Users patient = user(7, "patient", "PATIENT");
        patient.setAvatar("/avatar.png");
        when(userServices.getUserByUsername("patient")).thenReturn(patient);

        ResponseEntity<ApiResponse<Map<String, String>>> response = controller.getProfile(principal);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("patient", response.getBody().getData().get("username"));
        assertEquals("PATIENT", response.getBody().getData().get("role"));
    }

    private static Users user(int id, String username, String role) {
        Users user = new Users();
        user.setId(id);
        user.setUsername(username);
        user.setFullName(username);
        user.setRole(role);
        return user;
    }
}
