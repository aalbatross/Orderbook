package org.aalbatross.reactive.flows;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

public class OrderbookFlowManagerIntegrationTest {

    @Before
    public void init() {
        OrderbookFlowManager.INSTANCE.closeAll();
    }

    @Test
    public void simpleFlowManagerTest() throws InterruptedException {
        var mgr = OrderbookFlowManager.INSTANCE;
        mgr.createNewOrderbook("ETH-USD");
        mgr.createNewOrderbook("BTC-USD");

        Thread.sleep(5000);
        Assert.assertEquals(Set.of("ETH-USD", "BTC-USD"), mgr.list());

        mgr.displayOrderBook("ETH-USD");
        mgr.displayOrderBook("BTC-USD");

        Thread.sleep(2000);

        mgr.stopOrderbook("ETH-USD");

        mgr.closeAll();

        Assert.assertTrue(mgr.list().isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void sameProductFlowManagerTest() throws InterruptedException {
        var mgr = OrderbookFlowManager.INSTANCE;
        mgr.createNewOrderbook("ETH-USD");


        Thread.sleep(5000);
        mgr.createNewOrderbook("ETH-USD");
    }

    @Test(expected = RuntimeException.class)
    public void stopNonExistingProductFlowManagerTest() {
        var mgr = OrderbookFlowManager.INSTANCE;
        mgr.stopOrderbook("ETH-USD");
    }

    @Test(expected = RuntimeException.class)
    public void displayNonExistingProductFlowManagerTest() {
        var mgr = OrderbookFlowManager.INSTANCE;
        mgr.displayOrderBook("ETH-USD");
    }
}
