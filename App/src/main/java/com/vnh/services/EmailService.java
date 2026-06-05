/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.vnh.services;

/**
 *
 * @author Nguyen Hung
 */
public interface EmailService {
    void sendOtp(String toEmail, String otp);
}
