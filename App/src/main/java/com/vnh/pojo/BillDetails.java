/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vnh.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Nguyen Hung
 */
@Entity
@Table(name = "bill_details")
@NamedQueries({
    @NamedQuery(name = "BillDetails.findAll", query = "SELECT b FROM BillDetails b"),
    @NamedQuery(name = "BillDetails.findByBillId", query = "SELECT b FROM BillDetails b WHERE b.billDetailsPK.billId = :billId"),
    @NamedQuery(name = "BillDetails.findByServiceId", query = "SELECT b FROM BillDetails b WHERE b.billDetailsPK.serviceId = :serviceId"),
    @NamedQuery(name = "BillDetails.findByQuantity", query = "SELECT b FROM BillDetails b WHERE b.quantity = :quantity"),
    @NamedQuery(name = "BillDetails.findByAmount", query = "SELECT b FROM BillDetails b WHERE b.amount = :amount")})
public class BillDetails implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected BillDetailsPK billDetailsPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "quantity")
    private int quantity;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "amount")
    private BigDecimal amount;
    @JoinColumn(name = "bill_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    @JsonIgnore
    private Bills bills;
    @JoinColumn(name = "service_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    @JsonIgnore
    private Services services;

    public BillDetails() {
    }

    public BillDetails(BillDetailsPK billDetailsPK) {
        this.billDetailsPK = billDetailsPK;
    }

    public BillDetails(BillDetailsPK billDetailsPK, int quantity, BigDecimal amount) {
        this.billDetailsPK = billDetailsPK;
        this.quantity = quantity;
        this.amount = amount;
    }

    public BillDetails(int billId, int serviceId) {
        this.billDetailsPK = new BillDetailsPK(billId, serviceId);
    }

    public BillDetailsPK getBillDetailsPK() {
        return billDetailsPK;
    }

    public void setBillDetailsPK(BillDetailsPK billDetailsPK) {
        this.billDetailsPK = billDetailsPK;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Bills getBills() {
        return bills;
    }

    public void setBills(Bills bills) {
        this.bills = bills;
    }

    public Services getServices() {
        return services;
    }

    public void setServices(Services services) {
        this.services = services;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (billDetailsPK != null ? billDetailsPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BillDetails)) {
            return false;
        }
        BillDetails other = (BillDetails) object;
        if ((this.billDetailsPK == null && other.billDetailsPK != null) || (this.billDetailsPK != null && !this.billDetailsPK.equals(other.billDetailsPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.vnh.pojo.BillDetails[ billDetailsPK=" + billDetailsPK + " ]";
    }
    
}
