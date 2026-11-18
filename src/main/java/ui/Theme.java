package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Colours, fonts and drawing helpers shared by the home page, the setup dialog and the board.
 */
final class Theme {
    static final String FONT = "Segoe UI";
    static final Color GOLD = new Color(255, 205, 110);
    static final Color CREAM = new Color(250, 236, 210);
    static final Color MUTED = new Color(250, 236, 210, 140);
    static final Color AMBER = new Color(251, 191, 36);
    static final Color CARD = new Color(46, 34, 40);
    static final Color FIELD = new Color(28, 20, 26);
    static final Color BORDER = new Color(255, 205, 110, 70);
    static final Color DARK_TEXT = new Color(40, 26, 16);
    static final Color[] STONE_COLORS = {
        new Color(52, 168, 158), new Color(236, 170, 58), new Color(206, 66, 84),
        new Color(74, 118, 214), new Color(118, 184, 86), new Color(228, 222, 206),
        new Color(158, 98, 204),
    };

    private Theme() {
    }

    static Font font(int style, float size) {
        return new Font(FONT, style, 1).deriveFont(size);
    }

    static void smooth(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    static void paintBackground(Graphics2D g, int w, int h) {
        g.setPaint(new GradientPaint(0, 0, new Color(34, 28, 38), 0, h, new Color(20, 16, 22)));
        g.fillRect(0, 0, w, h);
        g.setPaint(new RadialGradientPaint(new Point2D.Double(w / 2.0, h * 0.55), (float) Math.max(1, w * 0.6),
                new float[] {0f, 1f}, new Color[] {new Color(120, 70, 40, 60), new Color(0, 0, 0, 0)}));
        g.fillRect(0, 0, w, h);
    }

    static void drawStone(Graphics2D g, double x, double y, double r, Color c, double shadow) {
        g.setColor(new Color(0, 0, 0, (int) (110 * shadow)));
        g.fill(new Ellipse2D.Double(x - r * 0.85, y - r * 0.3, r * 2, r * 1.6));
        g.setPaint(new RadialGradientPaint(new Point2D.Double(x - r * 0.35, y - r * 0.4), (float) (r * 1.5),
                new float[] {0f, 0.5f, 1f}, new Color[] {mix(c, Color.WHITE, 0.5), c, mix(c, Color.BLACK, 0.55)}));
        g.fill(circle(x, y, r));
        g.setColor(new Color(255, 255, 255, 170));
        g.fill(new Ellipse2D.Double(x - r * 0.55, y - r * 0.65, r * 0.55, r * 0.35));
    }

    /**
     * Paints a pill-shaped button: gold when primary, outlined otherwise.
     */
    static void paintButton(Graphics2D g, RoundRectangle2D shape, String text, boolean primary, boolean hover,
            boolean enabled, double fade) {
        double a = enabled ? fade : fade * 0.45;
        if (primary) {
            g.setColor(alpha(hover && enabled ? mix(GOLD, Color.WHITE, 0.25) : GOLD, a));
            g.fill(shape);
            g.setColor(alpha(DARK_TEXT, a));
        } else {
            g.setColor(alpha(new Color(255, 255, 255), (hover && enabled ? 0.12 : 0.04) * a));
            g.fill(shape);
            g.setStroke(new BasicStroke(1.3f));
            g.setColor(alpha(hover && enabled ? GOLD : BORDER, a));
            g.draw(shape);
            g.setColor(alpha(CREAM, a));
        }
        g.setFont(font(Font.BOLD, 15));
        drawCentered(g, text, shape.getCenterX(), shape.getCenterY());
    }

    static Ellipse2D.Double circle(double x, double y, double r) {
        return new Ellipse2D.Double(x - r, y - r, r * 2, r * 2);
    }

    static Color mix(Color a, Color b, double t) {
        return new Color((int) (a.getRed() + (b.getRed() - a.getRed()) * t),
                (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }

    static Color alpha(Color c, double a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(),
                (int) Math.max(0, Math.min(255, c.getAlpha() * a)));
    }

    static void drawCentered(Graphics2D g, String text, double x, double y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (float) (x - fm.stringWidth(text) / 2.0),
                (float) (y + (fm.getAscent() - fm.getDescent()) / 2.0));
    }
}
