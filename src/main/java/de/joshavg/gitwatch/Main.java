package de.joshavg.gitwatch;

import de.joshavg.gitwatch.cli.CliApplication;
import de.joshavg.gitwatch.state.AddRecursive;
import de.joshavg.gitwatch.state.AddRepo;
import de.joshavg.gitwatch.state.FetchAll;
import de.joshavg.gitwatch.state.ListStatus;
import de.joshavg.gitwatch.state.OpenTerminal;
import java.io.IOException;
import org.eclipse.jgit.api.errors.GitAPIException;

public class Main {

    public static void main(String... args) throws IOException, GitAPIException {
        ListStatus ls = new ListStatus();
        new CliApplication("GitWatch",
            ls,
            new AddRepo(),
            new FetchAll(),
            new AddRecursive(),
            new OpenTerminal())
            .onEmptyEnter(a -> ls.onEnter())
            .start();
    }

}
