package de.joshavg.gitwatch.cli;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

class DefaultState implements State {

    private final List<State> states;
    private CliApplication app;

    DefaultState(List<State> states) {
        this.states = states;
    }

    @Override
    public void init(CliApplication app) {
        this.app = app;
    }

    @Override
    public List<String> getTransitions() {
        return Collections.singletonList("exit");
    }

    @Override
    public String getTransitionDescription(String transition) {
        return "exits the application";
    }

    @Override
    public void handle(String input) {
        if ("exit".equals(input)) {
            app.exit();
            return;
        } else if ("help".equals(input) || input == null) {
            app.outputStates();
            return;
        }

        Optional<State> stateOptional = states.stream()
            .filter(s ->
                s.getTransitions().contains(input)
                    || s.getTransitions().stream().anyMatch(t -> input.startsWith(t + " ")))
            .findFirst();

        if (stateOptional.isPresent()) {
            app.setCurrentState(stateOptional.get());
        } else {
            app.outputStates();
        }
    }
}
