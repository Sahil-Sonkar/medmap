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

    @ManyToOne
    @JoinColumn(name = "buyerCrn", nullable = false)
    private Company buyer;

    @ManyToOne
    @JoinColumn(name = "sellerCrn", nullable = false)
    private Company seller;

    @ManyToOne
    @JoinColumn(name = "medicineId", nullable = false)
    private Medicine medicine;

    private Integer quantity;
}
