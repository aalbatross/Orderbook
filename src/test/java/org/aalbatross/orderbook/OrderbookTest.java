package org.aalbatross.orderbook;

import org.aalbatross.orderbook.entities.Order;
import org.aalbatross.orderbook.entities.OrderType;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OrderbookTest {

    List<Order> buys = IntStream.range(0, 20).asDoubleStream().mapToObj(price -> Order.builder().orderType(OrderType.BUY).price(price).size(15.0).build()).collect(Collectors.toList());
    List<Order> sells = IntStream.range(0, 20).asDoubleStream().mapToObj(price -> Order.builder().orderType(OrderType.SELL).price(price).size(15.0).build()).collect(Collectors.toList());


    @Test
    public void displayTest() {
        Orderbook BOOK = new Orderbook("XYZ");
        Stream.concat(buys.stream(), sells.stream()).forEachOrdered(BOOK::update);

        Assert.assertEquals(20, BOOK.buySize());
        Assert.assertEquals(20, BOOK.sellSize());

        var expectedBuyResult = IntStream.iterate(19, x -> x >= 10, x -> x - 1).asDoubleStream().mapToObj(price -> Order.builder().orderType(OrderType.BUY).price(price).size(15.0).build()).collect(Collectors.toList());
        var expectedSellResult = IntStream.iterate(0, x -> x < 10, x -> x + 1).asDoubleStream().mapToObj(price -> Order.builder().orderType(OrderType.SELL).price(price).size(15.0).build()).collect(Collectors.toList());

        Assert.assertEquals(expectedBuyResult, BOOK.top10Buys());
        Assert.assertEquals(expectedSellResult, BOOK.top10Sells());

        Assert.assertEquals("XYZ", BOOK.getProductId());

        final var standardOut = System.out;
        final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        BOOK.display();
        Assert.assertFalse(outputStreamCaptor.toString(StandardCharsets.UTF_8).isEmpty());
        Assert.assertTrue(outputStreamCaptor.toString(StandardCharsets.UTF_8).startsWith("Orderbook: productId: XYZ Frequency: buy: 20 sell: 20"));
        System.setOut(standardOut);
    }

    @Test
    public void displayWhenEmptyTest() {
        Orderbook BOOK = new Orderbook("XYZ");
        BOOK.display();
    }
}
