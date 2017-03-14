package de.joshavg.gitwatch.state;

import de.joshavg.gitwatch.Config;
import de.joshavg.gitwatch.cli.CliApplication;
import de.joshavg.gitwatch.cli.CliOut;
import de.joshavg.gitwatch.cli.State;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class AddRepo implements State {

    private CliApplication app;

    private Path currentPath;

    @Override
    public void init(CliApplication app) {
        this.app = app;
    }

    @Override
    public void onEnter() {
        currentPath = null;
        CliOut.writeln("enter path");
    }

    @Override
    public List<String> getTransitions() {
        return Collections.singletonList("add");
    }

    @Override
    public String getTransitionDescription(String transition) {
        return "adds a repo to the list";
    }

    @Override
    public void handle(String input) {
        if (currentPath == null) {
            fetchGitDir(input);
        } else {
            fetchName(input);
        }
    }

    private void fetchName(String input) {
        if (input.isEmpty()) {
            CliOut.writeln("enter a name");
            return;
        }

        Config config = new Config();
        String value = config.getConfigValue("repo." + input);
        if (value != null) {
            CliOut.writeln("name already in use, provide a new one");
            return;
        }

        config.setConfigValue("repo." + input, currentPath.toAbsolutePath().toString());
        config.save();
        app.toDefaultState();
    }

    private void fetchGitDir(String input) {
        try {
            Path inputPath = Paths.get(input);
            if (!inputPath.toFile().exists()) {
                throw new InvalidPathException(input, "path does not exist");
            }

            Path gitDir = inputPath.resolve(".git");
            if (!gitDir.toFile().exists()) {
                throw new InvalidPathException(gitDir.toString(), "git dir does not exist");
            }

            currentPath = gitDir;
            CliOut.writeln("name?");
        } catch (InvalidPathException e) {
            CliOut.writeln("path is invalid: %s", e.getReason());
        }
    }
}
