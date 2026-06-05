/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.vnh.services;

import com.vnh.pojo.Notifications;
import com.vnh.pojo.Users;
import java.util.List;

/**
 *
 * @author Nguyen Hung
 */
public interface NotificationService {

    void createNotification(String message, Users user);

    List<Notifications> getNotificationsByUser(Users user);
}
