package dialoge;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.JCompTools;
import CommonTools.JRtaComboBox;
import CommonTools.JRtaTextField;
import abrechnung.AbrechnungDlg;
import emailHandling.EmailSendenExtern;
import environment.Path;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;
import gui.Cursors;
import hauptFenster.Reha;
import oOorgTools.OOTools;
import systemEinstellungen.SystemConfig;
import systemTools.IconListRenderer;

public class EmailDialog extends JXDialog implements  RehaTPEventListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private ActionListener al = null;
    private RehaTPEventClass rtp = null;
    private JXTitledPanel jtp = null;
    private MouseAdapter mymouse = null;
    private PinPanel pinPanel = null;
    private JXPanel content = null;
    // PatientToolBarLogic eltern = null;
    private JButton[] buts = { null, null };
    private JRtaTextField[] tf = { null, null, null };
    private JRtaComboBox cmb = null;
    private JTextArea jta;
    private JList jList = null;
    // JXTable tab;
    // DefaultTableModel mod;
    private String recipients, betreff, mailtext;
    private int postfach = 0;
    private ArrayList<String[]> attachments;
    // private String info = null;

    private AbrechnungDlg abrDlg = null;

    public EmailDialog(JXFrame owner, String titel, String recipients, String betreff, String mailtext,
            ArrayList<String[]> attachments, int postfach, boolean direktsenden) {
        super(owner, (JComponent) Reha.getThisFrame()
                                      .getGlassPane());
        installListener();
        this.setUndecorated(true);
        this.setName("EMAILDlg");
        this.recipients = recipients;
        if (postfach == 0) {
            this.recipients = this.recipients + ((Boolean) SystemConfig.hmIcalSettings.get("aufeigeneemail")
                    ? "," + SystemConfig.hmEmailExtern.get("SenderAdresse")
                    : "");
        } else {
            this.recipients = this.recipients + ((Boolean) SystemConfig.hmIcalSettings.get("aufeigeneemail")
                    ? "," + SystemConfig.hmEmailIntern.get("SenderAdresse")
                    : "");
        }
        this.betreff = betreff;
        this.mailtext = mailtext;
        this.attachments = attachments;
        this.postfach = postfach;
        // this.eltern = xeltern;
        // this.isSMS = SMS;
        // this.info = info;
        // this.nummer = nummer;
        this.jtp = new JXTitledPanel();
        this.jtp.setName("EMAILDlg");
        this.mymouse = new DragWin(this);
        this.jtp.addMouseListener(mymouse);
        this.jtp.addMouseMotionListener(mymouse);
        this.jtp.setContentContainer(getContent());
        this.jtp.setTitleForeground(Color.WHITE);
        this.jtp.setTitle("<html>" + "pi-Emailversand" + "</html>");
        this.pinPanel = new PinPanel();
        this.pinPanel.getGruen()
                     .setVisible(false);
        this.pinPanel.setName("EMAILDlg");
        this.jtp.setRightDecoration(this.pinPanel);
        this.setContentPane(jtp);

        this.setModal(true);
        this.setResizable(false);
        this.rtp = new RehaTPEventClass();
        this.rtp.addRehaTPEventListener(this);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        regleJList(0);
    }

    private void regleJList(int selection) {
        try {
            if (attachments.size() > 0) {
                Object[] result = new Object[attachments.size()];
                Map<Object, ImageIcon> icons = new HashMap<Object, ImageIcon>();
                for (int i = 0; i < attachments.size(); i++) {
                    icons.put(attachments.get(i)[1], findIcon(attachments.get(i)[1]));
                }
                for (int i = 0; i < attachments.size(); i++) {
                    result[i] = attachments.get(i)[1];
                }
                jList.setCellRenderer(new IconListRenderer(icons));
                jList.setListData(result);
                jList.setSelectedIndex(selection);
            } else {
                jList.setModel(new DefaultListModel());
                jList.repaint();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private ImageIcon findIcon(String datei) {
        ImageIcon reticon = null;
        String test = datei.toLowerCase();
        if (test.endsWith(".ics")) {
            reticon = SystemConfig.hmSysIcons.get("patnachrichten");
        } else if (test.endsWith(".pdf")) {
            reticon = new ImageIcon(SystemConfig.hmSysIcons.get("pdf")
                                                           .getImage()
                                                           .getScaledInstance(26, 26, Image.SCALE_SMOOTH));
        } else if (test.endsWith(".odt")) {
            reticon = new ImageIcon(SystemConfig.hmSysIcons.get("ooowriter")
                                                           .getImage()
                                                           .getScaledInstance(26, 26, Image.SCALE_SMOOTH));
        } else if (test.endsWith(".ods")) {
            reticon = new ImageIcon(SystemConfig.hmSysIcons.get("ooocalc")
                                                           .getImage()
                                                           .getScaledInstance(26, 26, Image.SCALE_SMOOTH));
        } else {
            reticon = SystemConfig.hmSysIcons.get("patnachrichten");
        }
        return reticon;
    }

    public void senden() {
        Reha.getThisFrame()
            .setCursor(Cursors.wartenCursor);
        this.setCursor(Cursors.wartenCursor);
        if (tf[0].getText()
                 .trim()
                 .equals("")
                || tf[0].getText()
                        .trim()
                        .startsWith(",")) {
            JOptionPane.showMessageDialog(null, "Keine Empfängeradresse angegeben");
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    tf[0].requestFocus();
                }
            });
            return;
        }
        if (tf[1].getText()
                 .trim()
                 .equals("")) {
            JOptionPane.showMessageDialog(null, "Keine Betreff angegeben");
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    tf[1].requestFocus();
                }
            });
            return;
        }
        HashMap<String, String> hmPostfach = null;
        if (postfach == 0) {
            hmPostfach = (HashMap<String, String>) SystemConfig.hmEmailExtern.clone();
        } else {
            hmPostfach = (HashMap<String, String>) SystemConfig.hmEmailIntern.clone();
        }
        String smtphost = hmPostfach.get("SmtpHost");
        String authent = hmPostfach.get("SmtpAuth");
        String benutzer = hmPostfach.get("Username");
        String pass1 = hmPostfach.get("Password");
        String sender = hmPostfach.get("SenderAdresse");
        String secure = hmPostfach.get("SmtpSecure");
        String useport = hmPostfach.get("SmtpPort");
        SystemConfig.hmIcalSettings.get("aufeigeneemail");
        hmPostfach.get("SenderAdresse");
        // String text = "Ihre Behandlungstermine befinden sich im Dateianhang";
        boolean authx = (authent.equals("0") ? false : true);
        boolean bestaetigen = false;

        EmailSendenExtern oMail = new EmailSendenExtern();
        try {
            /*
             * String mailtext = SystemConfig.hmAdrPDaten.get("<Pbanrede>")+
             * ",\nwie gewünscht senden wir Ihnen hiermit Ihre Reha-Termine im RTA\n\nMit freundlichen Grüßen\nIhr Planungsteam im RTA "
             * ;
             */
            oMail.sendMail(smtphost, benutzer, pass1, sender, tf[0].getText(), tf[1].getText(), jta.getText(),
                    attachments, authx, bestaetigen, secure, useport);
            oMail = null;
        } catch (Exception e) {
            e.printStackTrace();
            Reha.getThisFrame()
                .setCursor(Cursors.normalCursor);
            if (abrDlg != null) {
                abrDlg.setVisible(false);
                abrDlg.dispose();
                abrDlg = null;
            }
            JOptionPane.showMessageDialog(null,
                    "Emailversand fehlgeschlagen\n\n" + "Mögliche Ursachen:\n"
                            + "- falsche Angaben zu Ihrem Emailpostfach und/oder dem Provider\n"
                            + "- Sie haben keinen Kontakt zum Internet" + "\n\nFehlertext:" + e.getLocalizedMessage());
        }
        if (!(Boolean) SystemConfig.hmIcalSettings.get("direktsenden")) {
            if (abrDlg != null) {
                abrDlg.setVisible(false);
                abrDlg.dispose();
                abrDlg = null;
            }
            Reha.getThisFrame()
                .setCursor(Cursors.normalCursor);
            int frage = JOptionPane.showConfirmDialog(null,
                    "Versand erfolgreich abgeschlossen.\n\nWollen Sie den Email-Dialog jetzt schließen?",
                    "Wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
            if (frage == JOptionPane.YES_OPTION) {
                FensterSchliessen("dieses");
            }
        } else {
            FensterSchliessen("dieses");
            Reha.getThisFrame()
                .setCursor(Cursors.normalCursor);
        }
    }

    private JXPanel getContent() {
        // 1 2 3 4 5 6 7 8 9
        String xwerte = "15dlu,40dlu,2dlu,fill:0:grow(0.6),10dlu,40dlu,2dlu,fill:0:grow(0.4),15dlu";
        // 1 2 3 4 5 6 7
        // String xwerte =
        // "5dlu,fill:0:grow(0.5),125dlu,5dlu,120dlu,fill:0:grow(0.5),5dlu";
        // 1 2 3 4 5 6 7 8 9 10
        String ywerte = "15dlu,p,5dlu,p,20dlu,p,2dlu,p,fill:0:grow(1.0),p,0dlu";
        FormLayout lay = new FormLayout(xwerte, ywerte);
        CellConstraints cc = new CellConstraints();
        content = new JXPanel();
        content.setBackground(Color.LIGHT_GRAY);
        content.setLayout(lay);
        content.add(new JLabel("Empfänger:"), cc.xy(2, 2));
        tf[0] = new JRtaTextField("normal", false);
        tf[0].setText(recipients);
        content.add(tf[0], cc.xy(4, 2));
        content.add(new JLabel("Absender:"), cc.xy(6, 2));
        cmb = new JRtaComboBox(new String[] { SystemConfig.hmEmailExtern.get("Username"),
                SystemConfig.hmEmailIntern.get("Username") });
        cmb.setSelectedIndex(this.postfach);
        content.add(cmb, cc.xy(8, 2));
        content.add(new JLabel("Betreff:"), cc.xy(2, 4));
        tf[1] = new JRtaTextField("normal", false);
        tf[1].setText(betreff);
        content.add(tf[1], cc.xyw(4, 4, 5));

        content.add(new JLabel("Emailtext:"), cc.xy(2, 6));
        jta = new JTextArea();
        jta.setFont(new Font("Courier", Font.PLAIN, 14));
        jta.setLineWrap(true);
        jta.setName("emailtext");
        jta.setWrapStyleWord(true);
        jta.setEditable(true);
        jta.setBackground(Color.WHITE);
        jta.setForeground(Color.BLUE);
        jta.setText(mailtext);
        jta.setCaretPosition(0);
        JScrollPane span = JCompTools.getTransparentScrollPane(jta);
        span.validate();
        content.add(span, cc.xywh(2, 8, 5, 2));

        content.add(new JLabel("Dateianhang:"), cc.xy(8, 6));
        jList = new JList();
        jList.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1 && e.getClickCount() == 2) {
                    starteAnhang(jList.getSelectedIndex());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }

        });

        // jList.setListData(new String[]{"Datei 1","Datei 2"});
        span = JCompTools.getTransparentScrollPane(jList);
        span.validate();
        content.add(span, cc.xywh(8, 8, 1, 2));

        JXPanel buttons = new JXPanel();
        buttons.setOpaque(false);
        // buttons.setBackground(Color.WHITE);
        FormLayout blay = new FormLayout("65dlu,40dlu,65dlu", "10dlu,fill:0:grow(0.5),p,fill:0:grow(0.5),10dlu");
        CellConstraints bcc = new CellConstraints();
        buttons.setLayout(blay);

        buts[0] = new JButton("senden");
        buts[0].setActionCommand("senden");
        buts[0].addActionListener(al);
        // buts[0].setIcon(SystemConfig.hmSysIcons.get("email"));
        buts[0].setMnemonic(KeyEvent.VK_S);
        buts[1] = new JButton("abbrechen");
        buts[1].setActionCommand("abbrechen");
        buts[1].addActionListener(al);
        buts[1].setMnemonic(KeyEvent.VK_A);
        buttons.add(buts[0], bcc.xy(1, 3));
        buttons.add(buts[1], bcc.xy(3, 3, CellConstraints.DEFAULT, CellConstraints.FILL));
        buttons.validate();

        content.add(buttons, cc.xyw(2, 10, 5, CellConstraints.LEFT, CellConstraints.TOP));

        JXPanel attaches = new JXPanel();
        attaches.setOpaque(false);
        // attaches.setBackground(Color.WHITE);
        FormLayout alay = new FormLayout("p", "10dlu,fill:0:grow(0.5),p,fill:0:grow(0.5),10dlu");
        CellConstraints acc = new CellConstraints();
        attaches.setLayout(alay);

        JToolBar jtb = new JToolBar();
        jtb.setOpaque(false);
        jtb.setRollover(true);
        jtb.setBorder(null);
        jtb.setOpaque(false);
        JButton[] aktrbut = { null, null };
        aktrbut[0] = new JButton();
        aktrbut[0].setIcon(SystemConfig.hmSysIcons.get("neu"));
        aktrbut[0].setToolTipText("Neuen Dateianhang aufnehmen");
        aktrbut[0].setActionCommand("attachneu");
        aktrbut[0].addActionListener(al);
        jtb.add(aktrbut[0]);

        aktrbut[1] = new JButton();
        aktrbut[1].setIcon(SystemConfig.hmSysIcons.get("delete"));
        aktrbut[1].setToolTipText("Dateianhang entfernen");
        aktrbut[1].setActionCommand("attachdelete");
        aktrbut[1].addActionListener(al);
        jtb.add(aktrbut[1]);
        attaches.add(jtb, acc.xy(1, 3));
        attaches.validate();
        content.add(attaches, cc.xy(8, 10, CellConstraints.LEFT, CellConstraints.TOP));

        content.validate();
        return content;

    }

    public EmailDialog getInstance() {
        return this;
    }

    private void installListener() {
        al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String cmd = arg0.getActionCommand();
                if (cmd.equals("senden")) {
                    /*
                     * new SwingWorker<Void,Void>(){
                     *
                     * @Override protected Void doInBackground() throws Exception { abrDlg = new
                     * AbrechnungDlg(); abrDlg.pack(); abrDlg.setLocationRelativeTo(getInstance());
                     * abrDlg.setzeLabel("starte Aufbereitung Termin-Email");
                     * abrDlg.setVisible(true); return null; } }.execute();
                     */
                    senden();
                } else if (cmd.equals("abbrechen")) {
                    FensterSchliessen("dieses");
                    return;
                } else if (cmd.equals("attachneu")) {
                    // JOptionPane.showMessageDialog(null, "Funktion noch nicht aktiv");
                    regleAttachNeu();
                } else if (cmd.equals("attachdelete")) {
                    // JOptionPane.showMessageDialog(null, "Funktion noch nicht aktiv");
                    regleAttachDelete();
                }

            }

        };
    }

    private void regleAttachDelete() {
        try {
            int selected = jList.getSelectedIndex();
            if (selected >= 0) {
                attachments.remove(selected);
                attachments.trimToSize();
                if (attachments.size() > 0) {
                    regleJList(attachments.size() - 1);
                } else {
                    regleJList(-1);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void regleAttachNeu() {
        String[] sret = { null, null };
        final JFileChooser chooser = new JFileChooser("Dateianhang wählen");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        final File file = new File(Path.Instance.getProghome());
        chooser.setCurrentDirectory(file);
        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName()
                     .equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
                        || e.getPropertyName()
                            .equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
                    e.getNewValue();
                }
            }
        });
        chooser.setVisible(true);
        setCursor(Cursors.normalCursor);
        final int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File inputVerzFile = chooser.getSelectedFile();
            if (inputVerzFile.getName()
                             .trim()
                             .equals("")) {
                // sret = "";
            } else {
                sret[1] = inputVerzFile.getName()
                                       .trim();
                sret[0] = inputVerzFile.getPath();
                attachments.add(sret.clone());
                regleJList(attachments.size() - 1);
            }
        } else {
            // sret = "";
        }
        chooser.setVisible(false);
    }

    private void FensterSchliessen(String welches) {

        this.jtp.removeMouseListener(this.mymouse);
        this.jtp.removeMouseMotionListener(this.mymouse);
        this.mymouse = null;

        if (this.rtp != null) {
            this.rtp.removeRehaTPEventListener(this);
            this.rtp = null;
        }

        setVisible(false);
        this.dispose();
    }

    @Override
    public void rehaTPEventOccurred(RehaTPEvent evt) {
        FensterSchliessen("dieses");
    }


    private void starteAnhang(int wahl) {
        try {
            String test = attachments.get(wahl)[1].toLowerCase();
            // System.out.println(attachments.get(wahl)[0].replace("\\", "/"));
            if (test.endsWith(".ics")) {
                Runtime.getRuntime()
                       .exec("C:/Windows/notepad.exe " + attachments.get(wahl)[0].replace("\\", "/"));
            } else if (test.endsWith(".pdf")) {
                final String xdatei = attachments.get(wahl)[0].replace("\\", "/");
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        Process process = new ProcessBuilder(SystemConfig.hmFremdProgs.get("AcrobatReader"), "",
                                xdatei).start();
                        InputStream is = process.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(isr);
                        // String line;
                        while ((br.readLine()) != null) {
                            // System.out.println(line);
                        }
                        is.close();
                        isr.close();
                        br.close();

                        return null;
                    }

                }.execute();
            } else if (test.endsWith(".odt") || test.endsWith(".ott")) {
                new OOTools().starteWriterMitDatei(attachments.get(wahl)[0].replace("\\", "/"));
            } else if (test.endsWith(".ods") || test.endsWith(".ots")) {
                new OOTools().starteCalcMitDatei(attachments.get(wahl)[0].replace("\\", "/"));
            } else {
                Runtime.getRuntime()
                       .exec("C:/Windows/notepad.exe " + attachments.get(wahl)[0].replace("\\", "/"));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
