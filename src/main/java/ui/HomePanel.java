package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * The home page: a title and one card per rule set. Picking a card asks for the players' names.
 */
final class HomePanel extends JPanel {

    /** Called once both players have chosen their names. */
    interface StartListener {
        void start(boolean kalah, String player1, String player2);
    }

    private boolean dimmed;

    HomePanel(StartListener onStart) {
        super(new GridBagLayout());

        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));

        column.add(centered(new Widgets.StoneRow()));
        column.add(Box.createVerticalStrut(18));
        JLabel title = new JLabel("Mancala");
        title.setFont(Theme.font(Font.BOLD, 60));
        title.setForeground(Theme.CREAM);
        column.add(centered(title));
        column.add(Box.createVerticalStrut(36));

        JPanel cards = new JPanel(new GridLayout(1, 2, 16, 16));
        cards.setOpaque(false);
        Widgets.ModeCard kalah = new Widgets.ModeCard("Play Kalah",
                Theme.STONE_COLORS[1], Theme.STONE_COLORS[0], Theme.STONE_COLORS[2]);
        Widgets.ModeCard ayo = new Widgets.ModeCard("Play Ayo",
                Theme.STONE_COLORS[3], Theme.STONE_COLORS[4], Theme.STONE_COLORS[6]);
        kalah.addActionListener(e -> choose(onStart, true, "Play Kalah"));
        ayo.addActionListener(e -> choose(onStart, false, "Play Ayo"));
        cards.add(kalah);
        cards.add(ayo);
        cards.setMaximumSize(cards.getPreferredSize());
        column.add(centered(cards));

        add(column);
    }

    private void choose(StartListener onStart, boolean kalah, String title) {
        dimmed = true;
        repaint();
        String[] names = SetupDialog.ask(SwingUtilities.getWindowAncestor(this), title);
        dimmed = false;
        repaint();
        if (names != null) {
            onStart.start(kalah, names[0], names[1]);
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        Theme.paintBackground(g, getWidth(), getHeight());
        g.dispose();
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        if (dimmed) {
            graphics.setColor(new Color(10, 6, 12, 170));
            graphics.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private static <T extends JComponent> T centered(T component) {
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        return component;
    }
}
