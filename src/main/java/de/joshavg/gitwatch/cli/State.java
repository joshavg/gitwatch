package de.joshavg.gitwatch.cli;

import java.util.Collections;
import java.util.List;

public interface State {

    default void init(CliApplication app) {
    }

    default void onEnter() {
    }

    default void onExit() {
    }

    default void handle(String input) {
    }

    default List<String> getTransitions() {
        return Collections.emptyList();
    }

    default String getTransitionDescription(String transition) {
        return "";
    }

}
