package CommonTools;

import java.awt.event.MouseEvent;

import javax.swing.JLabel;

public class JRtaLabel extends JLabel {
    /**
     * 
     */
    private static final long serialVersionUID = 1554916556438578627L;
    private String toolTipText = null;

    public JRtaLabel(String starttext) {
        super();
        this.setText(starttext);
        this.setToolTipText(" ");
    }

    public JRtaLabel() {
        super();
        this.setToolTipText(" ");
    }

    

    @Override
    public String getToolTipText(MouseEvent event) {
        String result;
        if (getAlternateText() != null) {
            result = getAlternateText();
        } else {
            result = super.getToolTipText(event);
        }
        this.revalidate();
        return result;
    }

    public String getAlternateText() {
        return this.toolTipText;
    }

    public void setAlternateText(String text) {
        toolTipText = String.valueOf(text);
    }
}
