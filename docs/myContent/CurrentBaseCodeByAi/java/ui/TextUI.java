package ui;
import mancala.*;
import java.util.Scanner;

public class TextUI {
   public static void main(String[] args) {
       MancalaGame game = new MancalaGame();

       // Add players
       Player player1 = new Player("Player 1");
       Player player2 = new Player("Player 2");
       game.setPlayers(player1, player2);

       // Start the game
       game.startNewGame();

       // Create a Scanner for user input
       Scanner scanner = new Scanner(System.in);

       // Game loop
       while (!game.isGameOver()) {
           // Get the current player
           Player currentPlayer = game.getCurrentPlayer();

           // Print the current state of the board
           System.out.println(game.toString());

           // Get the pit number from the user
           System.out.println(currentPlayer.getName() + ", please enter the pit number:");
           int pitNumber = scanner.nextInt();

           // Make a move
           try {
               game.move(pitNumber);
           } catch (InvalidMoveException e) {
               System.out.println("Invalid move: " + e.getMessage());
           }

           // Switch to the next player
           game.setCurrentPlayer(currentPlayer == game.getPlayers().get(0) ? game.getPlayers().get(1) : game.getPlayers().get(0));
       }

       // Print the final state of the board
       System.out.println(game.toString());

       // Determine the winner
       try {
           Player winner = game.getWinner();
           System.out.println("The winner is " + winner.getName());
       } catch (GameNotOverException e) {
           System.out.println("The game is not over yet");
       }

       // Close the scanner
       scanner.close();
   }
}
