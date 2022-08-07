package org.aalbatross.command;

import java.util.List;

@FunctionalInterface
public interface Validatable {
    boolean test(List<String> command);
}
