package org.thera_pi.updater;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Meeeeh {

    private static Logger logger = LoggerFactory.getLogger(Meeeeh.class);

    public static void main(String[] args) {
        String htttpaddr = "https://gitlab.com/thera-pi/thera-pi/-/archive/entwicklung/thera-pi-entwicklung.zip";
        try {
           URL url = new URL(htttpaddr);
            Path target = Paths.get("C:\\RehaVerwaltung\\temp" + "\\" + "thera-pi-entwicklung.zip");

            java.nio.file.Files.copy(url.openStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error("bad things happen here", e);
        }

    }

}
