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

    private Date manufactureDate;

    private Date expirationDate;

    @ManyToOne
    @JoinColumn(name = "company_crn", nullable = false)
    private Company manufacturer;

    private Integer initialQuantity;
}
