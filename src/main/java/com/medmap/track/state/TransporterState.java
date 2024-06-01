package com.medmap.track.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransporterState implements PurchaseOrderState {

    private static final Logger logger = LoggerFactory.getLogger(TransporterState.class);

    @Override
    public void next(PurchaseOrderContext context) {
        context.setState(new RetailerState());
    }

    @Override
    public void prev(PurchaseOrderContext context) {
        context.setState(new DistributorState());
    }

    @Override
    public void printStatus() {
        logger.info("Order is with the transporter.");
    }
}
