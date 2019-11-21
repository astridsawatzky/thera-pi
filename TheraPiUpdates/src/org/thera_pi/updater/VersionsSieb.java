package org.thera_pi.updater;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class VersionsSieb implements DateiSieb {

    private Version version;

    public VersionsSieb(Version currentVersion) {
        this.version = currentVersion;
    }

    @Override
    public List<File> select(List<File> filesList) {

        List<UpdateFile> updatefiles = new LinkedList<UpdateFile>();


        List<File> result = filesList.stream()
                                     .filter(f -> f.getName()
                                                   .matches("therapi_[\\d]+_[\\d]+_[\\d]+_[\\d]+_[\\d]+_[\\d]+"))
                                     .map(UpdateFile::new)
                                     .filter(u-> u.from.equals(version))
                                     .map(u->u.file)
                                     .collect(Collectors.toList());


        return result;
    }


}
