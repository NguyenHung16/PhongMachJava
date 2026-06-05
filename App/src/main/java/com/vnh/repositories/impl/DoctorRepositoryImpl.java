package com.vnh.repositories.impl;

import com.vnh.pojo.Doctors;
import com.vnh.repositories.DoctorRepository;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public class DoctorRepositoryImpl implements DoctorRepository {
    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<Doctors> getDoctors() {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        // Lấy luôn thông tin User để DTO Mapper hoạt động
        Query q = session.createQuery("SELECT d FROM Doctors d JOIN FETCH d.userId", Doctors.class);
        return q.getResultList();
    }

    @Override
    public List<Doctors> getDoctorsBySpecialtyId(int specialtyId) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        Query q = session.createQuery("SELECT d FROM Doctors d JOIN FETCH d.userId WHERE d.specialtyId.id = :specialtyId", Doctors.class);
        q.setParameter("specialtyId", specialtyId);
        return q.getResultList();
    }

    @Override
    public Doctors getDoctorById(int id) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        try {
            return session.createQuery("SELECT d FROM Doctors d JOIN FETCH d.userId WHERE d.id = :id", Doctors.class)
                          .setParameter("id", id)
                          .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void saveOrUpdate(Doctors d) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        session.merge(d);
    }

    @Override
    public void deleteDoctor(int id) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        Doctors d = this.getDoctorById(id);
        if (d != null) {
            session.remove(d);
        }
    }
}
