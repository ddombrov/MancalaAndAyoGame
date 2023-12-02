package mancala;

/**
 * Exception thrown when an invalid move is attempted in the game.
 */
public class InvalidMoveException extends Exception {
    private static final long serialVersionUID = 3;
    public InvalidMoveException(final String message) {
        super(message);
    }
}