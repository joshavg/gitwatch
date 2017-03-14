package de.joshavg.gitwatch;

import java.nio.file.Path;

public class Repo {

    private final String name;
    private final Path path;

    public Repo(String name, Path path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }
}
