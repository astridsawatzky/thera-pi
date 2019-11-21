package org.thera_pi.updater;

import static org.junit.Assert.*;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class VersionsSiebTest {


    @Test
    public void not_matching_files_are_ignored() throws Exception {
        Version version= new Version(1,1,0);
        VersionsSieb sieb = new VersionsSieb( version);

        List<File> filesList = new LinkedList<>();
        filesList.add(new File("randomname"));
        List<File> resultList = sieb.select(filesList);
        assertEquals(0, resultList.size());
    }
    @Test
    public void matching_files_are_kept() throws Exception {
        Version version= new Version(1,1,0);
        VersionsSieb sieb = new VersionsSieb( version);

        List<File> filesList = new LinkedList<>();
        filesList.add(new File("randomname"));
        filesList.add(new File("therapi_1_1_0_1_1_1"));
        List<File> resultList = sieb.select(filesList);
        assertEquals(1, resultList.size());
    }

    @Test
    public void aFileMatchesWhenthefirstPartEqualsTheversion() throws Exception {


        List<File> filesList = new LinkedList<>();
        filesList.add(new File("randomname"));
        filesList.add(new File("therapi_1_1_0_1_1_1"));
        filesList.add(new File("therapi_1_0_0_1_1_1"));
        VersionsSieb sieb = new VersionsSieb( new Version(1,1,0));
        assertEquals(1, sieb.select(filesList).size());

        VersionsSieb sieb1 = new VersionsSieb( new Version(1,0,0));
        assertEquals(1, sieb1.select(filesList).size());

        VersionsSieb sieb2 = new VersionsSieb( new Version(2,1,0));
        assertEquals(0, sieb2.select(filesList).size());
    }



}
