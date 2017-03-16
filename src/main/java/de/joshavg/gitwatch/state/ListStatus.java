package de.joshavg.gitwatch.state;

import com.jakewharton.fliptables.FlipTableConverters;
import de.joshavg.gitwatch.Config;
import de.joshavg.gitwatch.Repo;
import de.joshavg.gitwatch.cli.CliApplication;
import de.joshavg.gitwatch.cli.CliOut;
import de.joshavg.gitwatch.cli.State;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class ListStatus implements State {

    private CliApplication app;

    @Override
    public void init(CliApplication app) {
        this.app = app;
    }

    @Override
    public void onEnter() {
        List<Repo> repos = new Config().getRepos();
        List<String[]> table = new ArrayList<>();
        repos.sort(Comparator.comparing(Repo::getName));

        for (Repo repo : repos) {
            try {
                Repository gitRepo = new FileRepositoryBuilder()
                    .setGitDir(repo.getPath().toFile())
                    .build();
                Git git = new Git(gitRepo);

                Status status = git.status().call();
                BranchTrackingStatus bts = BranchTrackingStatus.of(gitRepo, gitRepo.getBranch());

                if ("ls".equals(app.getCurrentLine()) || !status.isClean()) {
                    table.add(new String[]{
                        repo.getName(),
                        gitRepo.getBranch(),
                        status.isClean() ? "✓" : "✗",
                        Integer.toString(status.getModified().size()),
                        bts == null ? "?" : Integer.toString(bts.getAheadCount()),
                        bts == null ? "?" : Integer.toString(bts.getBehindCount())});
                }
            } catch (IOException e) {
                CliOut.writeln("error instantiating repo: %s", e.getMessage());
            } catch (GitAPIException e) {
                CliOut.writeln("error fetching repo status: %s", e.getMessage());
            } catch (NoWorkTreeException e) {
                CliOut.writeln("path has no work tree: %s", repo.getPath().toFile());
            }
        }

        CliOut.writeln(FlipTableConverters.fromObjects(
            new String[]{"Name", "Branch", "Status", "Modified", "Ahead", "Behind"},
            table.toArray(new String[0][])));

        app.toDefaultState();
    }

    @Override
    public List<String> getTransitions() {
        return Arrays.asList("ls", "lsc");
    }

    @Override
    public String getTransitionDescription(String transition) {
        return "list the status of all known repos";
    }
}
