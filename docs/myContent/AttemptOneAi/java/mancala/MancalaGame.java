package mancala;

import java.util.ArrayList;

public class MancalaGame {
    private ArrayList<Player> players;
    private Player currentPlayer;
    private Board theBoard;

    public MancalaGame() {
        this.players = new ArrayList<>();
    }

    public void setPlayers(Player onePlayer, Player twoPlayer) {
        players.add(onePlayer);
        players.add(twoPlayer);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        currentPlayer = player;
    }

    public void setBoard(Board theBoard) {
        this.theBoard = theBoard;
    }

    public Board getBoard() {
        return theBoard;
    }

    // Other methods...

    public int move(int startPit) {
        // Placeholder logic, replace with actual game logic
        int stonesToMove = theBoard.moveStones(startPit, currentPlayer);
        // Switch player for the next turn
        currentPlayer = (currentPlayer == players.get(0)) ? players.get(1) : players.get(0);
        return stonesToMove;
    }

    public boolean isGameOver() {
        for (int i = 0; i < 6; i++) {
            if (theBoard.getPits().get(i).getStoneCount() > 0) {
                return false;
            }
        }
        return true;
    }

    public void startNewGame() {
        theBoard.resetBoard();
        currentPlayer = players.get(0); // Set the first player as the current player
    }


    public int getNumStones(int pitnum) {
        return theBoard.getPits().get(pitnum).getStoneCount();
    }


    public int getStoreCount(Player player) {
       return player.getStore().getStoneCount();
    }

    Player getWinner() {
        Player winner = null;
        int maxStones = 0;
        for (Player player : players) {
            int stones = getStoreCount(player);
            if (stones > maxStones) {
                maxStones = stones;
                winner = player;
            }
        }
        return winner;

    }
    
}