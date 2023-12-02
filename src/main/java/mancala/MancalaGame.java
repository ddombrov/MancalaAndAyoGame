package mancala;

import java.util.ArrayList;
//import java.io.Serializable;

//public class MancalaGame implements Serializable {
public class MancalaGame {

    // instance vars
    // private Board board;
    private ArrayList<Player> players;
    private Player currentPlayer;
    private GameRules game;

    /**
     * Default constructor for the MancalaGame class.
     * Initializes the board and an empty list of players.
     */
    public MancalaGame() {
        // this.board = new Board();
        this.players = new ArrayList<>();
        this.game = new KalahRules();
    }

    public MancalaGame(int kOrA) {

        this.players = new ArrayList<>();
        if (kOrA == 0) {
            this.game = new KalahRules();
        } else {
            this.game = new AyoRules();
        }
    }

    /**
     * Sets the players for the game
     * 
     * @param onePlayer the first player
     * @param twoPlayer the second player
     */
    public void setPlayers(Player onePlayer, Player twoPlayer) {

        // add to players
        this.players.add(onePlayer);
        this.players.add(twoPlayer);

        // p1 will start
        setCurrentPlayer(onePlayer);

        // register players with the board
        // this.board.registerPlayers(onePlayer, twoPlayer);
        game.registerPlayers(onePlayer, twoPlayer);

    }

    /**
     * Gets the list of players in the game.
     * 
     * @return an ArrayList of Player objects
     */
    public ArrayList<Player> getPlayers() {
        return this.players;
    }

    /**
     * Gets the current player.
     * 
     * @return the current Player object
     */
    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    /**
     * Sets the current player.
     * 
     * @param player the Player object to set as the current player
     */
    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    /**
     * Sets the board for the game.
     * 
     * @param theBoard the Board object to set for the game
     */
    // public void setBoard(Board theBoard) {
    // this.board = theBoard;
    //

    public void setBoard(GameRules theBoard) {
        this.game = theBoard;
    }

    /**
     * Gets the board for the game.
     * 
     * @return the Board object for the game
     */
    // public Board getBoard() {
    // //return this.board;
    //

    public GameRules getBoard() {
        return this.game;
    }

    /**
     * Gets the number of stones in a specific pit.
     * 
     * @param pitNum the number of the pit
     * @return the number of stones in the pit
     * @throws PitNotFoundException if the pit number is invalid
     */
    public int getNumStones(int pitNum) throws PitNotFoundException {

        // throw excpetion if pit was not initalized or is not valid
        // if (this.getBoard().getPits().isEmpty() || pitNum < 1 || pitNum > 12) {
        if (pitNum < 1 || pitNum > 12) {
            throw new PitNotFoundException("Invalid pit number");
        }

        // return this.getBoard().getNumStones(pitNum);
        return this.getBoard().getDataStructure().getNumStones(pitNum);
    }

    /**
     * Makes a move for the current player.
     * 
     * @param startPit the pit from which to start the move
     * @return the total number of stones remaining in the players side pits
     * @throws InvalidMoveException  if the move is invalid
     * @throws IllegalStateException if the game is over
     */
    public int move(int startPit) throws InvalidMoveException, IllegalStateException {

        // check if game is over
        if (isGameOver()) {
            throw new IllegalStateException("The game is over");
        }

        // get the current player
        Player currentPlayer = getCurrentPlayer();
        int playerNum = 2;
        if (currentPlayer == players.get(0)) {
            playerNum = 1;
        }

        // get pit is pit number pitNum
        // Pit pit = getBoard().getPits().get(startPit-1);
        // Pit pit = getBoard().getDataStructure().getNumStones(startPit);

        // check if the pit is empty
        // if (pit.getStoneCount() == 0) {
        if (getBoard().getDataStructure().getNumStones(startPit) == 0) {

            throw new InvalidMoveException("The pit is empty");
        }

        try {
            getBoard().moveStones(startPit, playerNum);
        } catch (InvalidMoveException e) {
            System.out.println("InvalidMove: " + e.getMessage());
        }

        // return the amount of stones in the current player's storage
        return 1;
    }

    /**
     * Gets the total number of stones in a player's store.
     * 
     * @param player the player
     * @return the total number of stones in the player's store
     * @throws NoSuchPlayerException if the player is not found
     */
    public int getStoreCount(Player player) throws NoSuchPlayerException {
        if (player.getName().isEmpty()) {
            throw new NoSuchPlayerException("Invalid player");
        }
        // return this.getBoard().getPlayerStoreCount(player);
        return player == players.get(0) ? player.getStoreCount() : players.get(1).getStoreCount();

    }

    /**
     * Gets the winner of the game.
     * 
     * @return the winning Player object or null for a tie
     * @throws GameNotOverException if the game is not over yet
     */
    public Player getWinner() throws GameNotOverException {

        // if the game is over
        if (this.getBoard().isSideEmpty(1) == true || this.getBoard().isSideEmpty(7) == true) {

            for (int i = 1; i <= 6; i++) {
                players.get(0).getStore().addStones(this.getBoard().getNumStones(i));
                players.get(1).getStore().addStones(this.getBoard().getNumStones(i + 6));
            }

            // return a winner (whoever has more stones in their store)
            return (players.get(0).getStore().getTotalStones() > players.get(1).getStore().getTotalStones())
                    ? players.get(0)
                    : ((players.get(1).getStoreCount() > players.get(0).getStoreCount())
                            ? players.get(1)
                            : null);

        } else {
            throw new GameNotOverException("The game is not over yet");
        }

    }

    /**
     * Checks if the game is over.
     * 
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {

        // return true if either side has no stones
        int p1Stones = 0;
        int p2Stones = 0;

        for (int i = 1; i <= 6; i++) {
            p1Stones += this.getBoard().getNumStones(i);
            p2Stones += this.getBoard().getNumStones(i + 6);
        }

        return p1Stones == 0 || p2Stones == 0;

    }

    /**
     * Generates a string representation of the game.
     * 
     * @return a string representation of the game and board
     */
    @Override
    public String toString() {
        return this.getBoard().toString();
    }
}
