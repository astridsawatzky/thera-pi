package org.thera_pi.updater;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class UpdaterFactory {


    UpdateRepository  emptyrepo =new EmptyRepository();
    private UpdateRepository repo=emptyrepo;
    private Stoppable killme = (latch) -> latch.countDown();
    private UpdateUI ui = () -> (new UpdateConsent(true, true));

    public UpdaterFactory withRepository(UpdateRepository repo) {
        this.repo = repo;
        return this;
    }

    public UpdaterFactory withStoppable(Stoppable killme) {
        this.killme = killme;
        return this;
    }

    public UpdaterFactory withUI(UpdateUI userinterface) {
        this.ui = userinterface;
        return this;
    }

    public Updater build() {
        Updater updater = new Updater();
        updater.setRepo(repo);
        updater.setMyParent(killme);
        updater.setUi(ui);
        return updater;
    }
    private static final class EmptyRepository implements UpdateRepository {
        @Override
        public List<File> filesList() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public int downloadFiles(List<File> neededList, Path path) {
            return 0;
        }
    }
}
