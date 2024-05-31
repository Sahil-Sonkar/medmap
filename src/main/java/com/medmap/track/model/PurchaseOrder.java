package com.medmap.track.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "purchase_order")
public class PurchaseOrder {

    private Integer id;

    private String buyerCrn;

    private String sellerCrn;

    private String medicineName;

    private Integer quantity;

    private String orgRole;
}
