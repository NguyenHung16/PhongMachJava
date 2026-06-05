package com.vnh.repositories.impl;

import com.vnh.pojo.Users;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock private LocalSessionFactoryBean sessionFactory;
    @Mock private SessionFactory hibernateSessionFactory;
    @Mock private Session session;
    @Mock private Query<Users> query;
    @InjectMocks private UserRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        lenient().when(sessionFactory.getObject()).thenReturn(hibernateSessionFactory);
        lenient().when(hibernateSessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    void getUsersReturnsList() {
        List<Users> users = List.of(new Users());
        when(session.createQuery(anyString(), eq(Users.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(users);

        assertSame(users, repository.getUsers(Map.of()));
    }

    @Test
    void getUserByUsernameReturnsUserAndNullOnFailure() {
        Users user = new Users();
        when(session.createQuery(anyString(), eq(Users.class))).thenReturn(query);
        when(query.setParameter("un", "doctor1")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(user);

        assertSame(user, repository.getUserByUsername("doctor1"));

        doThrow(new RuntimeException("missing")).when(query).getSingleResult();
        assertNull(repository.getUserByUsername("doctor1"));
    }

    @Test
    void addUserPersistsAndReturnsSameInstance() {
        Users user = new Users();

        assertSame(user, repository.addUser(user));
        verify(session).persist(user);
    }

    @Test
    void authenticateAlwaysReturnsFalseInRepository() {
        assertFalse(repository.authenticate("a", "b"));
    }

    @Test
    void getUserByIdAndUpdateUserDelegateSession() {
        Users user = new Users();
        when(session.get(Users.class, 2)).thenReturn(user);

        assertSame(user, repository.getUserById(2));
        assertSame(user, repository.updateUser(user));
        verify(session).merge(user);
    }

    @Test
    void findByEmailReturnsUserAndNullOnFailure() {
        Users user = new Users();
        when(session.createQuery(anyString(), eq(Users.class))).thenReturn(query);
        when(query.setParameter("em", "mail@example.com")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(user);

        assertSame(user, repository.findByEmail("mail@example.com"));

        doThrow(new RuntimeException("missing")).when(query).getSingleResult();
        assertNull(repository.findByEmail("mail@example.com"));
    }
}
