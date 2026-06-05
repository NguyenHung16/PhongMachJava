package com.vnh.repositories.impl;

import com.vnh.pojo.Notifications;
import com.vnh.pojo.Users;
import com.vnh.repositories.NotificationRepository;
import java.util.List;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class NotificationRepositoryImpl implements NotificationRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<Notifications> getNotificationsByUser(Users user) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Notifications n WHERE n.userId.id = :uid ORDER BY n.createdAt DESC", Notifications.class);
        q.setParameter("uid", user.getId());
        return q.getResultList();
    }

    @Override
    public void addNotification(Notifications notification) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        s.persist(notification);
    }
}
