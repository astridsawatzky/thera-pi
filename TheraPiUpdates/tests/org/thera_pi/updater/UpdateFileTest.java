package org.thera_pi.updater;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;


public class UpdateFileTest {


    @Test
    public void renameMe() throws Exception {
        File file = new File("therapi_1_0_0_1_1_1");
        UpdateFile ufile = new UpdateFile(file );
        ufile.extractVersions();

    }

}
