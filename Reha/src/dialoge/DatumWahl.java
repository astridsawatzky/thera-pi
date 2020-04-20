package dialoge;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.JXPanel;

import hauptFenster.Reha;
import rehaContainer.RehaTP;
import terminKalender.TerminFenster;
import terminKalender.TerminFenster.Ansicht;

public class DatumWahl {



    private ActionListener al;

    private RehaSmartDialog rSmart = null;


    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    private String aktTag = "x";
    private String wahlTag = "y";

    public DatumWahl(int x, int y) {
        RehaTP jtp = new RehaTP();
        jtp.setBorder(null);
        jtp.setTitle("Wohin mit dem Termin???");
        jtp.setContentContainer(getForm());
        jtp.setVisible(true);

        rSmart = new RehaSmartDialog(null, "WohinmitTermin");

        rSmart.setModal(false);
        rSmart.setAlwaysOnTop(true);
        rSmart.setResizable(false);

        rSmart.setSize(new Dimension(225, 200));
        rSmart.setPreferredSize(new Dimension(225, 200));
        rSmart.getTitledPanel()
              .setTitle("Monatsübersicht");
        rSmart.setContentPanel(jtp.getContentContainer());

        x = 20;
        y = 500;
        rSmart.setLocation(x, y);
        rSmart.pack();
        rSmart.setVisible(true);


    }

    private JXPanel getForm() {

        JXPanel xbuilder = new JXPanel();
        xbuilder.setBorder(null);
        xbuilder.setLayout(new BorderLayout());
        xbuilder.setVisible(true);
        final JXMonthView monthView = new JXMonthView();
        monthView.setPreferredColumnCount(1);
        monthView.setPreferredRowCount(1);
        monthView.setTraversable(true);
        monthView.setShowingWeekNumber(true);
        al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (TerminFenster.getThisClass() != null) {
                    Date dat = monthView.getSelectionDate();
                    if (dat == null) {
                        return;
                    }
                    wahlTag = sdf.format(monthView.getSelectionDate());
                    if (wahlTag.equals(aktTag)) {
                        return;
                    }
                    aktTag = wahlTag;
                    Reha.instance.progLoader.ProgTerminFenster(1, Ansicht.NORMAL);
                    TerminFenster.getThisClass()
                                 .springeAufDatum(aktTag);
                } else {
                    Date dat = monthView.getSelectionDate();
                    if (dat == null) {
                        return;
                    }
                    wahlTag = sdf.format(monthView.getSelectionDate());
                    if (wahlTag.equals(aktTag)) {
                        return;
                    }
                    aktTag = wahlTag;
                    Reha.instance.progLoader.ProgTerminFenster(1, Ansicht.NORMAL);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            TerminFenster.getThisClass()
                                         .springeAufDatum(aktTag);

                        }
                    });
                }

            }
        };
        monthView.addActionListener(al);
        xbuilder.add(monthView, BorderLayout.CENTER);
        return xbuilder;
    }

}
