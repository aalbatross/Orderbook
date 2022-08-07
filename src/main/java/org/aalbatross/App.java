package org.aalbatross;

import org.aalbatross.command.CommandManager;
import org.aalbatross.reactive.flows.OrderbookFlowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final String WELCOME = "Welcome to Orderbook View.";

    public static void main(String[] args) {
        System.out.println(WELCOME);
        System.out.println(CommandManager.INSTANCE.helpMessage());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            OrderbookFlowManager.INSTANCE.closeAll();
            LOGGER.info("Closing app.");
        }));

        Set<String> command = Set.of("create", "drop", "display", "list");

        while (true) {
            Scanner sc = new Scanner(System.in);
            var commandInput = sc.nextLine();
            var cmds = Arrays.asList(commandInput.split("\\s"));
            CommandManager.INSTANCE.handle(cmds);
        }
    }
}
