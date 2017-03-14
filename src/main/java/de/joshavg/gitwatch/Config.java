package de.joshavg.gitwatch;

import de.joshavg.gitwatch.cli.CliOut;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

public class Config {

    private static class ConfigException extends RuntimeException {

        private ConfigException(Exception parent) {
            super(parent);
        }
    }

    private Properties props;
    private Path path;

    public Config() {
        String home = System.getProperty("user.home");
        path = Paths.get(home + "/.gitwatch.properties");

        if (!path.toFile().exists()) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                CliOut.writeln("ERROR - cannot create config file");
                throw new ConfigException(e);
            }
        }

        props = new Properties();
        try {
            props.load(new FileInputStream(path.toString()));
        } catch (IOException e) {
            CliOut.writeln("ERROR - cannot load config file");
            throw new ConfigException(e);
        }
    }

    public Path getPath() {
        return path;
    }

    public String getConfigValue(String key) {
        return props.getProperty(key);
    }

    public void setConfigValue(String key, String value) {
        props.setProperty(key, value);
    }

    public List<Repo> getRepos() {
        List<Repo> values = new ArrayList<>();

        for (Entry<Object, Object> e : props.entrySet()) {
            String key = e.getKey().toString();
            String value = e.getValue().toString();
            if (key.startsWith("repo.")) {
                values.add(new Repo(key.substring(5), Paths.get(value)));
            }
        }

        return values;
    }

    public void save() {
        try {
            props.store(new FileWriter(path.toString()), "");
        } catch (IOException e) {
            CliOut.writeln("ERROR - cannot store config file");
            throw new ConfigException(e);
        }
    }

}
