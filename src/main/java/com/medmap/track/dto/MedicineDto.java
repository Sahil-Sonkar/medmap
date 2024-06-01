package com.medmap.track.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MedicineDto {

    private String name;

    private Date manufactureDate;

    private Date expirationDate;

    private Integer manufacturerCrn;

    private Integer initialQuantity;
}
