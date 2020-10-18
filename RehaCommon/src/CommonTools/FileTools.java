package CommonTools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileTools {
    public static void deleteAllFiles(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (int x = 0; x < files.length; x++) {
                    files[x].delete();
                }
            }
        }
    }

    public static boolean delFileWithSuffixAndPraefix(File dir, String xpraefix, String xsuffix) {
        final String suffix = xsuffix;
        final String praefix = xpraefix;
        FilenameFilter fileFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.startsWith(praefix) && name.endsWith(suffix));
            }

        };
        File[] files = dir.listFiles(fileFilter);
        boolean ok = true;
        for (int i = 0; i < files.length; i++) {
            if (!files[i].delete()) {
                ok = false;
            }
        }
        return (files.length == 0 || !ok ? false : true);
    }

    public static boolean delFileWithPraefix(File dir, String xpraefix) {

        final String praefix = xpraefix;
        FilenameFilter fileFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.startsWith(praefix));
            }

        };
        File[] files = dir.listFiles(fileFilter);
        boolean ok = true;
        for (int i = 0; i < files.length; i++) {
            if (!files[i].delete()) {
                ok = false;
            }
        }
        return (files.length == 0 || !ok ? false : true);
    }

    public static byte[] File2ByteArray(File file) throws Exception {


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FileInputStream fileInputStream = new FileInputStream(file);

        byte[] buffer = new byte[16384];

        for (int len = fileInputStream.read(buffer); len > 0; len = fileInputStream.read(buffer)) {
            byteArrayOutputStream.write(buffer, 0, len);
        }

        fileInputStream.close();

        return byteArrayOutputStream.toByteArray();
    }

    public static void ByteArray2File(byte[] xdata, String fileout) {


        try {



            // Byte Array laden
            byte[] data = xdata;
            // Zu erzeugende Datei angeben
            File f = new File(fileout);

            // Datei schreiben
            FileOutputStream fileOut = new FileOutputStream(f);
            fileOut.write(data);
            fileOut.flush();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String testeString(String webstring, String fundstelle, String sbeginn, String sende) {
        int aktuell = 0;
        int wo = 0;

        String meinweb = new String(webstring);
        int lang = meinweb.length();

        wo = webstring.indexOf(fundstelle, aktuell);
        String nurBild = "";
        boolean start = false;
        boolean austritt = false;
        int ende = 0;
        for (int i = wo; i < lang; i++) {
            for (int d = 0; d < 1; d++) {
                if ((meinweb.substring(i, i + 1)
                            .equals(sbeginn))
                        && (!start)) {

                    i++;
                    start = true;
                    break;
                }
                if ((meinweb.substring(i, i + 1)
                            .equals(String.valueOf(ende)))
                        && (start)) {
                    start = false;
                    ende = i;
                    austritt = true;
                    break;
                }
            }
            if (austritt) {
                break;
            }
            if (start) {
                nurBild = nurBild + meinweb.substring(i, i + 1);
            }
        }
        return new String(nurBild);
    }

}
