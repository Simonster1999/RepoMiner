import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import java.io.File;

public class RepoMiner {

    public static void main (String[] args) throws InvalidRemoteException, TransportException, GitAPIException {

        Git git = Git.cloneRepository()
                .setURI("https://github.com/matekd/group-02-web.git")
                .setDirectory(new File("/testClone"))
                .call();
    }
}
