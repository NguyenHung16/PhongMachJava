package com.vnh.controllers;

import com.vnh.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @Autowired
    private UserServices userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
