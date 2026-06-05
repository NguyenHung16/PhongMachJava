package com.vnh.repositories.impl;

import com.vnh.pojo.Specialties;
import com.vnh.repositories.SpecialtyRepository;
import java.util.List;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SpecialtyRepositoryImpl implements SpecialtyRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<Specialties> getSpecialties() {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Specialties", Specialties.class);
        return q.getResultList();
    }

    @Override
    public Specialties getSpecialtyById(int id) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        return s.get(Specialties.class, id);
    }

    @Override
    public void saveOrUpdate(Specialties specialty) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        if (specialty.getId() != null) {
            s.merge(specialty);
        } else {
            s.persist(specialty);
        }
    }

    @Override
    public void deleteSpecialty(int id) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        Specialties spec = this.getSpecialtyById(id);
        if (spec != null) {
            s.remove(spec);
        }
    }
}
