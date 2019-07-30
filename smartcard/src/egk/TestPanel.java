package egk;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.List;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class TestPanel extends JPanel {

    /**
     * Launch the application.
     *
     * @throws CardException
     */
    public static void main(String[] args) throws CardException {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {

                    JFrame frame = new JFrame();

                    TestPanel panel = new TestPanel(TerminalFactory.getDefault()
                                                                   .terminals()
                                                                   .list());
                    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    frame.add(panel);
                    frame.setMinimumSize(new Dimension(600, 400));
                    frame.pack();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     *
     * @param list
     */
    public TestPanel(List<CardTerminal> list) {

        int y = 10;
        setLayout(null);

        for (CardTerminal terminal : list) {

            JLabel label = new JLabel(terminal.getName());
            label.setBounds(10, y += 60, 290, 64);

            TestLabel lblNewLabel = new TestLabel("nix los hier");
            lblNewLabel.setBounds(300, y, 131, 64);
            add(label);
            add(lblNewLabel);
            EgkReader egk = new EgkReader(terminal.getName());
            egk.addCardListener(lblNewLabel);
            Thread thread = new Thread(egk);
            thread.setDaemon(true);
            thread.start();
        }
    }

    class TestLabel extends JLabel implements CardListener {
        public TestLabel(String string) {
            super(string);

        }

        @Override
        public void cardInserted(CardTerminalEvent cardTerminalEvent) {
            setText("Juhu");

        }

        @Override
        public void cardRemoved(CardTerminalEvent cardTerminalEvent) {
            setText("oh nein!");

        }

    }
}
