package BuildIniTable;

import CommonTools.INIFile;
import CommonTools.SqlInfo;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class BuildIniTable implements WindowListener {
  public static BuildIniTable thisClass;
  
  SqlInfo sqlInfo = null;
  
  JFrame jFrame = null;
  
  Vector<String> mandantIkvec = new Vector<String>();
  
  Vector<String> mandantNamevec = new Vector<String>();
  
  public Connection conn;
  
  public boolean DbOk;
  
  public String[] inis = new String[] { 
      "preisgruppen.ini", "terminkalender.ini", "gruppen.ini", "icons.ini", "fristen.ini", "color.ini", 
      "dta301.ini", "gutachten.ini", "ktraeger.ini", "sqlmodul.ini", 
      "thbericht.ini" };
  
  public String pfadzurmandini;
  
  public String pfadzurini;
  
  public int anzahlmandanten;
  
  public static void main(String[] args) {
    BuildIniTable application = new BuildIniTable();
    (application.getInstance()).sqlInfo = new SqlInfo();
    application.getInstance().getJFrame();
  }
  
  private BuildIniTable getInstance() {
    return this;
  }
  
  public JFrame getJFrame() {
    try {
      UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    } 
    int frage = JOptionPane.showConfirmDialog(null, "Haben Sie von Ihren Datenbanken eine Sicherungskopie erstellt?", "Achtung wichtige Benutzeranfrage", 0);
    if (frage != 0)
      System.exit(0); 
    thisClass = this;
    thisClass.pfadzurmandini = thisClass.mandantTesten();
    thisClass.pfadzurini = thisClass.pfadzurmandini.replace("mandanten.ini", "");
    INIFile ini = new INIFile(thisClass.pfadzurmandini);
    thisClass.anzahlmandanten = Integer.parseInt(ini.getStringProperty("TheraPiMandanten", "AnzahlMandanten"));
    for (int i = 0; i < thisClass.anzahlmandanten; i++) {
      this.mandantIkvec.add(ini.getStringProperty("TheraPiMandanten", "MAND-IK" + Integer.toString(i + 1)));
      this.mandantNamevec.add(ini.getStringProperty("TheraPiMandanten", "MAND-NAME" + Integer.toString(i + 1)));
    } 
    this.jFrame = new JFrame();
    this.sqlInfo.setFrame(this.jFrame);
    this.jFrame.addWindowListener(this);
    this.jFrame.setSize(600, 600);
    this.jFrame.setPreferredSize(new Dimension(600, 600));
    this.jFrame.setTitle("Thera-Pi  INI-Tabelle(n) erzeugen");
    this.jFrame.setDefaultCloseOperation(3);
    this.jFrame.setLocationRelativeTo((Component)null);
    ProcessPanel pan = new ProcessPanel();
    this.jFrame.getContentPane().setLayout(new BorderLayout());
    this.jFrame.getContentPane().add((Component)pan, "Center");
    this.jFrame.setVisible(true);
    return this.jFrame;
  }
  
  private String mandantTesten() {
    String mandini = System.getProperty("user.dir");
    mandini = String.valueOf(mandini.replace("\\", "/")) + "/ini/mandanten.ini";
    System.out.println(mandini);
    if (!mandIniExist(mandini)) {
      JOptionPane.showMessageDialog(null, "Das System kann die mandanten.ini nicht finden!\nBitte navigieren Sie in das Verzeichnis in dem sich die\nmandanten.ini befindet und wählen Sie die mandanten.ini aus!");
      String sret = dateiDialog(mandini);
      if (!sret.endsWith("/ini/mandanten.ini")) {
        JOptionPane.showMessageDialog(null, "Sie haben die falsche(!!!) Datei ausgewählt, das Programm wird beendet!");
        System.exit(0);
      } 
      return sret;
    } 
    return mandini;
  }
  
  private boolean mandIniExist(String abspath) {
    File f = new File(abspath);
    return f.exists();
  }
  
  private String dateiDialog(String pfad) {
    String sret = "";
    JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
    chooser.setDialogType(0);
    chooser.setFileSelectionMode(2);
    File file = new File(pfad);
    chooser.setCurrentDirectory(file);
    chooser.addPropertyChangeListener(new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals("SelectedFileChangedProperty") || 
              e.getPropertyName().equals("directoryChanged"))
              File file = (File)e.getNewValue(); 
          }
        });
    chooser.setVisible(true);
    int result = chooser.showOpenDialog((Component)null);
    if (result == 0) {
      File inputVerzFile = chooser.getSelectedFile();
      String inputVerzStr = inputVerzFile.getPath();
      if (inputVerzFile.getName().trim().equals("")) {
        sret = "";
      } else {
        sret = inputVerzStr.trim().replace("\\", "/");
      } 
    } else {
      sret = "";
    } 
    chooser.setVisible(false);
    return sret;
  }
  
  public Connection starteDB(String hostAndDb, String user, String pw) throws Exception {
    String sDB = "SQL";
    if (this.conn != null)
      try {
        this.conn.close();
      } catch (SQLException sQLException) {} 
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
    } catch (InstantiationException e) {
      System.out.println("SQLTreiberfehler: " + e.getMessage());
      this.DbOk = false;
      return null;
    } catch (IllegalAccessException e) {
      System.out.println("SQLTreiberfehler: " + e.getMessage());
      this.DbOk = false;
      return null;
    } catch (ClassNotFoundException e) {
      System.out.println("SQLTreiberfehler: " + e.getMessage());
      this.DbOk = false;
      return null;
    } 
    try {
      this.conn = DriverManager.getConnection(hostAndDb, user, pw);
      this.sqlInfo.setConnection(this.conn);
      this.DbOk = true;
      System.out.println("Datenbankkontakt hergestellt");
    } catch (SQLException ex) {
      this.DbOk = false;
      return null;
    } 
    return this.conn;
  }
  
  public void windowOpened(WindowEvent e) {}
  
  public void windowClosing(WindowEvent e) {
    if (this.conn != null)
      try {
        this.conn.close();
        System.out.println("Connection geschlossen");
      } catch (SQLException e1) {
        e1.printStackTrace();
      }  
  }
  
  public void windowClosed(WindowEvent e) {}
  
  public void windowIconified(WindowEvent e) {}
  
  public void windowDeiconified(WindowEvent e) {}
  
  public void windowActivated(WindowEvent e) {}
  
  public void windowDeactivated(WindowEvent e) {}
}
