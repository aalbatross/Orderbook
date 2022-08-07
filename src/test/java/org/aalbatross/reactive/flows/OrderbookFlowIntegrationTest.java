package org.aalbatross.reactive.flows;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class OrderbookFlowIntegrationTest {

    @Test
    public void testSimpleOrderbookFlow() throws InterruptedException {
        var flow = new OrderbookFlow("ETH-USD", "test");
        flow.start();
        Assert.assertEquals("test", flow.name());
        Assert.assertEquals("ETH-USD", flow.getProductId());

        Thread.sleep(5000);
        Assert.assertTrue(flow.isRunning());
        flow.stop();

        final var standardOut = System.out;
        final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        flow.display();
        Assert.assertFalse(outputStreamCaptor.toString(StandardCharsets.UTF_8).isEmpty());
        Assert.assertTrue(outputStreamCaptor.toString(StandardCharsets.UTF_8).contains("Orderbook: productId: ETH-USD"));
        System.setOut(standardOut);
        Assert.assertFalse(flow.isRunning());
    }
}
