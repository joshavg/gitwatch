package de.joshavg.gitwatch.state;

import de.joshavg.gitwatch.Config;
import de.joshavg.gitwatch.Repo;
import de.joshavg.gitwatch.cli.CliApplication;
import de.joshavg.gitwatch.cli.CliOut;
import de.joshavg.gitwatch.cli.State;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class FetchAll implements State {

    private CliApplication app;

    @Override
    public void init(CliApplication app) {
        this.app = app;
    }

    @Override
    public void onEnter() {
        List<Repo> repos = new Config().getRepos();

        for (Repo repo : repos) {
            try {
                Repository gitRepo = new FileRepositoryBuilder()
                    .setGitDir(repo.getPath().toFile())
                    .build();
                Git git = new Git(gitRepo);
                git.fetch().setCheckFetchedObjects(true).call();

                CliOut.writeln("%s: %d uncommitted changes", repo.getName(),
                    git.status().call().getUncommittedChanges().size());
            } catch (IOException e) {
                CliOut.writeln("io exception: %s", e.getMessage());
            } catch (InvalidRemoteException e) {
                CliOut.writeln("invalid remote: %s", e.getMessage());
            } catch (TransportException e) {
                CliOut.writeln("communication error: %s", e.getMessage());
            } catch (GitAPIException e) {
                CliOut.writeln("git error: %s", e.getMessage());
            }
        }

        app.toDefaultState();
    }

    @Override
    public List<String> getTransitions() {
        return Collections.singletonList("fa");
    }

    @Override
    public String getTransitionDescription(String transition) {
        return "fetches all known repos";
    }
}
