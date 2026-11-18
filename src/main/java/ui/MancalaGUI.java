package ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Window for playing Mancala. It shows either the home page or a game.
 */
public class MancalaGUI extends JFrame {
    private final HomePanel home = new HomePanel(this::showGame);

    /**
     * Builds the window and opens the home page.
     */
    public MancalaGUI() {
        this(false);
    }

    /**
     * Builds the window and opens the home page.
     *
     * @param browser true to fill the whole display without a title bar, for running in a web page
     */
    public MancalaGUI(boolean browser) {
        super("Mancala");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (browser) {
            setUndecorated(true);
            setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
        } else {
            setSize(1180, 700);
            setMinimumSize(new Dimension(860, 540));
            setLocationRelativeTo(null);
        }
        showHome();
    }

    private void showHome() {
        show(home);
    }

    private void showGame(boolean kalah, String player1, String player2) {
        show(new BoardPanel(kalah, player1, player2, this::showHome));
    }

    private void show(Container page) {
        setContentPane(page);
        revalidate();
        repaint();
    }

    /**
     * Launches the GUI.
     *
     * @param args pass --browser when running inside a web page
     */
    public static void main(String[] args) {
        boolean browser = Arrays.asList(args).contains("--browser");
        SwingUtilities.invokeLater(() -> {
            new MancalaGUI(browser).setVisible(true);
            if (browser) {
                // Runs after the first paint, so the web page can drop its loading screen.
                SwingUtilities.invokeLater(() -> {
                    try {
                        gameShown();
                    } catch (UnsatisfiedLinkError e) {
                        // The page did not provide the hook; nothing to tell it.
                    }
                });
            }
        });
    }

    /** Implemented in JavaScript by the web page that hosts the game (CheerpJ natives). */
    private static native void gameShown();
}
