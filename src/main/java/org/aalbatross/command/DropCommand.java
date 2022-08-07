package org.aalbatross.command;

import org.aalbatross.reactive.flows.OrderbookFlowManager;

import java.util.List;

public class DropCommand implements Validatable, Command, Helpable {

    private String productId;

    @Override
    public void handle(List<String> command) {
        if (test(command))
            OrderbookFlowManager.INSTANCE.stopOrderbook(productId);
    }

    @Override
    public String helpMessage() {
        return "drop syntax: drop <product_id> ,example: drop ETH-USD ";
    }

    @Override
    public boolean test(List<String> command) {
        if (command.size() == 2 && command.get(0).trim().equals("drop")) {
            productId = command.get(1).trim();
        } else {
            System.out.println(helpMessage());
            return false;
        }
        return true;
    }
}
