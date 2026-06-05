package com.vnh.services.impl;

import com.vnh.pojo.Doctors;
import com.vnh.pojo.Specialties;
import com.vnh.pojo.Users;
import com.vnh.repositories.DoctorRepository;
import com.vnh.repositories.UserRepository;
import com.vnh.repositories.SpecialtyRepository;
import com.vnh.services.DoctorService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class DoctorServiceImpl implements DoctorService {

    @Autowired private DoctorRepository doctorRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private SpecialtyRepository specialtyRepo;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private Cloudinary cloudinary;

    @Override
    public List<Doctors> getDoctors() { return doctorRepo.getDoctors(); }

    @Override
    public List<Doctors> getDoctorsBySpecialtyId(int specialtyId) { return doctorRepo.getDoctorsBySpecialtyId(specialtyId); }

    @Override
    public Doctors getDoctorById(int id) { return doctorRepo.getDoctorById(id); }

    @Override
    public void saveOrUpdate(Map<String, String> params, MultipartFile avatar) {
        Users u;
        if (params.get("id") != null && !params.get("id").isEmpty()) {
            u = userRepo.getUserById(Integer.parseInt(params.get("id")));
        } else {
            u = new Users();
            u.setRole("DOCTOR");
            u.setPassword(passwordEncoder.encode("123456")); // Mật khẩu mặc định
        }

        u.setFullName(params.get("fullName"));
        u.setUsername(params.get("username"));
        u.setEmail(params.get("email"));
        u.setPhoneNumber(params.get("phoneNumber"));

        if (avatar != null && !avatar.isEmpty()) {
            try {
                Map res = this.cloudinary.uploader().upload(avatar.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                u.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) { System.err.println(ex.getMessage()); }
        }

        if (u.getId() == null) userRepo.addUser(u);
        else userRepo.updateUser(u);

        Doctors d = doctorRepo.getDoctorById(u.getId());
        if (d == null) {
            d = new Doctors();
            d.setId(u.getId());
            d.setDoctorCode("DOC" + u.getId());
        }
        
        d.setQualification(params.get("qualification"));
        d.setBiography(params.get("biography"));
        
        if (params.get("specialtyId") != null) {
            Specialties s = specialtyRepo.getSpecialtyById(Integer.parseInt(params.get("specialtyId")));
            d.setSpecialtyId(s);
        }

        doctorRepo.saveOrUpdate(d);
    }

    @Override
    public void deleteDoctor(int id) {
        doctorRepo.deleteDoctor(id);
    }
}
