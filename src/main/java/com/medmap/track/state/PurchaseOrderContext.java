package com.medmap.track.state;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class PurchaseOrderContext {
    private PurchaseOrderState state;

    public PurchaseOrderContext() {
        state = new ManufacturerState();
    }

    public void nextState() {
        state.next(this);
    }

    public void previousState() {
        state.prev(this);
    }

    public void printStatus() {
        state.printStatus();
    }

}
