package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AbstractDocument;

/**
 * Small custom-painted components used by the home page and the setup dialog.
 */
final class Widgets {
    static final int NAME_LIMIT = 20;

    private Widgets() {
    }

    /** A pill-shaped button, gold when primary and outlined otherwise. */
    static final class PillButton extends JButton {
        private final boolean primary;

        PillButton(String text, boolean primary) {
            super(text);
            this.primary = primary;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFont(Theme.font(Font.BOLD, 15));
            setPreferredSize(new Dimension(getFontMetrics(getFont()).stringWidth(text) + 48, 44));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            Theme.smooth(g);
            RoundRectangle2D.Double shape = new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2,
                    getHeight() - 2, getHeight() - 2);
            Theme.paintButton(g, shape, getText(), primary, getModel().isRollover(), isEnabled(), 1);
            if (isFocusOwner() && isEnabled()) {
                g.setColor(Theme.alpha(Theme.GOLD, 0.6));
                g.setStroke(new BasicStroke(2f));
                g.draw(shape);
            }
            g.dispose();
        }
    }

    /** A large card on the home page that starts a kind of game. */
    static final class ModeCard extends JButton {
        private final String title;
        private final Color[] stones;

        ModeCard(String title, Color... stones) {
            this.title = title;
            this.stones = stones;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(300, 100));
            getAccessibleContext().setAccessibleName(title);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            Theme.smooth(g);
            boolean hover = getModel().isRollover() || isFocusOwner();
            RoundRectangle2D.Double shape = new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 22, 22);
            g.setColor(hover ? Theme.mix(Theme.CARD, Theme.GOLD, 0.08) : Theme.CARD);
            g.fill(shape);
            g.setStroke(new BasicStroke(1.4f));
            g.setColor(hover ? Theme.GOLD : Theme.BORDER);
            g.draw(shape);

            // A small cluster of stones as the card's picture.
            double cx = 54;
            double cy = getHeight() / 2.0;
            g.setColor(new Color(0, 0, 0, 70));
            g.fill(Theme.circle(cx, cy, 30));
            double[][] spots = {{-9, -7}, {9, -6}, {0, 9}};
            for (int i = 0; i < stones.length && i < spots.length; i++) {
                Theme.drawStone(g, cx + spots[i][0], cy + spots[i][1], 10, stones[i], 0.6);
            }

            g.setColor(Theme.CREAM);
            g.setFont(Theme.font(Font.BOLD, 21));
            g.drawString(title, 100, (float) (cy + 7));
            g.dispose();
        }
    }

    /** A text box for a player's name, with a placeholder and a length limit. */
    static final class NameField extends JTextField {
        private final String placeholder;

        NameField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(11, 16, 11, 16));
            setFont(Theme.font(Font.BOLD, 18));
            setForeground(Theme.CREAM);
            setCaretColor(Theme.GOLD);
            setSelectionColor(Theme.alpha(Theme.GOLD, 0.45));
            setSelectedTextColor(Theme.CREAM);
            ((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                        throws BadLocationException {
                    String incoming = text == null ? "" : text;
                    int room = NAME_LIMIT - (fb.getDocument().getLength() - length);
                    super.replace(fb, offset, length, incoming.substring(0, Math.max(0, Math.min(room,
                            incoming.length()))), attrs);
                }

                @Override
                public void insertString(FilterBypass fb, int offset, String text, AttributeSet attrs)
                        throws BadLocationException {
                    replace(fb, offset, 0, text, attrs);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            Theme.smooth(g);
            RoundRectangle2D.Double shape = new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 14, 14);
            g.setColor(Theme.FIELD);
            g.fill(shape);
            g.setStroke(new BasicStroke(1.4f));
            g.setColor(isFocusOwner() ? Theme.GOLD : Theme.BORDER);
            g.draw(shape);
            if (getText().isEmpty()) {
                g.setColor(Theme.alpha(Theme.CREAM, 0.3));
                g.setFont(getFont());
                g.drawString(placeholder, getInsets().left,
                        (getHeight() + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent()) / 2);
            }
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    /** A row of stones used as decoration above the title. */
    static final class StoneRow extends JComponent {
        StoneRow() {
            setPreferredSize(new Dimension(5 * 56, 56));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            Theme.smooth(g);
            int[] colors = {2, 5, 0, 3, 1};
            double start = (getWidth() - 4 * 56) / 2.0;
            for (int i = 0; i < colors.length; i++) {
                Theme.drawStone(g, start + i * 56, getHeight() / 2.0, 20, Theme.STONE_COLORS[colors[i]], 0.8);
            }
            g.dispose();
        }
    }
}
