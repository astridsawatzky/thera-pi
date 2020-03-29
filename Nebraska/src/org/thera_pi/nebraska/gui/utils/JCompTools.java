package org.thera_pi.nebraska.gui.utils;

import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class JCompTools {

    public static JScrollPane getTransparentScrollPane(JPanel jpan) {
        JScrollPane jscr = new JScrollPane();
        jscr.setOpaque(false);
        jscr.getViewport()
            .setOpaque(false);
        jscr.setBorder(null);
        jscr.setViewportBorder(null);
        jscr.setViewportView(jpan);
        jscr.validate();
        return jscr;
    }

    public static JScrollPane getTransparentScrollPane(Component jpan) {
        JScrollPane jscr = new JScrollPane();
        jscr.setOpaque(false);
        jscr.getViewport()
            .setOpaque(false);
        jscr.setBorder(null);
        jscr.setViewportBorder(null);
        jscr.setViewportView(jpan);
        jscr.validate();
        return jscr;
    }

}
