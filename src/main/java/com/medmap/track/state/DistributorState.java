package com.medmap.track.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributorState implements PurchaseOrderState {

    private static final Logger logger = LoggerFactory.getLogger(DistributorState.class);

    @Override
    public void next(PurchaseOrderContext context) {
        context.setState(new TransporterState());
    }

    @Override
    public void prev(PurchaseOrderContext context) {
        context.setState(new ManufacturerState());
    }

    @Override
    public void printStatus() {
        logger.info("Order is with the distributor.");
    }
}
