/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vnh.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Nguyen Hung
 */
@Entity
@Table(name = "medicines")
@NamedQueries({
    @NamedQuery(name = "Medicines.findAll", query = "SELECT m FROM Medicines m"),
    @NamedQuery(name = "Medicines.findById", query = "SELECT m FROM Medicines m WHERE m.id = :id"),
    @NamedQuery(name = "Medicines.findByName", query = "SELECT m FROM Medicines m WHERE m.name = :name"),
    @NamedQuery(name = "Medicines.findByDosageForm", query = "SELECT m FROM Medicines m WHERE m.dosageForm = :dosageForm"),
    @NamedQuery(name = "Medicines.findByManufacturer", query = "SELECT m FROM Medicines m WHERE m.manufacturer = :manufacturer"),
    @NamedQuery(name = "Medicines.findByStockQuantity", query = "SELECT m FROM Medicines m WHERE m.stockQuantity = :stockQuantity"),
    @NamedQuery(name = "Medicines.findByUnit", query = "SELECT m FROM Medicines m WHERE m.unit = :unit"),
    @NamedQuery(name = "Medicines.findByExpiryDate", query = "SELECT m FROM Medicines m WHERE m.expiryDate = :expiryDate"),
    @NamedQuery(name = "Medicines.findByPrice", query = "SELECT m FROM Medicines m WHERE m.price = :price"),
    @NamedQuery(name = "Medicines.findByImageUrl", query = "SELECT m FROM Medicines m WHERE m.imageUrl = :imageUrl")})
public class Medicines implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "name")
    private String name;
    @Size(max = 50)
    @Column(name = "dosage_form")
    private String dosageForm;
    @Size(max = 100)
    @Column(name = "manufacturer")
    private String manufacturer;
    @Basic(optional = false)
    @NotNull
    @Column(name = "stock_quantity")
    private int stockQuantity;
    @Size(max = 20)
    @Column(name = "unit")
    private String unit;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "expiry_date")
    @Temporal(TemporalType.DATE)
    private Date expiryDate;
    
    // Đổi kiểu dữ liệu từ BigDecimal thành Double hoặc Float
    @Basic(optional = false)
    @NotNull
    @Column(name = "price")
    private Double price;
    
    @Size(max = 255)
    @Column(name = "image_url")
    private String imageUrl;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "medicines")
    @JsonIgnore
    private Collection<PrescriptionDetails> prescriptionDetailsCollection;

    public Medicines() {
    }

    public Medicines(Integer id) {
        this.id = id;
    }

    public Medicines(Integer id, String name, int stockQuantity, Double price) {
        this.id = id;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.price = price;
    }
    
    // Getters and Setters...

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Collection<PrescriptionDetails> getPrescriptionDetailsCollection() {
        return prescriptionDetailsCollection;
    }

    public void setPrescriptionDetailsCollection(Collection<PrescriptionDetails> prescriptionDetailsCollection) {
        this.prescriptionDetailsCollection = prescriptionDetailsCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Medicines)) {
            return false;
        }
        Medicines other = (Medicines) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.vnh.pojo.Medicines[ id=" + id + " ]";
    }
}