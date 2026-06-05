package com.vnh.repositories.impl;

import com.vnh.pojo.Notifications;
import com.vnh.pojo.Users;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationRepositoryImplTest {

    @Mock private LocalSessionFactoryBean sessionFactory;
    @Mock private SessionFactory hibernateSessionFactory;
    @Mock private Session session;
    @Mock private Query<Notifications> query;
    @InjectMocks private NotificationRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        when(sessionFactory.getObject()).thenReturn(hibernateSessionFactory);
        when(hibernateSessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    void getNotificationsByUserReturnsList() {
        Users user = new Users();
        user.setId(3);
        List<Notifications> notifications = List.of(new Notifications());
        when(session.createQuery(anyString(), eq(Notifications.class))).thenReturn(query);
        when(query.setParameter("uid", 3)).thenReturn(query);
        when(query.getResultList()).thenReturn(notifications);

        assertSame(notifications, repository.getNotificationsByUser(user));
    }

    @Test
    void addNotificationPersistsEntity() {
        Notifications notification = new Notifications();

        repository.addNotification(notification);

        verify(session).persist(notification);
    }
}
