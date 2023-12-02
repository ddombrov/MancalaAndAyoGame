package mancala;

/**
 * Exception thrown when a player is not found in the game.
 */
public class NoSuchPlayerException extends Exception {
    private static final long serialVersionUID = 4;
    public NoSuchPlayerException(final String message) {
        super(message);
    }
}