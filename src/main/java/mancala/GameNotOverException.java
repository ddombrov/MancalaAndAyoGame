package mancala;

/**
 * Exception thrown when a game is not over, but an operation that requires the
 * game to be over is attempted.
 */
public class GameNotOverException extends Exception {
    public GameNotOverException(String message) {
        super(message);
    }
}