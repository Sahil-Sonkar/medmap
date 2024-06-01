package com.medmap.track.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "companyCrn", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "medicineId", nullable = false)
    private Medicine medicine;

    private Integer quantity;
}
