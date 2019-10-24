package org.thera_pi.updater;

public class UpdateConsent {
    private final boolean download;
    private final boolean update;

    public UpdateConsent(boolean download, boolean update) {
        this.download = download;
        this.update = update;
    }

    boolean allowsDownload() {
        return download;
    }

     boolean allowsInstall() {
        return update;
    }

}
