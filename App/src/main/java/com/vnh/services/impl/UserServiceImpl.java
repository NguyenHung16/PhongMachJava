package com.vnh.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.vnh.pojo.Patients;
import com.vnh.pojo.Doctors;
import com.vnh.pojo.Users;
import com.vnh.repositories.PatientRepository;
import com.vnh.repositories.DoctorRepository;
import com.vnh.repositories.UserRepository;
import com.vnh.services.UserServices;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class UserServiceImpl implements UserServices {

    @Autowired private UserRepository userRepo;
    @Autowired private PatientRepository patientRepo;
    @Autowired private DoctorRepository doctorRepo;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private Cloudinary cloudinary;

    @Override
    public List<Users> getUsers(Map<String, String> params) {
        return userRepo.getUsers(params);
    }

    @Override
    public Users getUserByUsername(String username) {
        return this.userRepo.getUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users u = this.getUserByUsername(username);
        if (u == null) throw new UsernameNotFoundException("User not found!");

        Set<GrantedAuthority> authorities = new HashSet<>();
        String role = u.getRole();
        if (role != null) {
            if (!role.startsWith("ROLE_")) role = "ROLE_" + role;
            authorities.add(new SimpleGrantedAuthority(role));
        }

        return new org.springframework.security.core.userdetails.User(
                u.getUsername(), u.getPassword(), authorities);
    }

    @Override
    public Users addUser(Map<String, String> params, MultipartFile avatar) {
        Users u = new Users();
        u.setFullName(params.get("fullName"));
        u.setEmail(params.get("email"));
        u.setPhoneNumber(params.get("phoneNumber"));
        u.setUsername(params.get("username"));
        u.setGender(params.get("gender"));
        u.setPassword(this.passwordEncoder.encode(params.get("password")));
        u.setRole(params.getOrDefault("role", "PATIENT"));

        if (avatar != null && !avatar.isEmpty()) {
            try {
                Map res = this.cloudinary.uploader().upload(avatar.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                u.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                System.err.println("Cloudinary error: " + ex.getMessage());
            }
        }

        Users savedUser = this.userRepo.addUser(u);
        if (savedUser != null) {
            if ("PATIENT".equals(savedUser.getRole())) {
                Patients p = new Patients();
                p.setId(savedUser.getId());
                p.setUserId(savedUser);
                p.setPatientCode("P" + savedUser.getId());
                this.patientRepo.addPatient(p);
            }
        }
        return savedUser;
    }

    @Override
    public boolean authenticate(String username, String password) {
        Users u = this.getUserByUsername(username);
        return u != null && passwordEncoder.matches(password, u.getPassword());
    }

    @Override
    public Users updateUser(int userId, Map<String, String> params, MultipartFile avatar) {
        Users existingUser = this.userRepo.getUserById(userId);
        if (existingUser == null) return null;

        if (params.containsKey("fullName")) existingUser.setFullName(params.get("fullName"));
        if (params.containsKey("email")) existingUser.setEmail(params.get("email"));
        if (params.containsKey("phoneNumber")) existingUser.setPhoneNumber(params.get("phoneNumber"));

        if (avatar != null && !avatar.isEmpty()) {
            try {
                Map res = this.cloudinary.uploader().upload(avatar.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                existingUser.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                System.err.println("Cloudinary error: " + ex.getMessage());
            }
        }

        return this.userRepo.updateUser(existingUser);
    }

    @Override
    public Users getUserById(int id) { return this.userRepo.getUserById(id); }

    @Override
    public boolean existsByEmail(String email) { return userRepo.findByEmail(email) != null; }

    @Override
    public void resetPassword(String email, String newPassword) {
        Users user = userRepo.findByEmail(email);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepo.updateUser(user);
        }
    }
}
