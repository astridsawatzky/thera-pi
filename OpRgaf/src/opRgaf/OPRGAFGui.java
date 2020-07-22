package opRgaf;

import javax.swing.event.TableModelEvent;

interface OPRGAFGui {

    void tableChanged(TableModelEvent arg0);

    void setzeFocus();

    void sucheRezept(String rezept);


}
