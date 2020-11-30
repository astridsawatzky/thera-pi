package terminKalender;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import CommonTools.ZeitFunk;
import hauptFenster.Reha;
import systemEinstellungen.SystemConfig;
import systemEinstellungen.TKSettings;
import terminKalender.TerminFenster.Ansicht;

class KalenderPanel extends JXPanel {
    private static final Font DIALOGFONT = new Font("Dialog", Font.PLAIN, 12);
    private static final long serialVersionUID = 7354087866079956906L;
    private List<List<String>> dat = new ArrayList<>();
    private int anzahl;
    private int vectorzahl;
    private int i;
    private int zeitSpanneVon;
    private int zeitSpanneBis;
    private int minutenInsgesamt;
    private float fPixelProMinute;
    private int iMaxHoehe;
    private int yDifferenz;
    private int xStart;
    private int xEnde;
    private int baseline;
    private boolean spalteAktiv;
    private int blockAktiv;
    private int panelNummer;
    private int maleSchwarz = -1;
    private int[] aktivPunkt = { 0, 0, 0, 0 };
    private boolean shiftGedrueckt;
    private int[] gruppe = { -1, -1 };
    private int[] rahmen = { -1, -1, -1, -1 };
    private boolean inGruppierung;
    private int[] positionScreen = { -1, -1, -1, -1 };
    private Font fon = new Font("Tahoma", Font.PLAIN, 10);
    private ImageIcon dragImage = SystemConfig.hmSysIcons.get("buttongruen");

    private Image dragImage2 = SystemConfig.hmSysIcons.get("buttongruen")
      .getImage()
      .getScaledInstance(8, 8, Image.SCALE_SMOOTH);
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private float yTimeLine;
    private boolean showTimeLine;
    private int pfeily;
    private Logger logger = LoggerFactory.getLogger(KalenderPanel.class);

    void setPanelNumber(int aktPanel) {
        this.panelNummer = aktPanel;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        vectorzahl = dat.size();

        if (vectorzahl > 0) {
            int yStartMin;
            float fStartPix;

            int yEndeMin;
            float fEndePix;

            float fDifferenz;
            int i1;
            g2d.setFont( DIALOGFONT);

            g2d.setColor(TKSettings.KalenderHintergrund);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            for (i = 0; i < getAnzahl(); i++) {
                try {
                    if (dat.get(0).get(i) == null) {
                        g2d.setColor(TKSettings.KalenderHintergrund);
                        g2d.fillRect(0, 0, getWidth(), getHeight());
                        break;
                    }
                } catch (ArrayIndexOutOfBoundsException bounds) {
                    g2d.setColor(TKSettings.KalenderHintergrund);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    break;
                }
                String sName = dat.get(0).get(i);
                if (sName == null) {
                    sName = "";
                }
                String sReznr  =  dat.get(1).get(i);
                if (sReznr  == null) {
                    sReznr = "";
                }
                String sStart= dat.get(2).get(i);
                int dauer = Integer.parseInt(dat.get(3).get(i));
                yStartMin = (int) ZeitFunk.MinutenSeitMitternacht(sStart) - zeitSpanneVon;

                fStartPix = yStartMin * fPixelProMinute;
                fEndePix = fStartPix + dauer * fPixelProMinute;
                yStartMin = (int) fStartPix;
                yEndeMin = (int) fEndePix;
                yDifferenz = yEndeMin - yStartMin;
                fDifferenz = fEndePix - fStartPix;
                for (i1 = 0; i1 < 1; i1++) {
                    if (yDifferenz <= 8) {
                        g2d.setFont(g2d.getFont()
                                       .deriveFont(8.5f));
                        baseline = (int) (fEndePix - ((fDifferenz - 8.5) / 2 + 1.0));
                        break;
                    }

                    g2d.setFont(g2d.getFont()
                                   .deriveFont(11.5f));
                    baseline = (int) (fEndePix - ((fDifferenz - 11.5) / 2 + 1.0));
                }

                farbgedingse(g2d, sName, sReznr, sStart, dauer, yStartMin);
            }
            if (getAnzahl() == 0) {
                g2d.setColor(TKSettings.KalenderHintergrund);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        } else {
            g2d.setColor(TKSettings.KalenderHintergrund);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        if (this.inGruppierung) {
            g2d.setColor(Color.BLACK);
            Composite original = g2d.getComposite();
            AlphaComposite ac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
            g2d.setComposite(ac1);
            g2d.fillRect(rahmen[0], rahmen[1], rahmen[2], rahmen[3]);
            g2d.setComposite(original);
        }
        if (showTimeLine) {
            yTimeLine = (ZeitFunk.MinutenSeitMitternacht(sdf.format(new Date())) - zeitSpanneVon) * fPixelProMinute;
            pfeily = Math.round(yTimeLine);

            int xCoord[] = { 1, 1, 6, 1 }; // die x-Koordinaten
            int yCoord[] = { pfeily - 5, pfeily + 5, pfeily, pfeily - 5 }; // die y-Koordinaten
            int anz = xCoord.length;
            g.setColor(Color.red);
            g.fillPolygon(xCoord, yCoord, anz);
            g.setColor(Color.black);
            g.drawPolygon(xCoord, yCoord, anz);
        }
    }

    private void farbgedingse(Graphics2D g2d, String sName, String sReznr, String sStart, int dauer, int yStartMin) {
        if (this.maleSchwarz >= 0 && this.maleSchwarz == i) {
            Font altfont = g2d.getFont();
            g2d.setFont(fon);
            g2d.setColor(SystemConfig.aktTkCol.get("aktBlock")[0]);
            g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
            g2d.setColor(SystemConfig.aktTkCol.get("aktBlock")[1]);
            aktivPunkt[0] = xStart;
            aktivPunkt[1] = yStartMin;
            aktivPunkt[2] = xEnde;
            aktivPunkt[3] = yDifferenz;
            if (sReznr.contains("@FREI")) {
                Reha.instance.terminpanel.dragLab[this.panelNummer].setText("");

                g2d.drawString(sName, 5, baseline);

                g2d.draw3DRect(xStart, yStartMin, xEnde - 3, yDifferenz - 1, true);
            } else if (this.spalteAktiv) {
                if (!"".equals(sName) || Reha.instance.terminpanel.aktAnsicht == Ansicht.MASKE) {
                    if (yDifferenz < 12) {
                        if (yDifferenz > 0) {
                            Reha.instance.terminpanel.dragLab[this.panelNummer].setBounds(xStart + 1,
                                    yStartMin, xStart + 13, yDifferenz - 1);
                            g2d.drawImage(this.dragImage.getImage(), xStart + 1,
                                    yStartMin + yDifferenz / 2 - this.dragImage.getIconHeight() / 2,
                                    null);
                        }
                    } else {
                        Reha.instance.terminpanel.dragLab[this.panelNummer].setBounds(xStart + 1,
                                yStartMin, xStart + 13, yDifferenz - 1);
                        g2d.drawImage(this.dragImage.getImage(), xStart + 1,
                                yStartMin + yDifferenz / 2 - this.dragImage.getIconHeight() / 2,
                                null);
                    }
                    g2d.drawString(sStart.substring(0, 5) + "-" + sName, xStart + 16, baseline);
                    g2d.draw3DRect(xStart, yStartMin, xEnde - 3, yDifferenz - 1, true);
                } else {
                    g2d.drawString(sStart.substring(0, 5) + "-" + sName, 5, baseline);

                    g2d.draw3DRect(xStart, yStartMin, xEnde - 3, yDifferenz - 1, true);
                    Reha.instance.terminpanel.dragLab[this.panelNummer].setIcon(null);
                    Reha.instance.terminpanel.dragLab[this.panelNummer].setText("");
                }
            } else {
                g2d.drawString(sStart.substring(0, 5) + "-" + sName, 5, baseline);

                g2d.draw3DRect(xStart, yStartMin, xEnde - 3, yDifferenz - 1, true);
            }
            g2d.setFont(altfont);
            return;
        }

        if (this.blockAktiv >= 0 && this.blockAktiv == i && this.spalteAktiv) {
            g2d.setColor(Color.GRAY);
            g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
            g2d.setColor(Color.WHITE);
            if (sReznr.contains("@FREI")) {
                g2d.drawString(sName, 5, baseline);
            } else {
                g2d.drawString(sStart.substring(0, 5) + "-" + sName, 5, baseline);
            }
            return;
        }
        if (sReznr.trim()
                   .isEmpty()
                && sName.trim()
                         .isEmpty()) {
            g2d.setColor(SystemConfig.aktTkCol.get("Freitermin")[0]);
            g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
            g2d.setColor(SystemConfig.aktTkCol.get("Freitermin")[1]);
            g2d.drawString(sStart.substring(0, 5), 5, baseline);
            return;
        }
        if (sReznr.contains("@FREI")) {
            g2d.setColor(SystemConfig.aktTkCol.get("AusserAZ")[0]);
            g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
            g2d.setColor(SystemConfig.aktTkCol.get("AusserAZ")[1]);
            g2d.drawString(sName, 5, baseline);

            return;
        }
        if (sReznr.contains("@INTERN") && sName.contains("-RTA-")) {
            g2d.setColor(SystemConfig.aktTkCol.get("unvollst")[0]);
            g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
            g2d.setColor(SystemConfig.aktTkCol.get("unvollst")[1]);
            g2d.drawString(sStart.substring(0, 5) + "-" + sName, 5, baseline);
            return;
        }
        if (sReznr.trim()
                   .length() <= 2
                && !sName.trim()
                          .isEmpty()) {
            if (sReznr.trim()
                      .startsWith("RH")) {
                g2d.setColor(SystemConfig.aktTkCol.get("Rehapat")[0]);
                g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
                g2d.setColor(SystemConfig.aktTkCol.get("Rehapat")[1]);
                g2d.drawString(sStart.substring(0, 5) + "-" + sName, 5, baseline);
            } else {
                g2d.setColor(SystemConfig.aktTkCol.get("unvollst")[0]);
                g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
                g2d.setColor(SystemConfig.aktTkCol.get("unvollst")[1]);
                g2d.drawString(sStart.substring(0, 5) + "-" + sName, 5, baseline);
                return;
            }
        }

        if (sReznr.length() <= 2 && sName.isEmpty()) {
            if (sReznr.trim()
                      .startsWith("RH")) {
                g2d.setColor(SystemConfig.aktTkCol.get("Rehapat")[0]);
                g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
                g2d.setColor(SystemConfig.aktTkCol.get("Rehapat")[1]);
            } else {
                g2d.setColor(SystemConfig.aktTkCol.get("Freitermin")[0]);
                g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
                g2d.setColor(SystemConfig.aktTkCol.get("unvollst")[1]);
            }
            g2d.drawString(sStart.substring(0, 5) + "-" + sName, 5, baseline);
            return;
        }
        if (sReznr.contains("\\")) {
            String letter = extractColLetter(sReznr);
            Color[] colors = SystemConfig.aktTkCol.get("Col" + letter);
            if (colors != null) {
                g2d.setColor(colors[0]);
                g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
                g2d.setColor(colors[1]);
                g2d.drawString(sStart.substring(0, 5) + "-" + sName, 5, baseline);
                return;
            }
        }
        // Sonderprogramm für Rehatermine
        if (sReznr.contains("RH")) {
            g2d.setColor(SystemConfig.aktTkCol.get("Rehapat")[0]);
            g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
            g2d.setColor(SystemConfig.aktTkCol.get("Rehapat")[1]);
            g2d.drawString(sStart.substring(0, 5) + "-" + sName, 5, baseline);
            return;
        }
        // Sonderprogramm für Rehatermine
        if (dauer == 15) {
            g2d.setColor(SystemConfig.aktTkCol.get("15min")[0]);
            g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
            g2d.setColor(SystemConfig.aktTkCol.get("15min")[1]);
            g2d.drawString(sStart.substring(0, 5) + "-" + sName, 5, baseline);
            return;
        }
        if (dauer == 20) {
            g2d.setColor(SystemConfig.aktTkCol.get("20min")[0]);
            g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
            g2d.setColor(SystemConfig.aktTkCol.get("20min")[1]);
            g2d.drawString(sStart.substring(0, 5) + "-" + sName, 5, baseline);
            return;
        }
        if (dauer == 25) {
            g2d.setColor(SystemConfig.aktTkCol.get("25min")[0]);
            g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
            g2d.setColor(SystemConfig.aktTkCol.get("25min")[1]);
            g2d.drawString(sStart.substring(0, 5) + "-" + sName, 5, baseline);
            return;
        }
        if (dauer == 30) {
            g2d.setColor(SystemConfig.aktTkCol.get("30min")[0]);
            g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
            g2d.setColor(SystemConfig.aktTkCol.get("30min")[1]);
            g2d.drawString(sStart.substring(0, 5) + "-" + sName, 5, baseline);
            return;
        }
        if (dauer == 40) {
            g2d.setColor(SystemConfig.aktTkCol.get("40min")[0]);
            g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
            g2d.setColor(SystemConfig.aktTkCol.get("40min")[1]);
            g2d.drawString(sStart.substring(0, 5) + "-" + sName, 5, baseline);
            return;
        }
        if (dauer == 45) {
            g2d.setColor(SystemConfig.aktTkCol.get("45min")[0]);
            g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
            g2d.setColor(SystemConfig.aktTkCol.get("45min")[1]);
            g2d.drawString(sStart.substring(0, 5) + "-" + sName, 5, baseline);
            return;
        }
        if (dauer == 50) {
            g2d.setColor(SystemConfig.aktTkCol.get("50min")[0]);
            g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
            g2d.setColor(SystemConfig.aktTkCol.get("50min")[1]);
            g2d.drawString(sStart.substring(0, 5) + "-" + sName, 5, baseline);
            return;
        }
        if (dauer == 60) {
            g2d.setColor(SystemConfig.aktTkCol.get("60min")[0]);
            g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
            g2d.setColor(SystemConfig.aktTkCol.get("60min")[1]);
            g2d.drawString(sStart.substring(0, 5) + "-" + sName, 5, baseline);
            return;
        }
        if (dauer == 90) {
            g2d.setColor(SystemConfig.aktTkCol.get("90min")[0]);
            g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
            g2d.setColor(SystemConfig.aktTkCol.get("90min")[1]);
            g2d.drawString(sStart.substring(0, 5) + "-" + sName, 5, baseline);
            return;
        }
        g2d.setColor(SystemConfig.aktTkCol.get("unbekmin")[0]);
        g2d.fillRect(xStart, yStartMin, xEnde, yDifferenz);
        g2d.setColor(SystemConfig.aktTkCol.get("unbekmin")[1]);
        g2d.drawString(sStart.substring(0, 5) + "-" + sName, 5, baseline);
    }

    String extractColLetter(String sReznr) {
        try {
            return sReznr.substring(sReznr.indexOf('\\') + 1, sReznr.indexOf('\\') + 2);
        } catch (Exception e) {
            logger.error("bad things happen here", e);
            return "";
        }
    }

    public void setShowTimeLine(boolean show) {
        this.showTimeLine = show;
    }

    void datenZeichnen(List<ArrayList<Vector<String>>> vTerm, int therapeut, ArrayList<Vector<String>> therapeutTag) {
        if (vTerm.isEmpty() || therapeut < 0) {
            setAnzahl(0);
        } else {
            dat.clear();
            dat.add(therapeutTag.get(0));
            dat.add(therapeutTag.get(1));
            dat.add(therapeutTag.get(2));
            dat.add(therapeutTag.get(3));
            dat.add(therapeutTag.get(4));
            dat.add(therapeutTag.get(5));

            setAnzahl(dat.get(0).size());
        }
        repaint();
    }

    void zeitSpanne() {
        iMaxHoehe = getSize().height;
        fPixelProMinute = (float) iMaxHoehe / minutenInsgesamt;
        xStart = 2;
        xEnde = getSize().width;
        Point posInScreen = getLocationOnScreen();
        positionScreen[0] = posInScreen.x;
        positionScreen[1] = posInScreen.y;
        positionScreen[2] = posInScreen.x + getWidth();
        positionScreen[3] = posInScreen.y + getHeight();
        repaint();
    }

    public int[] getPosInScreen() {
        return positionScreen;
    }

    public float getFloatPixelProMinute() {
        return this.fPixelProMinute;
    }

    void zeitInit(int von, int bis) {
        zeitSpanneVon = von;
        setZeitSpanneBis(bis);
        minutenInsgesamt = bis - von;
    }

    public int[] BlockTest(int x, int y, int[] spdaten) {
        int[] ret = spdaten;
        vectorzahl =  dat.size();
        if (vectorzahl > 0) {
            String sStart = ""; // Startzeit
            int dauer; // Termin Dauer
            int yStartMin;
            float fStartPix;
            float fEndePix;
            this.blockAktiv = -1;
            this.spalteAktiv = false;
            for (i = 0; i < getAnzahl(); i++) {
                sStart =  dat.get(2).get(i);
                dauer = Integer.parseInt( dat.get(3).get(i));

                yStartMin = (int) ZeitFunk.MinutenSeitMitternacht(sStart) - zeitSpanneVon;
                fStartPix = yStartMin * fPixelProMinute;
                fEndePix = fStartPix + dauer * fPixelProMinute;
                if (y >= fStartPix && y <= fEndePix) {
                    this.blockAktiv = i;
                    this.spalteAktiv = true;
                    ret[3] = ret[2];
                    ret[2] = panelNummer;
                    ret[1] = i;
                    ret[0] = i;
                    break;
                }
            }
        }
        return ret.clone();
    }

    int[] BlockTestOhneAktivierung(int x, int y) {
        int[] ret = { -1, -1, -1, -1 };
        vectorzahl =  dat.size();
        if (vectorzahl > 0) {
            String sStart = ""; // Startzeit
            int dauer; // Termin Dauer
            int yStartMin;
            float fStartPix;
            float fEndePix;
            for (i = 0; i < getAnzahl(); i++) {
                sStart =  dat.get(2).get(i);
                dauer = Integer.parseInt( dat.get(3).get(i));

                yStartMin = (int) ZeitFunk.MinutenSeitMitternacht(sStart) - zeitSpanneVon;
                fStartPix = yStartMin * fPixelProMinute;
                fEndePix = fStartPix + dauer * fPixelProMinute;
                if (y >= fStartPix && y <= fEndePix) {
                    ret[3] = ret[2];
                    ret[2] = panelNummer;
                    ret[1] = i;
                    ret[0] = i;
                    break;
                }
            }
        }
        return ret.clone();
    }

    int blockGeklickt(int block) {
        if (block > -1 && getAnzahl() > 0) {
            this.maleSchwarz = block;
            this.spalteAktiv = true;
            repaint();
        } else {
            this.maleSchwarz = -1;
            this.spalteAktiv = false;
            this.blockAktiv = -1;
            repaint();
            aktivPunkt[0] = -1;
            aktivPunkt[1] = -1;
            aktivPunkt[2] = -1;
            aktivPunkt[3] = -1;
        }
        return this.maleSchwarz;
    }

    void spalteDeaktivieren() {
        this.maleSchwarz = -1;
        this.spalteAktiv = false;
        this.blockAktiv = -1;
        repaint();
        aktivPunkt[0] = -1;
        aktivPunkt[1] = -1;
        aktivPunkt[2] = -1;
        aktivPunkt[3] = -1;
        Reha.instance.terminpanel.dragLab[this.panelNummer].setText("");
        Reha.instance.terminpanel.dragLab[this.panelNummer].setIcon(null);
    }

    public void setSpalteaktiv(boolean aktiv) {
        this.spalteAktiv = aktiv;
    }

    void schwarzAbgleich(int block, int schwarz) {
        this.blockAktiv = block;
        this.maleSchwarz = schwarz;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint();
            }
        });
    }

    public int[] getPosition() {
        return aktivPunkt;
    }

    void shiftGedrueckt(boolean sg) {
        setShiftGedrueckt(sg);
    }

    void gruppierungZeichnen(int[] gruppe) {
        String sStart; // Startzeit
        String sEnde;
        int yStartMin;
        int dauer; // Termin Dauer
        int yEndeMin;
        float fStartPix;
        int block1, block2;
        block1 = gruppe[0];
        block2 = gruppe[1];
        if (!this.inGruppierung) {
            this.inGruppierung = true;
        }
        if (block1 > block2) {
            this.gruppe[0] = block2;
            this.gruppe[1] = block1;
        } else {
            this.gruppe[0] = block1;
            this.gruppe[1] = block2;
        }

        sStart = dat.get(2).get(this.gruppe[0]);
        yStartMin = (int) ZeitFunk.MinutenSeitMitternacht(sStart) - zeitSpanneVon;
        sEnde = dat.get(4).get(this.gruppe[1]);
        dauer = (int) ZeitFunk.ZeitDifferenzInMinuten(sStart, sEnde);

        fStartPix = yStartMin * fPixelProMinute;
        yStartMin = (int) fStartPix;

        yEndeMin = (int) (dauer * fPixelProMinute);
        rahmen[0] = 0;
        rahmen[1] = yStartMin;
        rahmen[2] = getWidth();
        rahmen[3] = yEndeMin;
        repaint();
    }

    public void setInGruppierung(boolean gruppierung) {
        this.inGruppierung = gruppierung;
    }

    public float getPixels() {
        return this.fPixelProMinute;
    }

    public void setZeitSpanneBis(int zeitSpanneBis) {
        this.zeitSpanneBis = zeitSpanneBis;
    }

    public int getZeitSpanneBis() {
        return zeitSpanneBis;
    }

    public void setShiftGedrueckt(boolean shiftGedrueckt) {
        this.shiftGedrueckt = shiftGedrueckt;
    }

    public boolean isShiftGedrueckt() {
        return shiftGedrueckt;
    }

    public void setDragImage2(Image dragImage2) {
        this.dragImage2 = dragImage2;
    }

    public Image getDragImage2() {
        return dragImage2;
    }

    public int getAnzahl() {
        return anzahl;
    }

    public void setAnzahl(int anzahl) {
        this.anzahl = anzahl;
        repaint();
    }

}
