package com.medmap.track.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RetailerState implements PurchaseOrderState {

    private static final Logger logger = LoggerFactory.getLogger(RetailerState.class);

    @Override
    public void next(PurchaseOrderContext context) {
        context.setState(new ConsumerState());
    }

    @Override
    public void prev(PurchaseOrderContext context) {
        context.setState(new TransporterState());
    }

    @Override
    public void printStatus() {
        logger.info("Order is with the retailer.");
    }
}
