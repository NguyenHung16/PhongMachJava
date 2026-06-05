package com.vnh.services.impl;

import com.vnh.pojo.Notifications;
import com.vnh.pojo.Users;
import com.vnh.repositories.NotificationRepository;
import com.vnh.services.NotificationService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepo;

    @Override
    public void createNotification(String message, Users user) {
        Notifications n = new Notifications();
        n.setMessage(message);
        // Sửa từ setUserId sang setUser cho đúng POJO
        n.setUser(user);
        n.setCreatedAt(new Date());
        n.setIsRead(false);
        notificationRepo.addNotification(n);
    }

    @Override
    public List<Notifications> getNotificationsByUser(Users user) {
        return notificationRepo.getNotificationsByUser(user);
    }
}
