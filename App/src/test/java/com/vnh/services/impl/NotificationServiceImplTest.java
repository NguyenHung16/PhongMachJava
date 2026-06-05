package com.vnh.services.impl;

import com.vnh.pojo.Notifications;
import com.vnh.pojo.Users;
import com.vnh.repositories.NotificationRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock private NotificationRepository notificationRepo;
    @InjectMocks private NotificationServiceImpl service;

    @Test
    void createNotificationInitializesUnreadNotification() {
        Users user = new Users();

        service.createNotification("Có lịch khám mới", user);

        ArgumentCaptor<Notifications> captor = ArgumentCaptor.forClass(Notifications.class);
        verify(notificationRepo).addNotification(captor.capture());
        Notifications saved = captor.getValue();
        assertEquals("Có lịch khám mới", saved.getMessage());
        assertSame(user, saved.getUser());
        assertFalse(saved.getIsRead());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void getNotificationsByUserDelegates() {
        Users user = new Users();
        Notifications notification = new Notifications();
        when(notificationRepo.getNotificationsByUser(user)).thenReturn(List.of(notification));

        assertEquals(List.of(notification), service.getNotificationsByUser(user));
    }
}
