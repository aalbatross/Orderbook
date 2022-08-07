package org.aalbatross.command;

import org.aalbatross.reactive.flows.OrderbookFlowManager;

import java.util.List;

public class CreateCommand implements Command, Validatable, Helpable {

    private String productId;

    @Override
    public boolean test(List<String> command) {
        if (command.size() == 2 && command.get(0).trim().equals("create")) {
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
            OrderbookFlowManager.INSTANCE.createNewOrderbook(productId);
    }

    @Override
    public String helpMessage() {
        return "create command syntax: create <product_id> \n example: create ETH-USD";
    }
}
