package mancala;

import java.util.ArrayList;

public class MancalaGame {
  private Board board;
  private ArrayList<Player> players;
  private Player currentPlayer;
  private int consecutiveTurns = 0;


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

 // Switch to the next player
 setCurrentPlayer(currentPlayer == getPlayers().get(0) ? getPlayers().get(1) : getPlayers().get(0));

 return stonesRemaining;
}






public int getStoreCount(Player player) throws NoSuchPlayerException {
 if (player == this.players.get(0)) {
    try {
        return this.board.getNumStones(6);
    } catch (PitNotFoundException e) {
        e.printStackTrace();
        // Handle the exception appropriately here
    }
 } else if (player == this.players.get(1)) {
    try {
        return this.board.getNumStones(13);
    } catch (PitNotFoundException e) {
        e.printStackTrace();
        // Handle the exception appropriately here
    }
 } else {
    throw new NoSuchPlayerException("No such player");
 }
 // Add a return statement here to satisfy the compiler
 return -1;
}







public Player getWinner() throws GameNotOverException, NoSuchPlayerException {
  // Check if the game is over
  if (!isGameOver()) {
      throw new GameNotOverException("The game is not over yet");
  }

  // Return the player with the most stones in their store
  int player1Score = players.get(0).getStore().getStones();
  int player2Score = players.get(1).getStore().getStones();
  for (Pit pit : board.getPits()) {
      if (pit.belongsTo(players.get(0))) {
          player1Score += pit.getStones();
      } else if (pit.belongsTo(players.get(1))) {
          player2Score += pit.getStones();
      }
  }
  return player1Score > player2Score ? players.get(0) : players.get(1);
}





public boolean isGameOver() {
  // Check if all the pits in one side of the board are empty
for (int i = 0; i < 6; i++) {
   if (board.getPits().get(i).getStones() > 0) {
       return false;
   }
}
for (int i = 7; i < 13; i++) {
   if (board.getPits().get(i).getStones() > 0) {
       return false;
   }
}


  // Check if a player gets an extra turn
  if (consecutiveTurns > 1) {
      return true;
  }

  return true;
}



public void startNewGame() {
   // Check if the game has already started
   if (!getBoard().getPits().isEmpty()) {
       return;
   }

   // Initialize the board
   getBoard().initializeBoard();

   // Set the current player
   setCurrentPlayer(getPlayers().get(0));
}





  @Override
  public String toString() {
      return this.board.toString();
  }

  
}
