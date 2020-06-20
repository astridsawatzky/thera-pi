package org.therapi.reha.patient;

import javax.swing.JOptionPane;

import CommonTools.SqlInfo;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.event.IDocumentEvent;
import gui.Cursors;
import hauptFenster.Reha;

final class NewDokumentOOListener extends OoListener {

    NewDokumentOOListener(IOfficeApplication officeAplication, String xdatei, String xid,
            DokumentationPanel xeltern) {
        super(officeAplication, xdatei, xeltern);
    }

    @Override
    public void onUnload(IDocumentEvent arg0) {
        try {

            IDocument doc = arg0.getDocument();
            if (doc == null) {
                // System.out.println(geaendert+" - "+datei+" - "+neu+" doc = null ");
                return;
            }

            String file = arg0.getDocument()
                              .getPersistenceService()
                              .getLocation()
                              .getPath();
            file = file.substring(1)
                       .replace("%20", " ");
            if (datei.equals(file) && !geaendert) {
                // System.out.println(geaendert+" - "+datei+" - "+file+" - "+neu);
                arg0.getDocument()
                    .removeDocumentListener(this);
                // System.out.println("Listener entfernt - Datei nicht geändert"+file);
                warschoninsave = true;
            } else {
                // System.out.println(geaendert+" - "+datei+" - "+file+" - "+neu);
                new Thread() {
                    @Override
                    public void run() {
                        String nurDatei = datei.substring(datei.replace("\\", "/")
                                                               .lastIndexOf("/")
                                + 1);
                        int frage = JOptionPane.showConfirmDialog(null, "Die Dokumentationsdatei --> " + nurDatei
                                + " <-- wurde geändert\n\nWollen Sie die geänderte Fassung in die Patienten-Dokumentation übernehmen?",
                                "Wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
                        if (frage == JOptionPane.YES_OPTION) {
                            geaendert = false;
                            try {
                                Reha.instance.patpanel.dokumentation.setCursor(Cursors.wartenCursor);
                                int nummer = SqlInfo.erzeugeNummer("doku");
                                eltern.speichernOoDocs(nummer, -1, datei, -1, null, true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }.start();
                arg0.getDocument()
                    .removeDocumentListener(this);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
