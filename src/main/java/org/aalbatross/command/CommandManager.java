package org.aalbatross.command;

import java.util.List;

public enum CommandManager implements Command, Helpable {
    INSTANCE;

    @Override
    public void handle(List<String> command) {
        if (command.isEmpty()) {
            System.out.println(helpMessage());
            return;
        }
        var cmd = command.get(0).trim();
        switch (cmd) {
            case "create":
                new CreateCommand().handle(command);
                break;
            case "drop":
                new DropCommand().handle(command);
                break;
            case "list":
                new ListCommand().handle(command);
                break;
            case "display":
                new DisplayCommand().handle(command);
                break;
            case "exit":
                System.exit(0);
                break;
            default:
                System.out.println(helpMessage());
        }
    }

    @Override
    public String helpMessage() {
        return "Try with any 5 commands - create, drop, list, display, exit.";
    }
}
