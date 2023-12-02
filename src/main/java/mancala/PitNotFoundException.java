package mancala;

/**
 * Exception thrown when a pit is not found in the game.
 */
public class PitNotFoundException extends Exception {
    private static final long serialVersionUID = 5;
    public PitNotFoundException(final String message) {
        super(message);
    }
}