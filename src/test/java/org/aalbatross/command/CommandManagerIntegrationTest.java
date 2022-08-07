package org.aalbatross.command;

import org.aalbatross.reactive.flows.OrderbookFlowManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CommandManagerIntegrationTest {

    @Before
    public void init() {
        OrderbookFlowManager.INSTANCE.closeAll();
    }

    @Test
    public void simpleFlowTest() throws InterruptedException {
        var mgr = CommandManager.INSTANCE;
        var fmgr = OrderbookFlowManager.INSTANCE;

        mgr.handle(List.of("create", "ETH-USD"));
        mgr.handle(List.of("create", "BTC-USD"));

        Thread.sleep(5000);

        mgr.handle(List.of("display", "ETH-USD"));
        mgr.handle(List.of("display", "BTC-USD"));

        Thread.sleep(2000);
        mgr.handle(List.of("list"));
        Assert.assertEquals(2, fmgr.list().size());

        mgr.handle(List.of("drop", "ETH-USD"));
        mgr.handle(List.of("drop", "BTC-USD"));

        Thread.sleep(2000);
        mgr.handle(List.of("list"));
        Assert.assertTrue(fmgr.list().isEmpty());
    }

    private void runOutputTest(List<String> command, String expectedOutput) {
        var mgr = CommandManager.INSTANCE;
        final var standardOut = System.out;
        final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        mgr.handle(command);
        Assert.assertFalse(outputStreamCaptor.toString(StandardCharsets.UTF_8).isEmpty());
        Assert.assertTrue(outputStreamCaptor.toString(StandardCharsets.UTF_8).contains(expectedOutput));
        System.setOut(standardOut);
    }

    @Test
    public void helpCreateCommandTest() {
        runOutputTest(List.of("create"), new CreateCommand().helpMessage());
        runOutputTest(List.of("display"), new DisplayCommand().helpMessage());
        runOutputTest(List.of("drop"), new DropCommand().helpMessage());
        runOutputTest(List.of("list", "product"), new ListCommand().helpMessage());
        runOutputTest(List.of("any"), CommandManager.INSTANCE.helpMessage());
        runOutputTest(List.of(), CommandManager.INSTANCE.helpMessage());
    }

}
