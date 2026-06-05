/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vnh.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author Nguyen Hung
 */
@Embeddable
public class BillDetailsPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "bill_id")
    private int billId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "service_id")
    private int serviceId;

    public BillDetailsPK() {
    }

    public BillDetailsPK(int billId, int serviceId) {
        this.billId = billId;
        this.serviceId = serviceId;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) billId;
        hash += (int) serviceId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BillDetailsPK)) {
            return false;
        }
        BillDetailsPK other = (BillDetailsPK) object;
        if (this.billId != other.billId) {
            return false;
        }
        if (this.serviceId != other.serviceId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.vnh.pojo.BillDetailsPK[ billId=" + billId + ", serviceId=" + serviceId + " ]";
    }
    
}
