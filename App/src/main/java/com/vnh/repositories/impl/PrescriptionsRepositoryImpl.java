package com.vnh.repositories.impl;

import com.vnh.pojo.Prescriptions;
import com.vnh.repositories.PrescriptionsRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import jakarta.persistence.Query;

@Repository
@Transactional
public class PrescriptionsRepositoryImpl implements PrescriptionsRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<Prescriptions> getPrescriptions() {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        // Fetch đầy đủ thông tin để DTO Mapper hoạt động
        String hql = "SELECT p FROM Prescriptions p " +
                     "JOIN FETCH p.doctorId d " +
                     "JOIN FETCH d.userId " +
                     "JOIN FETCH p.medicalRecordId m " +
                     "JOIN FETCH m.patientId pat " +
                     "JOIN FETCH pat.userId";
        return s.createQuery(hql, Prescriptions.class).getResultList();
    }

    @Override
    public Prescriptions getPrescriptionById(int id) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        try {
            String hql = "SELECT p FROM Prescriptions p " +
                         "JOIN FETCH p.doctorId d " +
                         "JOIN FETCH d.userId " +
                         "JOIN FETCH p.medicalRecordId " +
                         "WHERE p.id = :id";
            return s.createQuery(hql, Prescriptions.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void savePrescription(Prescriptions prescription) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        if (prescription.getId() != null) s.merge(prescription);
        else s.persist(prescription);
    }
}
