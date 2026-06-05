package com.vnh.dto;

import java.util.Date;

public class UserDto {

    private Integer id;
    private String username;
    private String email;
    private String role;
    private String fullName;
    private String avatar;
    private Date dateOfBirth;
    private String phoneNumber;
    private String address;
    private PatientDto patient;

    public UserDto() {
        // Constructor rỗng
    }

    // Constructors
    public UserDto(Integer id, String username, String email, String role, String fullName, String avatar, Date dateOfBirth, String phoneNumber, String address) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
        this.avatar = avatar;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }
     public PatientDto getPatient() {
        return patient;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    public void setPatient(PatientDto patient) {
        this.patient = patient;
    }

}
