package com.vnh.repositories.impl;

import com.vnh.pojo.Medicines;
import com.vnh.repositories.MedicineRepository;
import java.util.List;
import java.util.Map;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class MedicineRepositoryImpl implements MedicineRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<Medicines> getMedicines(Map<String, String> params) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Medicines> q = b.createQuery(Medicines.class);
        Root<Medicines> root = q.from(Medicines.class);
        q.select(root);

        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                Predicate p = b.like(root.get("name"), String.format("%%%s%%", kw));
                q.where(p);
            }
        }
        
        q.orderBy(b.asc(root.get("id")));
        return s.createQuery(q).getResultList();
    }

    @Override
    public Medicines getMedicineById(int id) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        return s.get(Medicines.class, id);
    }

    @Override
    public Medicines addOrUpdateMedicine(Medicines medicine) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        if (medicine.getId() != null) {
            s.merge(medicine);
        } else {
            s.persist(medicine);
        }
        return medicine;
    }

    @Override
    public void deleteMedicine(int id) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        Medicines m = this.getMedicineById(id);
        if (m != null) s.remove(m);
    }

    @Override
    public List<Medicines> getExpiringMedicines(int daysThreshold) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        // Lấy thuốc sắp hết hạn (Logic này cần được hiệu chỉnh dựa trên DB thực tế)
        return s.createQuery("FROM Medicines m WHERE m.expiryDate IS NOT NULL ORDER BY m.expiryDate ASC", Medicines.class)
                .setMaxResults(10)
                .getResultList();
    }

    @Override
    public List<Medicines> getLowStockMedicines(int stockThreshold) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        return s.createQuery("FROM Medicines m WHERE m.stockQuantity < :threshold", Medicines.class)
                .setParameter("threshold", stockThreshold)
                .getResultList();
    }
}
