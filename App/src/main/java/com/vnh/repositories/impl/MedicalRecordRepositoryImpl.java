package com.vnh.repositories.impl;

import com.vnh.pojo.MedicalRecords;
import com.vnh.repositories.MedicalRecordRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import jakarta.persistence.Query;

@Repository
@Transactional
public class MedicalRecordRepositoryImpl implements MedicalRecordRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<MedicalRecords> getMedicalRecords() {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        // Lấy danh sách bệnh án, các thông tin khác sẽ được tải khi cần trong Transaction
        return s.createQuery("SELECT m FROM MedicalRecords m ORDER BY m.createdAt DESC", MedicalRecords.class).getResultList();
    }

    @Override
    public MedicalRecords getMedicalRecordById(int id) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        return s.get(MedicalRecords.class, id);
    }

    @Override
    public void save(MedicalRecords medicalRecord) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        if (medicalRecord.getId() != null) s.merge(medicalRecord);
        else s.persist(medicalRecord);
    }

    @Override
    public List<MedicalRecords> getMedicalRecordsByPatientId(int patientId) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        // Câu truy vấn đơn giản, Hibernate sẽ tự load các Collection nhờ @Transactional ở Service
        String hql = "SELECT m FROM MedicalRecords m WHERE m.patientId.id = :patientId ORDER BY m.createdAt DESC";
        return s.createQuery(hql, MedicalRecords.class)
                .setParameter("patientId", patientId)
                .getResultList();
    }
}
