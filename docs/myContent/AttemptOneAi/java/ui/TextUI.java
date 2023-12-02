package ui;
import mancala.*;
import java.util.Scanner;

public class TextUI {
    public static void main(String[] args) {
        // Create players, board, and game
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        Board board = new Board();
        MancalaGame game = new MancalaGame();

        // Set up the game
        board.initializeBoard();
        board.registerPlayers(player1, player2);
        game.setPlayers(player1, player2);
        game.setBoard(board);

        // Start the game loop
        while (!game.isGameOver()) {
            printGameState(game);

            // Get user input (you can implement this part)
            int startPit = getUserInput();

            // Make a move
            game.move(startPit);
        }

        // Print the final result
        printGameResult(game);
    }

    private static void printGameState(MancalaGame game) {
        System.out.println("Current State of the Game:");
        for (int i = 0; i < 6; i++) {
            System.out.println("Pit " + i + ": " + game.getNumStones(i) + " stones");
        }
        for (int i = 0; i < 2; i++) {
            System.out.println("Player " + (i + 1) + "'s store: " + game.getStoreCount(game.getPlayers().get(i)) + " stones");
        }
    }
    
    private static int getUserInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the pit number to make a move: ");
        return scanner.nextInt();
    }

    private static void printGameResult(MancalaGame game) {
        // Print the final result of the game
        // You may print the winner, final scores, etc.
        System.out.println("Game Over!");
        // Add your printing logic for the final result here
    }
}