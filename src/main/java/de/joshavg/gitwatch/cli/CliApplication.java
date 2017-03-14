package de.joshavg.gitwatch.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;

public class CliApplication {

    private final List<State> states;

    private final String title;

    private State currentState;

    private State defaultState;

    private boolean exit;

    private Consumer<CliApplication> onEmptyEnter;

    private String currentLine;

    public CliApplication(String title, State... states) {
        this.title = title;

        this.states = new ArrayList<>();
        this.states.addAll(Arrays.asList(states));

        defaultState = new DefaultState(this.states);
        currentState = defaultState;
        this.states.add(currentState);

        onEmptyEnter = app -> {
            // defaults to noop
        };
    }

    public CliApplication onEmptyEnter(Consumer<CliApplication> f) {
        onEmptyEnter = f;
        return this;
    }

    public void start() {
        initStates();
        welcome();

        currentState.onEnter();

        while (!exit) {
            currentLine = new Scanner(System.in).nextLine();
            if("cancel".equals(currentLine)) {
                setCurrentState(defaultState);
            } else if("".equals(currentLine) && currentState == defaultState) {
                onEmptyEnter.accept(this);
            } else {
                currentState.handle(currentLine);
            }
        }
    }

    public String getCurrentLine() {
        return currentLine;
    }

    private void welcome() {
        CliOut.writeln("Welcome to " + title);
        CliOut.writeln();
    }

    void outputStates() {
        CliOut.writeln("commands are:");

        states.forEach(s ->
            s.getTransitions().forEach(t ->
                CliOut.writeln("%s - %s", t, s.getTransitionDescription(t))));

        CliOut.writeln("=================================");
    }

    private void initStates() {
        states.forEach(s -> s.init(this));
    }

    void exit() {
        exit = true;
    }

    void setCurrentState(State s) {
        if(currentState != null) {
            currentState.onExit();
        }
        currentState = s;
        currentState.onEnter();
    }

    public void toDefaultState() {
        if(currentState != null) {
            currentState.onExit();
        }
        currentState = defaultState;
        currentState.onEnter();
    }

}
