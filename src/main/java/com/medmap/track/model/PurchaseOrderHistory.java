package com.medmap.track.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "purchase_order")
public class PurchaseOrderHistory {

    private Integer prevOrg;

    private Integer currOrg;

    private Integer purchaseOrderId;

}
