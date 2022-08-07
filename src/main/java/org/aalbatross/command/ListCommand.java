package org.aalbatross.command;

import org.aalbatross.reactive.flows.OrderbookFlowManager;

import java.util.List;

public class ListCommand implements Validatable, Command, Helpable {
    @Override
    public void handle(List<String> command) {
        if (test(command)) {
            System.out.println(OrderbookFlowManager.INSTANCE.list());
        }
    }

    @Override
    public String helpMessage() {
        return "list command syntax : list";
    }

    @Override
    public boolean test(List<String> command) {
        if (command.size() == 1 && command.get(0).trim().equals("list")) {
            return true;
        } else {
            System.out.println(helpMessage());
            return false;
        }
    }
}
