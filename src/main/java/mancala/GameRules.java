package mancala;
import java.io.Serializable;

/**
 * Abstract class representing the rules of a Mancala game.
 * KalahRules and AyoRules will subclass this class.
 */
public abstract class GameRules implements Serializable {
    final private MancalaDataStructure gameBoard;
    private static final long serialVersionUID = 1000;
    private int currentPlayer = 1; // Player number (1 or 2)

    /**
     * Constructor to initialize the game board.
     */
    public GameRules() {
        gameBoard = new MancalaDataStructure();
    }

    /**
     * Get the number of stones in a pit.
     *
     * @param pitNum The number of the pit.
     * @return The number of stones in the pit.
     */
    public int getNumStones(final int pitNum) {
        return gameBoard.getNumStones(pitNum);
    }

    /**
     * Get the game data structure.
     *
     * @return The MancalaDataStructure.
     */
    public MancalaDataStructure getDataStructure() {
        return gameBoard;
    }

    /**
     * Check if a side (player's pits) is empty.
     *
     * @param pitNum The number of a pit in the side.
     * @return True if the side is empty, false otherwise.
     */
    boolean isSideEmpty(final int pitNum) {
        // This method can be implemented in the abstract class.

        // start by assuming sides are both empty
         /* default */ boolean isSide1Empty = true;
         /* default */ boolean isSide2Empty = true;

        for (int i = 1; i <= 6; i++) {

            // if player 1 has pits then his side's not empty
            if (gameBoard.getNumStones(i) > 0) {
                isSide1Empty = false;
            }

            // if player 2 has pits then his side's not empty
            if (gameBoard.getNumStones(i + 6) > 0) {
                isSide2Empty = false;
            }
        }

        // return a boolean on side emptiness based on pitNum
        return pitNum < 7 ? isSide1Empty : isSide2Empty;
    }

    /**
     * Set the current player.
     *
     * @param playerNum The player number (1 or 2).
     */
    public void setPlayer(final int playerNum) {
        currentPlayer = playerNum;
    }

    /**
     * Perform a move and return the number of stones added to the player's store.
     *
     * @param startPit  The starting pit for the move.
     * @param playerNum The player making the move.
     * @return The number of stones added to the player's store.
     * @throws InvalidMoveException If the move is invalid.
     */
    public abstract int moveStones(int startPit, int playerNum) throws InvalidMoveException;

    /**
     * Distribute stones from a pit and return the number distributed.
     *
     * @param startPit The starting pit for distribution.
     * @return The number of stones distributed.
     */
    abstract int distributeStones(int startPit);

    /**
     * Capture stones from the opponent's pit and return the number captured.
     *
     * @param stoppingPoint The stopping point for capturing stones.
     * @return The number of stones captured.
     */
    abstract int captureStones(int stoppingPoint);

    /**
     * Register two players and set their stores on the board.
     *
     * @param one The first player.
     * @param two The second player.
     */
    public void registerPlayers(final Player one, final Player two) {
        // this method can be implemented in the abstract class.

        /*
         * make a new store in this method, set the owner
         * then use the setStore(store,playerNum) method of the data structure
         */
        final Store store1 = new Store();
        store1.setOwner(one);
        final Store store2 = new Store();
        store2.setOwner(two);

        getDataStructure().setStore(store1, 1);
        getDataStructure().setStore(store2, 2);
    }

    /**
     * Reset the game board by setting up pits and emptying stores.
     */
    public void resetBoard() {
        gameBoard.setUpPits();
        gameBoard.emptyStores();
    }

    @Override
    public String toString() {
        // Implement toString() method logic here.

        final StringBuilder strBldr = new StringBuilder();

        strBldr.append("\n");

        // go through the pits
        for (int i = 1; i <= 6; i++) {

            // add to the string "Pit i: i's stones"
            strBldr.append("Pit").append(i).append(": ").append(getDataStructure().getNumStones(i))
                    .append("\t\t");

        }

        // add to the string player 1 and 2's store counts
        strBldr.append("\n");
        strBldr.append("Player1 Store: ").append(getDataStructure().getStoreCount(1));
        strBldr.append("\t\t\t\t\t\t");
        strBldr.append("Player2 Store: ").append(getDataStructure().getStoreCount(2)).append("\n");

        // go through the pits
        for (int i = 7; i <= 12; i++) {


                strBldr.append("Pit").append(i).append(": ").append(getDataStructure().getNumStones(i))
                        .append("\t\t");
            

        }

        // return the string
        return strBldr.toString();
    }

}
