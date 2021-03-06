package rehaInternalFrame;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.FocusListener;
import java.beans.PropertyVetoException;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;

import CommonTools.RehaEvent;
import CommonTools.RehaEventClass;
import CommonTools.RehaEventListener;
import events.PatStammEvent;
import events.PatStammEventClass;
import hauptFenster.AktiveFenster;
import hauptFenster.FrameSave;
import hauptFenster.Reha;

public class JPatientInternal extends JRehaInternal implements FocusListener, RehaEventListener {
    /**
     * 
     */
    private static final long serialVersionUID = -8069000862982060024L;
    RehaEventClass rEvent = null;

    public JPatientInternal(String titel, ImageIcon img, int desktop) {
        super(titel, img, desktop);
        this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        rEvent = new RehaEventClass();
        rEvent.addRehaEventListener(this);
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent arg0) {
        // System.out.println("Internal-Pat-Frame in schliessen***************");
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent arg0) {
        try {
            //// System.out.println("Lösche Patient von Desktop-Pane =
            //// "+Reha.instance.desktops[this.desktop]);
            // Nächsten JInternalFrame aktivieren
            if (!this.isIcon) {
                new FrameSave((Dimension) this.getSize()
                                              .clone(),
                        (Point) this.getLocation()
                                    .clone(),
                        Integer.valueOf(this.desktop), Integer.valueOf((this.getImmerGross() ? 1 : 0)),
                        String.valueOf("patient.ini"), String.valueOf("Patient"));
            }

            Reha.instance.aktiviereNaechsten(this.desktop);
            // JInternalFram von Desktop lösen
            Reha.instance.desktops[this.desktop].remove(this);
            // Listener deaktivieren
            rEvent.removeRehaEventListener(this);
            rEvent = null;
            this.removeInternalFrameListener(this);

            try {
                Reha.getThisFrame()
                    .requestFocus();
                /*
                 * Reha.instance.patpanel.fl = null; Reha.instance.patpanel.kli = null;
                 * Reha.instance.patpanel.gplst = null; Reha.instance.patpanel.newPolicy = null;
                 */
            } catch (Exception ex) {
                //// System.out.println("Fehler beim schließen des IFrames");
                // ex.printStackTrace();
            }

            String s1 = String.valueOf("#CLOSING");
            String s2 = "";
            PatStammEvent pEvt = new PatStammEvent(this);
            pEvt.setPatStammEvent("PatSuchen");
            pEvt.setDetails(s1, s2, "");
            PatStammEventClass.firePatStammEvent(pEvt);

            //// System.out.println("Internal-Pat-Frame in geschlossen***************");
            Reha.instance.aktiviereNaechsten(this.desktop);
            Reha.instance.patpanel.allesAufraeumen();
          
            /*
             * if(Reha.instance.patpanel.jry != null){ Reha.instance.patpanel.jry = null;
             * Reha.instance.patpanel = null; }
             */
            // Gutachten.gutachten = null;
            // Historie.historie = null;
            // Dokumentation.doku = null;
            // AktuelleRezepte.aktRez = null;
            // TherapieBerichte.aktBericht = null;
            this.destroyTitleBar();
            this.nord = null;
            this.inhalt = null;
            this.thisContent = null;

            this.removeAll();
            this.dispose();
            super.dispose();

            final String name = this.getName();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    AktiveFenster.loescheFenster(name);
                    Reha.instance.progLoader.loeschePatient();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void setzeSuche() {
        Reha.instance.patpanel.setzeFocus();
    }

    @Override
    public void setzeTitel(String stitel) {
        super.setzeTitel(stitel);
        repaint();

    }

    public boolean isActivated() {
        if (!this.isActive) {
            return false;
        } else {
            return true;
        }
    }

    public void activateInternal() {
        this.isActive = true;
        this.aktiviereDiesenFrame(this.getName());
        repaint();
    }

    @Override
    public void rehaEventOccurred(RehaEvent evt) {
        if (evt.getRehaEvent()
               .equals("REHAINTERNAL")) {
            // System.out.println("es ist ein Reha-Internal-Event");
        }
        if (evt.getDetails()[0].equals(this.getName())) {
            if (evt.getDetails()[1].equals("#ICONIFIED")) {
                try {
                    this.setIcon(true);
                    isIcon = true;
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                }
                this.setActive(false);
            }
        }
        if (evt.getDetails()[0].equals(this.getName())) {
            if (evt.getDetails()[1].equals("#DEICONIFIED")) {
                try {
                    this.setIcon(false);
                    isIcon = false;
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                }
                this.setActive(true);
                repaint();
            }
        }
    }
}
