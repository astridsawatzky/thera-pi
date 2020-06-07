package opRgaf;

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

import opRgaf.rezept.Money;

public class OffenePostenSummenPanel
{
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

    public OffenePostenSummenPanel() {
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

    public void setValGesamtOffen(Money money) {
        this.valGesamtOffen.setText(money.toPlainString());
    }

    public void setValSuchOffen(Money money) {
        this.valSuchOffen.setText(money.toPlainString());
    }

    public void setValSuchGesamt(Money money) {
        this.valSuchGesamt .setText(money.toPlainString());
    }

    public void setValAnzahlSaetze(int anzahl) {
        this.valAnzahlSaetze.setText(String.valueOf(anzahl));
    }






}
