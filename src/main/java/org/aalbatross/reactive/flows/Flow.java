package org.aalbatross.reactive.flows;

public interface Flow {
    String name();

    void start();

    void stop();

    boolean isRunning();
}
