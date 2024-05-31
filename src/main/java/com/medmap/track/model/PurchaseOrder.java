package com.medmap.track.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "purchase_order")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer buyerCrn;

    private Integer sellerCrn;

    private String medicineName;

    private Integer quantity;

    private String orgRole;
}
