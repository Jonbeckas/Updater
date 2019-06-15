package updater;

import java.net.URI;
import java.net.URL;

public class UpdaterSettings {
    /*githubuser is the user who has the repository.
      repository is the repository with the releases.
     */
    private String githubuser;
    private String repository;
    private onSucces onSucces;
    private onError onError;
    private boolean getPreRelease;

    public String getGithubuser() {
        return githubuser;
    }

    public String getRepository() {
        return repository;
    }

    public UpdaterSettings.onSucces getOnSucces() {
        return onSucces;
    }

    public UpdaterSettings.onError getOnError() {
        return onError;
    }
    public  boolean isGetPreRelease()  {
        return this.getPreRelease;
    }


    public UpdaterSettings(String githubuser, String repository,onError onError, onSucces onSucces, boolean getPreRelease) {
        this.repository = repository;
        this.githubuser = githubuser;
        this.onError = onError;
        this.onSucces = onSucces;
        this.getPreRelease = getPreRelease;
    }

    public interface onError {
        void onError(String version,Exception e);
    }
    public interface onSucces {
        void onSucces(String version, URL download);
    }
}
