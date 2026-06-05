/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vnh.repositories.impl;

import com.vnh.pojo.PrescriptionDetails;
import com.vnh.repositories.PrescriptionDetailsRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Nguyen Hung
 */
@Repository
@Transactional
public class PrescriptionDetailsRepositoryImpl implements PrescriptionDetailsRepository{
    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public void SavePrescriptionDetails(PrescriptionDetails prescriptionDetails) {
         Session session = this.sessionFactory.getObject().getCurrentSession();
         session.persist(prescriptionDetails);

    }
}
