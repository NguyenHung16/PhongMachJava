package com.vnh.repositories.impl;

import com.vnh.pojo.Users;
import com.vnh.repositories.UserRepository;
import java.util.List;
import java.util.Map;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<Users> getUsers(Map<String, String> params) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Users", Users.class);
        return q.getResultList();
    }

    @Override
    public Users getUserByUsername(String username) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Users WHERE username = :un", Users.class);
        q.setParameter("un", username);
        try {
            return (Users) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Users addUser(Users u) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        s.persist(u);
        return u;
    }

    @Override
    public boolean authenticate(String username, String password) {
        // Logic authentication is usually handled by Spring Security and Service
        return false; 
    }

    @Override
    public Users getUserById(int id) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        return s.get(Users.class, id);
    }

    @Override
    public Users updateUser(Users user) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        s.merge(user);
        return user;
    }

    @Override
    public Users findByEmail(String email) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Users WHERE email = :em", Users.class);
        q.setParameter("em", email);
        try {
            return (Users) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void update(Users user) {
        this.updateUser(user);
    }
}
