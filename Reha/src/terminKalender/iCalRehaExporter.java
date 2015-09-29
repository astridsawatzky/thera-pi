package terminKalender;

import hauptFenster.Reha;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import systemEinstellungen.SystemConfig;

public class iCalRehaExporter {
	boolean fehler = false;
	String plandatei = "";
	Vector<String> veczeilen = new Vector<String>();
	public iCalRehaExporter(){
		if( (plandatei=oeffneDateiDialog()).equals("")){
			JOptionPane.showMessageDialog(null,"Es wurde keine Plandatei ausgewählt");
		}else{
			 if( (veczeilen = plandateiEinlesen()).size() <= 0 ){
				 JOptionPane.showMessageDialog(null,"Die Datei "+plandatei+" ist keine gültige Rehaplan-Datei");
			 }else{
				 
			 }
		}
	}
	
	private Vector<String> plandateiEinlesen(){
		Vector<String> vecz = new Vector<String>();
		/*
		System.out.println(plandatei);
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(plandatei));
			String zeile = null;
			while ((zeile = in.readLine()) != null) {
				System.out.println("Gelesene Zeile: " + zeile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null){
				try {
					in.close();
				} catch (IOException e) {
				}
			}	
		} 
		*/
		return (Vector<String>)vecz.clone();
	}
	
	private String oeffneDateiDialog(){
		String sret = "";
		final JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        final File file = new File((String)SystemConfig.hmIcalSettings.get("rehaplanverzeichnis"));

        chooser.setCurrentDirectory(file);

        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
                        || e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
                    final File f = (File) e.getNewValue();
                }
            }
        });
        chooser.setVisible(true);
        Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
        final int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File inputVerzFile = chooser.getSelectedFile();
            //String inputVerzStr = inputVerzFile.getPath();

            if(inputVerzFile.getName().trim().equals("")){
            	sret = "";
            }else{
            	sret = inputVerzFile.getName().trim();	
            }
        }else{
        	sret = ""; //vorlagenname.setText(SystemConfig.oTerminListe.NameTemplate);
        }
        chooser.setVisible(false); 
		
		
		return sret;
	}

}
