package dialoge;

import java.awt.Container;

import org.jdesktop.swingx.JXTitledPanel;

import events.RehaTPEvent;
import events.RehaTPEventListener;

interface ISmartDialog extends RehaTPEventListener {



    public JXTitledPanel getSmartTitledPanel();

    public JXTitledPanel getTitledPanel();

    public void setContentPanel(Container cont);

    public void aktiviereIcon();

    public void deaktiviereIcon();

    public void setPinPanel(PinPanel pinPanel);

    public PinPanel getPinPanel();

    public void setIgnoreReturn(boolean ignore);

    public void ListenerSchliessen();

    @Override
    public void rehaTPEventOccurred(RehaTPEvent evt);

}
