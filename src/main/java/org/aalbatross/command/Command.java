package org.aalbatross.command;

import java.util.List;

public interface Command {
    void handle(List<String> command);
}
