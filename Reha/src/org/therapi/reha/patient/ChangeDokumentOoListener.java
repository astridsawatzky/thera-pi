package org.therapi.reha.patient;

import javax.swing.JOptionPane;

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.event.IDocumentEvent;
import gui.Cursors;
import hauptFenster.Reha;

final class ChangeDokumentOoListener extends OoListener {
    protected String id;

    ChangeDokumentOoListener(IOfficeApplication officeAplication, String xdatei, String xid,
            DokumentationPanel xeltern) {
        super(officeAplication, xdatei, xeltern);
        id=xid;
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
            if (geaendert) {
                try {
                    final String xfile = file;
                    final int xid = Integer.parseInt(id);
                    // final IDocumentEvent xarg0 = arg0;
                    Thread.sleep(50);
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                String nurDatei = datei.substring(datei.replace("\\", "/")
                                                                       .lastIndexOf("/")
                                        + 1);
                                int frage = JOptionPane.showConfirmDialog(null, "Die Dokumentationsdatei --> "
                                        + nurDatei
                                        + " <-- wurde geändert\n\nWollen Sie die geänderte Fassung in die Patienten-Dokumentation übernehmen?",
                                        "Wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
                                if (frage == JOptionPane.YES_OPTION) {
                                    geaendert = false;
                                    try {
                                        Reha.instance.patpanel.dokumentation.setCursor(Cursors.wartenCursor);
                                        eltern.speichernOoDocs(xid, -1, xfile, -1, null, false);
                                    } catch (Exception e) {

                                        e.printStackTrace();
                                    }

                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }.start();
                    arg0.getDocument()
                        .removeDocumentListener(this);
                    warschoninsave = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if ( !geaendert) {
                // System.out.println(geaendert+" - "+datei+" - "+file+" - "+neu);
                arg0.getDocument()
                    .removeDocumentListener(this);
                // System.out.println("Listener entfernt - Datei nicht geändert"+file);
                warschoninsave = true;
            } else {
                // System.out.println("else");
                // System.out.println("Datei equals(file) = "+datei.equals(file));
                // System.out.println("Datei = "+datei);
                // System.out.println("File = "+file);
                // System.out.println("geändert = "+geaendert);
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