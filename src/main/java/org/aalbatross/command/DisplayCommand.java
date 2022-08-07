package org.aalbatross.command;

import org.aalbatross.reactive.flows.OrderbookFlowManager;

import java.util.List;

public class DisplayCommand implements Helpable, Validatable, Command {
    private String productId;

    @Override
    public boolean test(List<String> command) {
        if (command.size() == 2 && command.get(0).trim().equals("display")) {
            productId = command.get(1).trim();
        } else {
            System.out.println(helpMessage());
            return false;
        }
        return true;
    }

    @Override
    public void handle(List<String> command) {
        if (test(command))
            OrderbookFlowManager.INSTANCE.displayOrderBook(productId);
    }

    @Override
    public String helpMessage() {
        return "display command syntax: display <product_id> \n example: display ETH-USD";
    }
}
