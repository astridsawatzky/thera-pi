package dialoge;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class About extends JDialog {

    private Md5Panel md5 = new Md5Panel();


    public static void main(String[] args) {
        try {
            About dialog = new About();
            dialog.collectValues();


            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void collectValues() {
        md5.collectValues();

    }

    public About(Frame frame) {
        super(frame);
        setTitle("\u00fcber Thera-\u03C0");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        CardLayout cardLayout = new CardLayout(10, 10);
        JPanel contentPanel = new JPanel(cardLayout);


        JPanel credits = new Credits();
        contentPanel.add(credits, credits.getName());

        contentPanel.add(md5, md5.getName());
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> this.dispose());
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);

        JButton btnCredits = new JButton("Credits");
        btnCredits.addActionListener(e -> cardLayout.show(contentPanel, credits.getName()));
        buttonPane.add(btnCredits);

        JButton md5Button = new JButton("MD5");
        md5Button.addActionListener(e -> cardLayout.show(contentPanel, md5.getName()));
        buttonPane.add(md5Button);
    }

    public About() {
        this(null);

    }

}
