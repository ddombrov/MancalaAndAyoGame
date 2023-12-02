package mancala;

/**
 * Exception thrown when a pit is not found in the game.
 */
public class PitNotFoundException extends Exception {
    public PitNotFoundException(String message) {
        super(message);
    }
}