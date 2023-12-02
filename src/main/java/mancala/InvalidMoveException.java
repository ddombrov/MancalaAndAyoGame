package mancala;

/**
 * Exception thrown when an invalid move is attempted in the game.
 */
public class InvalidMoveException extends Exception {
    public InvalidMoveException(final String message) {
        super(message);
    }
}