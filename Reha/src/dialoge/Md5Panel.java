package dialoge;

import java.awt.Font;
import java.awt.GridLayout;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Md5Panel extends JPanel implements ValuesReceiver {
    private static final Logger logger = LoggerFactory.getLogger(Md5Panel.class);

    public Md5Panel() {
        setName("md5");
        setLayout(new GridLayout(1, 0));

        add(new JScrollPane(txtpnBob, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    }

    private JTextPane txtpnBob = new JTextPane();

    void collectValues() {
        new MD5gatherer(this).execute();
    }

    public void setValues(Map<String, String> values) {
        Document document = txtpnBob.getDocument();
        txtpnBob.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        TreeSet<String> myset = new TreeSet<>();
        myset.addAll(values.keySet());

        for (String entry : myset) {
            try {
                document.insertString(document.getLength(),
                        String.format("%1$-" + 25 + "s", entry) + "=" + values.get(entry) + "\n", null);
            } catch (BadLocationException e) {
                logger.error("could not insert md5 for entry: " + entry, e);
            }
        }
    }
}
