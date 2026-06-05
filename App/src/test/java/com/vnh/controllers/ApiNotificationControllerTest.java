package com.vnh.controllers;

import com.vnh.pojo.Notifications;
import com.vnh.pojo.Users;
import com.vnh.services.NotificationService;
import com.vnh.services.UserServices;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiNotificationControllerTest {

    @Mock private NotificationService notificationService;
    @Mock private UserServices userService;
    @InjectMocks private ApiNotificationController controller;

    @Test
    void getMyNotificationsRequiresPrincipal() {
        assertEquals(401, controller.getMyNotifications(null).getStatusCode().value());
    }

    @Test
    void getMyNotificationsReturnsUserNotifications() {
        Users user = new Users();
        user.setUsername("patient");
        Notifications notification = new Notifications();
        notification.setId(1);
        notification.setMessage("Confirmed");
        when(userService.getUserByUsername("patient")).thenReturn(user);
        when(notificationService.getNotificationsByUser(user)).thenReturn(List.of(notification));

        ResponseEntity<List<Notifications>> response = controller.getMyNotifications(() -> "patient");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Confirmed", response.getBody().get(0).getMessage());
    }
}
