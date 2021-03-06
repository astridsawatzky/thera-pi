package CommonTools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class OpShowGesamt {
    private JLabel valGesamtOffen;
    private JLabel valSuchOffen;
    private JLabel valSuchGesamt;
    private JLabel valAnzahlSaetze;
    private BigDecimal gesamtOffen = BigDecimal.ZERO;
    private BigDecimal suchOffen= BigDecimal.ZERO;
    private BigDecimal suchGesamt;
    private int records;
    private JPanel auswertung;

    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);

    public OpShowGesamt() {
        auswertung = new JPanel();
        JLabel tmpLbl = new JLabel();

        FormLayout lay = new FormLayout(
                // 1 2 3 4 5 6 7
                "5dlu,145dlu,5dlu,100dlu:g,182dlu:g,5dlu,40dlu", // xwerte,
                // 1 2 3 4 5 6 7 8 9
                "0dlu,p,3dlu,p,2dlu,p,2dlu,p,5dlu" // ywerte
        );
        PanelBuilder builder = new PanelBuilder(lay);

        builder.getPanel()
               .setOpaque(false);
        CellConstraints cc = new CellConstraints();

        tmpLbl = builder.addLabel("Offene Posten gesamt:", cc.xy(2, 2, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        tmpLbl.setToolTipText("Summe OP in ausgewählten Rechnungsarten");
        valGesamtOffen = builder.addLabel("0,00", cc.xy(4, 2, CellConstraints.LEFT, CellConstraints.DEFAULT));
        valGesamtOffen.setForeground(Color.RED);
        Font f = valGesamtOffen.getFont();
        valGesamtOffen.setFont(f.deriveFont(f.getStyle() | Font.BOLD));

        tmpLbl = builder.addLabel("Offene Posten der letzten Abfrage:",
                cc.xy(2, 4, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        tmpLbl.setToolTipText("Summe OP in den zuletzt gesuchten Rechnungen");
        valSuchOffen = builder.addLabel("0,00", cc.xy(4, 4, CellConstraints.LEFT, CellConstraints.DEFAULT));
        valSuchOffen.setForeground(Color.RED);

        builder.addLabel("Summe Rechnunsbeträge der letzten Abfrage:",
                cc.xy(5, 2, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        valSuchGesamt = builder.addLabel("0,00", cc.xy(7, 2, CellConstraints.LEFT, CellConstraints.DEFAULT));
        valSuchGesamt.setForeground(Color.BLUE);

        builder.addLabel("Anzahl Datensätze der letzten Abfrage:",
                cc.xy(5, 4, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        valAnzahlSaetze = builder.addLabel("0", cc.xy(7, 4, CellConstraints.LEFT, CellConstraints.DEFAULT));
        valAnzahlSaetze.setForeground(Color.BLUE);

        auswertung.add(builder.getPanel());
    }

    public Component getPanel() {
        return auswertung;
    }

    public void schreibeGesamtOffen() {
        valGesamtOffen.setText(currencyFormat.format(gesamtOffen));
        auswertung.validate();
    }

    public BigDecimal getGesamtOffen() {
        return gesamtOffen;
    }

    public void setGesamtOffen(BigDecimal val) {
        gesamtOffen = val;
    }

    public void substFromGesamtOffen(BigDecimal val) {
        gesamtOffen = gesamtOffen.subtract(val);
        schreibeGesamtOffen();
    }

    public void ermittleGesamtOffen(boolean useRGR, boolean useAFR, boolean useVKR) {
        gesamtOffen = BigDecimal.ZERO;
        if (useRGR) {
            String offen = SqlInfo.holeEinzelFeld(
                    "select sum(roffen) from rgaffaktura where roffen > '0.00' AND rnr LIKE 'RGR-%'");
            if (!offen.isEmpty()) {
                gesamtOffen = gesamtOffen.add(BigDecimal.valueOf(Double.parseDouble(offen)));
            }
        }
        if (useAFR) {
            String offen = SqlInfo.holeEinzelFeld(
                    "select sum(roffen) from rgaffaktura where roffen > '0.00' AND rnr LIKE 'AFR-%'");

            if (!offen.isEmpty()) {
                gesamtOffen = gesamtOffen.add(BigDecimal.valueOf(Double.parseDouble(offen)));
            }
        }
        if (useVKR) {
            String offen = SqlInfo.holeEinzelFeld(
                    "select sum(v_offen) from verkliste where v_offen > '0.00' AND v_nummer LIKE 'VR-%'");
            if (!offen.isEmpty()) {
                gesamtOffen = gesamtOffen.add(BigDecimal.valueOf(Double.parseDouble(offen)));
            }
        }
        schreibeGesamtOffen();
    }

    public void schreibeSuchOffen() {
        try {
            valSuchOffen.setText(currencyFormat.format(suchOffen));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        auswertung.validate();
    }

    public BigDecimal getSuchOffen() {
        return suchOffen;
    }

    public void setSuchOffen(BigDecimal val) {
        suchOffen = val;
    }

    public void substFromSuchOffen(BigDecimal val) {
        suchOffen = suchOffen.subtract(val);
        schreibeSuchOffen();
    }

    public void delSuchOffen() {
        suchOffen = BigDecimal.ZERO;
        schreibeSuchOffen();
    }

    public void schreibeSuchGesamt() {
        try {
            valSuchGesamt.setText(currencyFormat.format(suchGesamt));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        auswertung.validate();
    }

    public BigDecimal getSuchGesamt() {
        return suchGesamt;
    }

    public void setSuchGesamt(BigDecimal val) {
        suchGesamt = val;
    }



    public void delSuchGesamt() {
        suchGesamt = BigDecimal.ZERO;
        schreibeSuchGesamt();
    }

    public void schreibeAnzRec() {
        valAnzahlSaetze.setText(Integer.toString(records));
        auswertung.validate();
    }

    public void incAnzRec() {
        records++;
    }

    public int getAnzRec() {
        return records;
    }

    public void setAnzRec(int val) {
        records = val;
    }

    public void delAnzRec() {
        records = 0;
        schreibeAnzRec();
    }

}
