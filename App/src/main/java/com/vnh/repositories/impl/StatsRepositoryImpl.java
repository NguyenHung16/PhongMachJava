package com.vnh.repositories.impl;

import com.vnh.repositories.StatsRepository;
import java.util.List;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class StatsRepositoryImpl implements StatsRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<Object[]> revenueByTime(int year, String time) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        String group = time.equals("MONTH") ? "MONTH(b.billDate)" : "QUARTER(b.billDate)";
        String hql = String.format("SELECT %s, SUM(b.totalAmount) FROM Bills b WHERE b.paymentStatus = 'PAID' AND YEAR(b.billDate) = :year GROUP BY %s", group, group);
        return s.createQuery(hql, Object[].class).setParameter("year", year).getResultList();
    }

    @Override
    public List<Object[]> countPatientsByGender() {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        return s.createQuery("SELECT u.gender, COUNT(p.id) FROM Patients p JOIN p.userId u GROUP BY u.gender", Object[].class).getResultList();
    }

    @Override
    public List<Object[]> countPatientsByAgeGroup() {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        // Giả sử có một cột birthday hoặc logic tính age, tạm thời đếm tổng
        return s.createQuery("SELECT 'Bệnh nhân', COUNT(p.id) FROM Patients p", Object[].class).getResultList();
    }

    @Override
    public List<Object[]> countPatientsBySpecialty() {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        String hql = "SELECT sp.name, COUNT(a.id) FROM Appointments a JOIN a.doctorId d JOIN d.specialtyId sp GROUP BY sp.name";
        return s.createQuery(hql, Object[].class).getResultList();
    }

    @Override
    public List<Object[]> topUsedServices() {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        return s.createQuery("SELECT m.name, COUNT(pd.medicines.id) FROM PrescriptionDetails pd JOIN pd.medicines m GROUP BY m.name", Object[].class).getResultList();
    }

    @Override
    public List<Object[]> popularDiseases() {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        return s.createQuery("SELECT m.diagnosis, COUNT(m.id) FROM MedicalRecords m GROUP BY m.diagnosis", Object[].class).getResultList();
    }
}
