package de.joshavg.gitwatch.state;

import static de.joshavg.gitwatch.cli.CliOut.writeln;

import de.joshavg.gitwatch.Config;
import de.joshavg.gitwatch.Repo;
import de.joshavg.gitwatch.cli.CliApplication;
import de.joshavg.gitwatch.cli.State;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class AddRecursive implements State {

    private CliApplication app;

    private int state;

    private List<Path> gitDirs;

    private int gitIndex;

    @Override
    public void init(CliApplication app) {
        this.app = app;
    }

    @Override
    public void onEnter() {
        state = 0;
        gitIndex = 0;
        writeln("enter path");
    }

    @Override
    public void handle(String input) {
        switch (state) {
            case 0:
                fetchGitDirs(input);
                break;
            case 1:
                askForName();
                break;
            case 2:
                setName(input);
                break;
            default:
                app.toDefaultState();
        }
    }

    private void setName(String input) {
        Config cfg = new Config();
        String configValue = cfg.getConfigValue("repo." + input);

        if (configValue != null) {
            writeln("repo name already exists, choose another one");
            return;
        }

        if (!"nope".equals(input)) {
            cfg.setConfigValue("repo." + input, gitDirs.get(gitIndex).toString());
            cfg.save();
        }

        gitIndex++;
        if (gitIndex < gitDirs.size()) {
            askForName();
        } else {
            app.toDefaultState();
        }
    }

    private void askForName() {
        writeln("name for repo in path %s - (nope to ignore this repo)", gitDirs.get(gitIndex));
        state = 2;
    }

    private void fetchGitDirs(String input) {
        writeln("searching for .git dirs in %s", input);
        List<Repo> repos = new Config().getRepos();
        List<Path> paths = repos.stream().map(r -> r.getPath())
            .collect(Collectors.toList());

        try {
            gitDirs = Files.walk(Paths.get(input))
                .filter(p -> p.toString().endsWith(".git") && p.toFile().isDirectory())
                .filter(p -> !paths.contains(p))
                .collect(Collectors.toList());
            for (Path p : gitDirs) {
                String stringPath = p.toString();
                writeln("found dir: %s", stringPath);
                // try to build
                new FileRepositoryBuilder()
                    .setGitDir(p.toFile())
                    .build();
            }

            writeln("that's all - next step [enter]");
            state = 1;
        } catch (IOException e) {
            writeln("io exception occurred: %s", e.getMessage());
            app.toDefaultState();
        }
    }

    @Override
    public List<String> getTransitions() {
        return Collections.singletonList("addr");
    }

    @Override
    public String getTransitionDescription(String transition) {
        return "add git dirs recursive";
    }
}
