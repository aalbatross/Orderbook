package org.aalbatross.reactive.source;

public interface Source {
    void start();

    boolean isRunning();

    void stop();
}
