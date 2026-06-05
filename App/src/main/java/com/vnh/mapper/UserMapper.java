package com.vnh.mapper;

import com.vnh.pojo.Users;
import com.vnh.dto.UserDto;

public class UserMapper {

   
    public static UserDto toUserDto(Users user) {
        if (user == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setId(user.getId());
       
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setFullName(user.getFullName());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
       
        dto.setAvatar(user.getAvatar());

        return dto;
    }
}