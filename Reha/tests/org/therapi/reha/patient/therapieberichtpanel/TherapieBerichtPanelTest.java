package org.therapi.reha.patient.therapieberichtpanel;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.therapi.reha.patient.Berichte.BerHist;
import org.therapi.reha.patient.Berichte.BerHistDto;

import mandant.IK;

public class TherapieBerichtPanelTest {

    public static void main(String[] args) {
        IK testIK = new IK("123456789");
        List<BerHist> berichte = new BerHistDto(testIK).therapieBerichtByPatIntern(350);

        JFrame frame = new JFrame("titel");

        TherapieBerichtPanel therapieBerichtPanel = new TherapieBerichtPanel();
        JMenuBar jMenubar = new JMenuBar();
        JMenu menu = new JMenu("mmimi");
        menu.add(new JMenuItem(therapieBerichtPanel.nunicht));
        jMenubar.add(menu);
        jMenubar.add(new JMenuItem(therapieBerichtPanel.nuaber));

        frame.getContentPane()
             .add(therapieBerichtPanel.getPanel());
        frame.setJMenuBar(jMenubar);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        therapieBerichtPanel.setData(berichte);

    }

}
