package environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LadeProg {

    public LadeProg(String prog) {
        Logger logger = LoggerFactory.getLogger(LadeProg.class);
        String progname = null;
        if (prog.indexOf(" ") >= 0) {
            progname = prog.split(" ")[0];
        } else {
            progname = prog;
        }
        File f = new File(progname);
        if (!f.exists()) {
            JOptionPane.showMessageDialog(null, "Diese Software ist auf Ihrem System nicht installiert!");
            return;
        }
        final String xprog = prog;
        new Thread() {
            @Override
            public void run() {
                try {

                    List<String> list = Arrays.asList(xprog.split(" "));
                    ArrayList<String> alist = new ArrayList<String>(list);
                    alist.add(0, "-jar");
                    alist.add(0, "-Djava.net.preferIPv4Stack=true");
                    alist.add(0, "java");

                    logger.debug(alist.stream()
                                      .collect(Collectors.joining(" ")));
                    Process process = new ProcessBuilder(alist).start();

                    InputStream is = process.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;

                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }

                    is.close();
                    isr.close();
                    br.close();
                    process = null;

                } catch (IOException e) {

                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "Fehler beim starten des Moduls, Fehlermeldung ist\n" + e.getMessage()
                                                                                     .toString());
                }

            }
        }.start();

    }

}
