package com.medmap.track.dto;

import lombok.Data;

@Data
public class PurchaseOrderDto {

    private Integer buyerCrn;

    private Integer sellerCrn;

    private String medicineName;

    private Integer quantity;
}
