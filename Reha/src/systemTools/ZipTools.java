package systemTools;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;

public class ZipTools {

    public static void scanZipFile(String zipname) {
        try {
            ZipInputStream zin = new ZipInputStream(new FileInputStream(zipname));
            while (zin.getNextEntry() != null) {
                zin.closeEntry();
            }
            zin.close();
        } catch (IOException e) {
        }
    }

}