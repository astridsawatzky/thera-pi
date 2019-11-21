package org.thera_pi.updater;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateFile {
    final File file;
    Version from;
    Version to;
    private static final Pattern r = Pattern.compile("therapi_([\\d]+_[\\d]+_[\\d]+)_([\\d]+_[\\d]+_[\\d]+)");

    public UpdateFile(File file) {
        this.file = file;
        extractVersions();

    }

    void extractVersions() {

        Matcher m = r.matcher(file.getName());

        if (m.find()) {
            String[] fromsplit = m.group(1)
                                  .split("_");
            from = new Version(Integer.parseInt(fromsplit[0]), Integer.parseInt(fromsplit[1]),
                    Integer.parseInt(fromsplit[2]));
            String[] toSplit = m.group(2)
                                .split("_");
            to = new Version(Integer.parseInt(toSplit[0]), Integer.parseInt(toSplit[1]), Integer.parseInt(toSplit[2]));
        } else {
            throw new IllegalArgumentException(file.getName());
        }
    }
}
