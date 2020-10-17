package rehaHMK;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;

import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;

import CommonTools.Colors;

public class CompoundPainters {

    public static final CompoundPainter<MattePainter> CP = CompoundPainters.createCompoundPainter(new Point2D.Float(),
            new Point2D.Float(150.0F, 800.0F), new float[] { 0.0F, 0.75F },
            new Color[] { Color.WHITE, Color.LIGHT_GRAY });
    public static final CompoundPainter<MattePainter> CP_ARZT = CompoundPainters.createCompoundPainter(
            new Point2D.Float(), new Point2D.Float(0.0F, 400.0F), new float[] { 0.0F, 0.75F },
            new Color[] { Color.WHITE, Colors.TaskPaneBlau.alpha(0.45F) });
    public static final CompoundPainter<MattePainter> CP_SCANNER = CompoundPainters.createCompoundPainter(
            new Point2D.Float(), new Point2D.Float(0.0F, 40.0F), new float[] { 0F, 1F },
            new Color[] { Colors.PiOrange.alpha(0.5F), Color.WHITE });

    public static CompoundPainter<MattePainter> createCompoundPainter(Point2D startPoint, Point2D endPoint,
            float[] dists, Color[] colors) {
        Point2D startcp = startPoint;
        Point2D endcp = endPoint;
        float[] distcp = dists;
        Color[] colorscp = colors;
        LinearGradientPaint p = new LinearGradientPaint(startcp, endcp, distcp, colorscp);
        MattePainter mp = new MattePainter(p);
        return new CompoundPainter<>(new Painter[] { (Painter) mp });
    }

}
