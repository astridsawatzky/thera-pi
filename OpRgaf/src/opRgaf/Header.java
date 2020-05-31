package opRgaf;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import environment.Path;

public class Header extends JXHeader implements ChangeListener {
    private static final int BUCHEN = 0;
    private static final int MAHNEN = 1;

    private static final ImageIcon MAHNWESEN_ICON = new ImageIcon(
            Path.Instance.getProghome() + File.separator + "icons" + File.separator + "Mahnung.png");
    private static final String MAHNWESEN_BESCHREIBUNG = "<html>Hier erzeugen Sie Mahnungen für noch nicht bezahlte Rechnungen.<br><br>"
            + "Button <b>[suchen]</b> listet die Rechnungen der gewählten Kategorie, bei denen noch <br>"
            + "ein Betrag offen ist und die in der eingestellten Mahnstufe noch nicht gemahnt wurden.</html>";
    private static final String MAHNWESEN_TITEL = "Mahnwesen";
    private static final ImageIcon BUCHEN_ICON = new ImageIcon(
            Path.Instance.getProghome() + File.separator + "icons" + File.separator + "Guldiner.png");
    private static final String BUCHEN_BESCHREIBUNG = "<html>Hier haben Sie die Möglichkeit Rechnungen nach verschiedenen Kriterien zu suchen.<br><br>"
            + "Wenn Sie die Rechnung, die Sie suchen, gefunden haben und die Rechnung <b>vollständig bezahlt</b> wurde,<br>"
            + "genügt es völlig über <b>Alt+A</b> den Vorgang <b>Ausbuchen</b> zu aktivieren.<br><br>"
            + "Wurde lediglich eine <b>Teilzahlung</b> geleistet, muß diese zuvor im Textfeld <b>Geldeingang</b> eingetragen werden.</html>";
    private static final String BUCHEN_TITEL = "Bezahlte Rezeptgebühr- oder Ausfallrechnungen ausbuchen / Teilzahlungen buchen";
    private static final Logger logger = LoggerFactory.getLogger(Header.class);

    public Header() {
        ((JLabel) getComponent(1)).setVerticalAlignment(JLabel.NORTH);
        setHeader(BUCHEN);
    }

    public void setHeader(int header) {
        if (header == BUCHEN) {
            setTitle(BUCHEN_TITEL);
            setDescription(BUCHEN_BESCHREIBUNG);
            setIcon(BUCHEN_ICON);
        } else if (header == MAHNEN) {
            setTitle(MAHNWESEN_TITEL);
            setDescription(MAHNWESEN_BESCHREIBUNG);
            setIcon(MAHNWESEN_ICON);
        } else {
            logger.info("unexpected value for tabheaderSeletion " + header);
        }
        validate();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JTabbedPane pane = (JTabbedPane) e.getSource();
        setHeader(pane.getSelectedIndex());
    }
}
