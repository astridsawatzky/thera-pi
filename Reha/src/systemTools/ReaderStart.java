package systemTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import hauptFenster.Reha;
import systemEinstellungen.SystemConfig;

public class ReaderStart {
    public ReaderStart(String datei) {
        final String xdatei = datei;
        new Thread() {
            @Override
            public void run() {
                Process process;
                try {
                    String readerstring = (SystemConfig.hmFremdProgs.get("AcrobatReader")
                                                                    .contains("AcroR") ? "/n" : "");
                    process = new ProcessBuilder(SystemConfig.hmFremdProgs.get("AcrobatReader"), readerstring,
                            xdatei).start();
                    InputStream is = process.getInputStream();

                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    // String line;
                    Reha.instance.progressStarten(false);
                    while ((br.readLine()) != null) {
                        // System.out.println("Lade Adobe "+line);
                    }
                    is.close();
                    isr.close();
                    br.close();
                    is = null;
                    br = null;
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }.start();
    }

}