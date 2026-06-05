package com.vnh.repositories;

import com.vnh.pojo.Users;
import java.util.List;
import java.util.Map;

public interface UserRepository {
    Users getUserByUsername(String username);
    Users addUser(Users u);
    boolean authenticate(String username, String password);
    Users getUserById(int id);
    Users updateUser(Users user);
    Users findByEmail(String email);
    void update(Users user);
    List<Users> getUsers(Map<String, String> params); // Hàm mới
}
