package de.joshavg.gitwatch.state;

import static de.joshavg.gitwatch.cli.CliOut.writeln;

import de.joshavg.gitwatch.Config;
import de.joshavg.gitwatch.cli.CliApplication;
import de.joshavg.gitwatch.cli.State;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class OpenTerminal implements State {

    private CliApplication app;

    @Override
    public void init(CliApplication app) {
        this.app = app;
    }

    @Override
    public void onEnter() {
        writeln("Enter name");
    }

    @Override
    public void handle(String input) {
        if ("config".equals(input)) {
            openConfigEditor();
            app.toDefaultState();
            return;
        }

        String path = new Config().getConfigValue("repo." + input);
        if (path == null) {
            writeln("unknown repo, try again");
            return;
        }

        try {
            File wd = Paths.get(path).resolve("..").toFile();
            Runtime.getRuntime().exec("x-terminal-emulator", null, wd);
        } catch (IOException e) {
            writeln("could not open terminal: %s", e.getMessage());
        }

        app.toDefaultState();
    }

    private void openConfigEditor() {
        try {
            Runtime.getRuntime().exec(new String[]{"xdg-open", new Config().getPath().toString()});
        } catch (IOException e) {
            writeln("could not open config: %s", e.getMessage());
        }
    }

    @Override
    public List<String> getTransitions() {
        return Collections.singletonList("open");
    }

    @Override
    public String getTransitionDescription(String transition) {
        return "opens a terminal at a repository path";
    }
}
