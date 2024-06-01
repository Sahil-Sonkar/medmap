package com.medmap.track.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConsumerState implements PurchaseOrderState {

    private static final Logger logger = LoggerFactory.getLogger(DistributorState.class);

    @Override
    public void next(PurchaseOrderContext context) {
        logger.info("Order is with the consumer. No further state.");
    }

    @Override
    public void prev(PurchaseOrderContext context) {
        context.setState(new RetailerState());
    }

    @Override
    public void printStatus() {
        logger.info("Order is with the consumer.");
    }
}
