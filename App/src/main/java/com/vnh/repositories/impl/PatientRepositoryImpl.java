package com.vnh.repositories.impl;

import com.vnh.pojo.Patients;
import com.vnh.repositories.PatientRepository;
import java.util.List;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PatientRepositoryImpl implements PatientRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<Patients> getPatients() {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        // JOIN FETCH User để lấy Họ tên, Avatar...
        Query q = s.createQuery("FROM Patients p JOIN FETCH p.userId", Patients.class);
        return q.getResultList();
    }

    @Override
    public Patients getPatientById(int id) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        try {
            return s.createQuery("FROM Patients p JOIN FETCH p.userId WHERE p.id = :id", Patients.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void addPatient(Patients p) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        s.persist(p);
    }

    @Override
    public List<Patients> findByDoctorId(int doctorId) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        // Lấy danh sách bệnh nhân đã từng khám với bác sĩ này
        String hql = "SELECT DISTINCT a.patientId FROM Appointments a " +
                     "JOIN FETCH a.patientId.userId " +
                     "WHERE a.doctorId.id = :docId";
        return s.createQuery(hql, Patients.class)
                .setParameter("docId", doctorId)
                .getResultList();
    }

    @Override
    public Patients getPatientByUserId(int userId) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        try {
            return s.createQuery("FROM Patients p JOIN FETCH p.userId WHERE p.userId.id = :userId", Patients.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
