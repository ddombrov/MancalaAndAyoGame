package mancala;

import java.util.ArrayList;

public class MancalaGame {
  private Board board;
  private ArrayList<Player> players;
  private Player currentPlayer;

  public MancalaGame() {
      this.board = new Board();
      this.players = new ArrayList<>();
  }

  public void setPlayers(Player onePlayer, Player twoPlayer) {
      this.players.add(onePlayer);
      this.players.add(twoPlayer);
      this.currentPlayer = onePlayer;
  }

  public ArrayList<Player> getPlayers() {
      return this.players;
  }

  public Player getCurrentPlayer() {
      return this.currentPlayer;
  }

  public void setCurrentPlayer(Player player) {
      this.currentPlayer = player;
  }

  public void setBoard(Board theBoard) {
      this.board = theBoard;
  }

  public Board getBoard() {
      return this.board;
  }

  public int getNumStones(int pitNum) throws PitNotFoundException {
      return this.board.getNumStones(pitNum);
  }
public int move(int startPit) throws InvalidMoveException {
   // Check if the game is over
   if (isGameOver()) {
       throw new IllegalStateException("The game is over");
   }

   // Get the current player
   Player currentPlayer = getCurrentPlayer();

   // Get the pit to play
   Pit pit = getBoard().getPits().get(startPit - 1);

   // Check if the pit is empty
   if (pit.getStones() == 0) {
       throw new InvalidMoveException("The pit is empty");
   }

   // Distribute the stones
   int stonesRemaining = getBoard().moveStones(startPit, currentPlayer);

   // Do not switch to the next player here
   // setCurrentPlayer(currentPlayer == getPlayers().get(0) ? getPlayers().get(1) : getPlayers().get(0));

   return stonesRemaining;
}




public int getStoreCount(Player player) throws NoSuchPlayerException {
     try {
         if (player == this.players.get(0)) {
             return this.board.getNumStones(6);
         } else if (player == this.players.get(1)) {
             return this.board.getNumStones(13);
         } else {
             throw new NoSuchPlayerException("No such player");
         }
   } catch (PitNotFoundException e) {
       // Handle exception
       System.out.println("Pit not found: " + e.getMessage());
       return -1; // Return -1 to indicate an error occurred
   }
}


 public Player getWinner() throws GameNotOverException {
     try {
         if (this.board.getNumStones(0) == 0 || this.board.getNumStones(7) == 0) {
             return this.board.getNumStones(0) > this.board.getNumStones(7) ? this.players.get(0) : this.players.get(1);
         } else {
             throw new GameNotOverException("The game is not over yet");
         }
   } catch (PitNotFoundException e) {
       // Handle exception
       System.out.println("Pit not found: " + e.getMessage());
       return null; // Return null to indicate an error occurred
   }
}

 public boolean isGameOver() {
     try {
         return this.board.getNumStones(0) == 0 || this.board.getNumStones(7) == 0;
     } catch (PitNotFoundException e) {
         // Handle exception
         System.out.println("Pit not found: " + e.getMessage());
         return false;
     }
 }

public void startNewGame() {
   this.board = new Board();
   this.board.initializeBoard(); // Initialize the board
   this.currentPlayer = this.players.get(0);
}

  @Override
  public String toString() {
      return this.board.toString();
  }
}
