package terminKalender;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import systemEinstellungen.TKSettings;
import systemTools.ListenerTools;

class TherapeutenTag  {

  private KalenderPanel day = new KalenderPanel();

      private static final Logger logger = LoggerFactory.getLogger(TherapeutenTag.class);

      public TherapeutenTag(String name, boolean b, float kalenderAlpha) {
         day.setName(name);
         day.setDoubleBuffered(true);
         day.setAlpha(TKSettings.KalenderAlpha);
    }

    void ListenerSetzen(int aktPanel) {
        day.setPanelNumber(aktPanel);
      }

      public void paintComponent(Graphics g) {
         day.paintComponent(g);
      }

      String extractColLetter(String sReznr) {

          return   day.extractColLetter(sReznr);
      }

      public void setShowTimeLine(boolean show) {
         day.setShowTimeLine(show);
      }

      void datenZeichnen( List<ArrayList<Vector<String>>> vTerm, int therapeut) {
          if(vTerm.size() > 0 && therapeut >= 0) {
            day.datenZeichnen(vTerm, therapeut,  vTerm.get(therapeut));
        } else {
              day.setAnzahl(0);
          }


      }

      /**********************************/
      void zeitSpanne() {
         day.zeitSpanne();
      }

      public int[] getPosInScreen() {
          return day.getPosInScreen();
      }

      public float getFloatPixelProMinute() {
          return day.getFloatPixelProMinute();
      }

      void zeitInit(int von, int bis) {
        day.zeitInit(von, bis);
      }

      public int[] BlockTest(int x, int y, int[] spdaten) {
          return day.BlockTest(x, y, spdaten);
      }
      /********************************/
      int[] BlockTestOhneAktivierung(int x, int y) {
        return day.BlockTestOhneAktivierung(x, y);
      }

      /********************************/
      int blockGeklickt(int block) {
          return day.blockGeklickt(block);
      }

      void spalteDeaktivieren() {
         day.spalteDeaktivieren();
      }

      public void setSpalteaktiv(boolean aktiv) {
         day.setSpalteaktiv(aktiv);
      }

      void schwarzAbgleich(int block, int schwarz) {
          day.schwarzAbgleich(block, schwarz);
      }

      public int[] getPosition() {
          return day.getPosition();
      }

      void shiftGedrueckt(boolean sg) {
         day.setShiftGedrueckt(sg);
      }

      void gruppierungZeichnen(int[] gruppe) {
         day.gruppierungZeichnen(gruppe);
      }

      public void setInGruppierung(boolean gruppierung) {
         day.setInGruppierung(gruppierung);

      }

      public float getPixels() {
        return day.getPixels();
      }

      public void setZeitSpanneBis(int zeitSpanneBis) {
         day.setZeitSpanneBis(zeitSpanneBis);
      }

      public int getZeitSpanneBis() {
         return day.getZeitSpanneBis();
      }



      public void setShiftGedrueckt(boolean shiftGedrueckt) {
         day.setShiftGedrueckt(shiftGedrueckt);
      }

      public boolean isShiftGedrueckt() {
         return day.isShiftGedrueckt();
      }

      public void setDragImage2(Image dragImage2) {
         day.setDragImage2(dragImage2);
      }

      public Image getDragImage2() {
          return day.getDragImage2();
      }

//outside calls on jcomponent

    public void requestFocus() {
       day.requestFocus();

    }

    public void removeListeners() {
        ListenerTools.removeListeners(getDay());

    }

    public int getWidth() {
        return day.getWidth();
    }

    public int getHeight() {

        return day.getHeight();
    }

    public void repaint() {
      day.repaint();

    }

    public void add(JLabel jLabel) {
      day.add(jLabel);

    }

    public KalenderPanel getDay() {
        return day;
    }

    public void setTransparenz(float alf) {
        day.setAlpha(alf);
        day.setBackground(TKSettings.KalenderHintergrund);
        day.repaint();

    }

    public void addKeyListener(KeyListener listener) {
       day.addKeyListener(listener);

    }

    public void addMouseListener(MouseListener listener) {
        day.addMouseListener(listener);

    }

    public void requestFocus(boolean  temporary) {
        //TODO: discouraged usage
        day.requestFocus(temporary);

    }

    public Point getLocationOnScreen() {
        return day.getLocationOnScreen();
    }

    public void addMouseMotionListener(MouseMotionListener listener) {
        day.addMouseMotionListener(listener);

    }

    public void addFocusListener(FocusListener listener) {
        day.addFocusListener(listener);

    }
  }
