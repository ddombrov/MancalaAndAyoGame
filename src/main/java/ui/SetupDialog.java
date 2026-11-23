package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Asks for both players' names before a game starts. Two players may not share a name.
 */
final class SetupDialog extends JDialog {
    private final Widgets.NameField name1 = new Widgets.NameField("Player 1's name");
    private final Widgets.NameField name2 = new Widgets.NameField("Player 2's name");
    private final JLabel error = new JLabel(" ");
    private final Widgets.PillButton start = new Widgets.PillButton("Start game", true);
    private String[] result;

    private SetupDialog(Window owner, String title) {
        super(owner, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        // Rounded corners need a see-through window; where that is unsupported the corners stay solid.
        boolean translucent = owner != null && owner.getGraphicsConfiguration().getDevice()
                .isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSLUCENT);
        if (translucent) {
            setBackground(new Color(0, 0, 0, 0));
        }

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                Theme.smooth(g);
                if (isOpaque()) {
                    g.setColor(Theme.CARD);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                RoundRectangle2D.Double shape = new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2,
                        28, 28);
                g.setColor(Theme.CARD);
                g.fill(shape);
                g.setStroke(new BasicStroke(1.5f));
                g.setColor(Theme.BORDER);
                g.draw(shape);
                g.dispose();
            }
        };
        card.setOpaque(!translucent);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(28, 30, 26, 30));

        JLabel heading = new JLabel(title);
        heading.setFont(Theme.font(Font.BOLD, 26));
        heading.setForeground(Theme.CREAM);
        card.add(left(heading));
        card.add(Box.createVerticalStrut(22));
        card.add(field("PLAYER 1", name1));
        card.add(Box.createVerticalStrut(16));
        card.add(field("PLAYER 2", name2));
        card.add(Box.createVerticalStrut(10));

        error.setFont(Theme.font(Font.PLAIN, 14));
        error.setForeground(Theme.AMBER);
        error.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        card.add(left(error));
        card.add(Box.createVerticalStrut(14));

        Widgets.PillButton cancel = new Widgets.PillButton("Cancel", false);
        cancel.addActionListener(e -> dispose());
        start.addActionListener(e -> submit());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        footer.add(cancel);
        footer.add(start);
        card.add(left(footer));

        DocumentListener edited = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }
        };
        name1.getDocument().addDocumentListener(edited);
        name2.getDocument().addDocumentListener(edited);
        name1.addActionListener(e -> name2.requestFocusInWindow());
        name2.addActionListener(e -> submit());
        card.registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        setContentPane(card);
        changed();
        setSize(new Dimension(460, card.getPreferredSize().height));
        setLocationRelativeTo(owner);
    }

    /**
     * Shows the dialog and waits for it to close.
     *
     * @return the two cleaned names, or null if the players cancelled
     */
    static String[] ask(Window owner, String title) {
        SetupDialog dialog = new SetupDialog(owner, title);
        dialog.setVisible(true);
        return dialog.result;
    }

    /** Collapses runs of whitespace and trims a player name. */
    static String cleanName(String name) {
        String cleaned = name.replaceAll("\\s+", " ").trim();
        return cleaned.length() > Widgets.NAME_LIMIT ? cleaned.substring(0, Widgets.NAME_LIMIT) : cleaned;
    }

    /** Names that differ only in capital letters count as the same name. */
    static boolean sameName(String a, String b) {
        return a.equalsIgnoreCase(b);
    }

    private void changed() {
        error.setText(" ");
        start.setEnabled(!cleanName(name1.getText()).isEmpty() && !cleanName(name2.getText()).isEmpty());
    }

    private void submit() {
        String first = cleanName(name1.getText());
        String second = cleanName(name2.getText());
        if (first.isEmpty() || second.isEmpty()) {
            error.setText("Choose a name for each player.");
            (first.isEmpty() ? name1 : name2).requestFocusInWindow();
            return;
        }
        if (sameName(first, second)) {
            error.setText("That name is taken, pick another.");
            name2.requestFocusInWindow();
            name2.selectAll();
            return;
        }
        result = new String[] {first, second};
        dispose();
    }

    private static JComponent field(String label, JComponent input) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel caption = new JLabel(label);
        caption.setFont(Theme.font(Font.BOLD, 11));
        caption.setForeground(Theme.MUTED);
        caption.setLabelFor(input);
        panel.add(left(caption));
        panel.add(Box.createVerticalStrut(7));
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.add(left(input));
        return left(panel);
    }

    private static <T extends JComponent> T left(T component) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        return component;
    }
}
