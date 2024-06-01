package com.medmap.track.model;

import jakarta.persistence.*;

import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name="medicine")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "manufacture_date")
    private Date manufactureDate;

    @Column(name = "expiration_date")
    private Date expirationDate;

    @ManyToOne
    @JoinColumn(name = "manufacturer_crn", nullable = false)
    private Company manufacturer;

    @Column(name = "initial_quantity")
    private Integer initialQuantity;
}
