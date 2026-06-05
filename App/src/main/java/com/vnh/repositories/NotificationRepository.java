package com.vnh.repositories;

import com.vnh.pojo.Users;
import java.util.List;
import com.vnh.pojo.Notifications;

public interface NotificationRepository {
    List<Notifications> getNotificationsByUser(Users user); // Đồng bộ tên hàm
    void addNotification(Notifications notification);       // Đồng bộ tên hàm
}
