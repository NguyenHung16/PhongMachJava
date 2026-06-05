/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vnh.controllers;

import com.vnh.pojo.Notifications;
import com.vnh.pojo.Users;
import com.vnh.services.NotificationService;
import com.vnh.services.UserServices;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Nguyen Hung
 */

@RestController
@RequestMapping("/api")
public class ApiNotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserServices userService; 

    @GetMapping("/notifications/my-notifications")
    public ResponseEntity<List<Notifications>> getMyNotifications(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

     
        String username = principal.getName();
        Users currentUser = userService.getUserByUsername(username);

        List<Notifications> notifications = notificationService.getNotificationsByUser(currentUser);

        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }
}
