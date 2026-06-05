package com.vnh.services;

import com.vnh.pojo.Users;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

public interface UserServices extends UserDetailsService {
    boolean existsByEmail(String email);
    void resetPassword(String email, String newPassword);
    Users getUserByUsername(String username);
    Users addUser(Map<String, String> params, MultipartFile avatar);
    boolean authenticate(String username, String password);
    Users getUserById(int id);
    Users updateUser(int userId, Map<String, String> params, MultipartFile avatarUrl);
    List<Users> getUsers(Map<String, String> params); // Hàm mới
}
