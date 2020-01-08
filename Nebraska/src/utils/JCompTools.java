package utils;

import java.awt.Component;

import javax.swing.JScrollPane;

public class JCompTools {

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
