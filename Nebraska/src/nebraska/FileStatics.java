package nebraska;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;

import javax.swing.JFileChooser;

public class FileStatics {
    public static String dirChooser(String pfad, String titel) {
        // String pfad = "C:/Lost+Found/verschluesselung/";
        String sret = "";
        final JFileChooser chooser = new JFileChooser(pfad);
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setDialogTitle(titel);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        final File file = new File(pfad);

        chooser.setCurrentDirectory(file);

        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName()
                     .equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
                        || e.getPropertyName()
                            .equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
                    // final File f = (File) e.getNewValue();
                }
            }
        });
        chooser.setVisible(true);
        final int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File inputVerzFile = chooser.getSelectedFile();
            if (inputVerzFile.getName()
                             .trim()
                             .equals("")) {
                sret = "";
            } else {
                sret = inputVerzFile.getAbsolutePath()
                                    .trim();
            }
        } else {
            sret = "";
        }
        return sret;
    }

}
