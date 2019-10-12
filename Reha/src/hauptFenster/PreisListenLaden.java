package hauptFenster;

import javax.swing.JOptionPane;

import systemEinstellungen.SystemConfig;
import systemEinstellungen.SystemPreislisten;

final class PreisListenLaden implements Runnable {
    private void Einlesen() {

        try {

            while (Reha.instance == null || Reha.instance.jxLinks == null || Reha.instance.jxRechts == null) {
                long zeit = System.currentTimeMillis();
                try {
                    Thread.sleep(50);
                    if (System.currentTimeMillis() - zeit > 20000) {
                        JOptionPane.showMessageDialog(null, "Fehler beim Starten des Systems ");
                        System.exit(0);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (isAktiv("Physio")) {
                new SocketClient().setzeInitStand("Preisliste Physio einlesen");
                SystemPreislisten.ladePreise("Physio");
            }
            if (isAktiv("Massage")) {
                new SocketClient().setzeInitStand("Preisliste Massage einlesen");
                SystemPreislisten.ladePreise("Massage");
            }
            if (isAktiv("Ergo")) {
                new SocketClient().setzeInitStand("Preisliste Ergo einlesen");
                SystemPreislisten.ladePreise("Ergo");
            }
            if (isAktiv("Logo")) {
                new SocketClient().setzeInitStand("Preisliste Logo einlesen");
                SystemPreislisten.ladePreise("Logo");
            }
            if (isAktiv("Reha")) {
                new SocketClient().setzeInitStand("Preisliste Reha einlesen");
                SystemPreislisten.ladePreise("Reha");
            }
            if (isAktiv("Podo")) {
                new SocketClient().setzeInitStand("Preisliste Podologie einlesen");
                SystemPreislisten.ladePreise("Podo");
            }
            if (SystemConfig.mitRs) {
                if (isAktiv("Rsport")) {
                    new SocketClient().setzeInitStand("Preisliste Rehasport einlesen");
                    SystemPreislisten.ladePreise("Rsport");
                }
                if (isAktiv("Ftrain")) {
                    new SocketClient().setzeInitStand("Preisliste Funktionstraining einlesen");
                    SystemPreislisten.ladePreise("Ftrain");
                }
            }

            SystemPreislisten.ladePreise("Common");

            System.out.println("Preislisten einlesen abgeschlossen");
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        try {
            new SocketClient().setzeInitStand("System-Init abgeschlossen!");
            Reha.instance.setzeInitEnde();
            Reha.instance.initok = true;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

    }

    public boolean isAktiv(String disziplin) {

        for (int i = 0; i < SystemConfig.rezeptKlassenAktiv.size(); i++) {
            if (SystemConfig.rezeptKlassenAktiv.get(i)
                                               .get(0)
                                               .toLowerCase()
                                               .startsWith(disziplin.toLowerCase())
                    || (disziplin.equals("Rsport") && SystemConfig.rezeptKlassenAktiv.get(i)
                                                                                     .get(0)
                                                                                     .toLowerCase()
                                                                                     .startsWith("rehasport"))
                    || (disziplin.equals("Ftrain") && SystemConfig.rezeptKlassenAktiv.get(i)
                                                                                     .get(0)
                                                                                     .toLowerCase()
                                                                                     .startsWith("funktionstrai"))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {
        Einlesen();
        int i = 0;
        while (!Reha.instance.initok) {
            i = i + 1;
            if (i > 10) {
                break;
            }
            try {
                Thread.sleep(100);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        new Thread(new ErsterLogin()).start();
    }

}