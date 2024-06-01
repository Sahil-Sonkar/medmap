package com.medmap.track.state;

public interface PurchaseOrderState {

    void next(PurchaseOrderContext context);

    void prev(PurchaseOrderContext context);

    void printStatus();
}
