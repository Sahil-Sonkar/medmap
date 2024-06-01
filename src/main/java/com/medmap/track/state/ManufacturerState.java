package com.medmap.track.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ManufacturerState implements PurchaseOrderState {

    private static final Logger logger = LoggerFactory.getLogger(ManufacturerState.class);

    @Override
    public void next(PurchaseOrderContext context) {
        context.setState(new DistributorState());
    }

    @Override
    public void prev(PurchaseOrderContext context) {
        logger.info("This is the initial state.");
    }

    @Override
    public void printStatus() {
        logger.info("Order is with the manufacturer.");
    }
}
