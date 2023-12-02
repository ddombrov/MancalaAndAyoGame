package mancala;

/**
 * Exception thrown when a player is not found in the game.
 */
public class NoSuchPlayerException extends Exception {
    public NoSuchPlayerException(final String message) {
        super(message);
    }
}