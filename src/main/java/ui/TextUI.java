package ui;

import mancala.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;
import java.util.InputMismatchException;

public class TextUI {

    /**
     * The main method for the TextUI class.
     * It creates a new game of Mancala, adds two players, starts the game, and then
     * enters a game loop where it handles user input and game logic.
     * 
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        MancalaGame game;
        System.out.println("k or a?");
        GameRules theBoard;
        int abc;
        Scanner scanner = new Scanner(System.in);

        if (scanner.nextLine().equals("k")) {
            game = new MancalaGame(0);
            theBoard = new KalahRules();
            abc = 5;
        } else {
            game = new MancalaGame(1);
            theBoard = new AyoRules();
            abc = 10;
        }

        Saver saver = new Saver();
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        /*
         * System.out.println(", please enter name:");
         *
         * String n1 = scanner.nextLine();
         * System.out.println(", please enter name:");
         *
         * String n2 = scanner.nextLine();
         *
         * Player player1 = new Player(n1);
         * Player player2 = new Player(n2);
         */

        game.setPlayers(player1, player2);
        // Board theBoard = new Board();

        theBoard.getDataStructure().setUpPits();
        game.setBoard(theBoard);
        int pitNumber = -1;
        int turn = 1;
        String filename;

        // System.out.println("new (0) or old game (1)?");

        // int load = scanner.nextInt();
        // scanner.nextLine();

        // if (load == 1) {
        // try {
        // System.out.println("filename?");

        // filename = scanner.nextLine();
        // Serializable prevGame = saver.loadObject(filename);
        // } catch (IOException e) {
        // System.out.println("Error with load: " + e.getMessage());
        // }
        // }

        while (!game.isGameOver()) {

            Player currentPlayer = game.getCurrentPlayer();
            System.out.println(game.toString());
            while (true) {
                System.out.println(currentPlayer.getName() + ", please enter the pit number:");
                try {
                    pitNumber = scanner.nextInt();
                    // if (pitNumber == -2) {

                    // scanner.nextLine();

                    // System.out.println("filename?");

                    // filename = scanner.nextLine();

                    // try {
                    // saver.saveObject(saver.loadObject(filename), filename);
                    // } catch (IOException e) {
                    // System.out.println("Error with save: " + e.getMessage());
                    // }
                    // }

                    // if (currentPlayer==game.getPlayers().get(0)) {
                    // turn=1;
                    // } else {
                    // turn=-1;
                    // }
                    if (((pitNumber >= 1 && pitNumber <= 6) && turn == 1)
                            || ((pitNumber >= 7 && pitNumber <= 12) && turn == -1)) {
                        // if (game.getBoard().getPits().get(pitNumber - 1).getStoneCount() == 0) {
                        if (game.getBoard().getDataStructure().getNumStones(pitNumber) == 0) {
                            System.out.println("The pit you chose is empty. Please choose another pit.");
                        } else {
                            break;
                        }
                    } else {
                        System.out.println("Invalid pit number. Please try again.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine();
                }
            }

            int p1orP2 = currentPlayer.equals(player1) ? 1 : 2;
            int playerOG = game.getBoard().getDataStructure().getStoreCount(p1orP2);
            int sum = pitNumber - 1 + game.getBoard().getDataStructure().getNumStones(pitNumber);
            int afterStore = (turn == 1) ? 7 : 6;
            int beforeStore = (turn == 1) ? 1 : 12;
            int afterStoreOG = game.getBoard().getDataStructure().getNumStones(afterStore);
            int beforeStoreOG = game.getBoard().getDataStructure().getNumStones(beforeStore);

            try {
                game.move(pitNumber);
            } catch (InvalidMoveException e) {
                System.out.println("Invalid move: " + e.getMessage());
            }
            int storeIncrease = (((sum % 12) == 0) ? (sum / 12) : (sum / 12) + 1);
            int beforeStoreIncrease = (pitNumber == beforeStore) ? beforeStoreOG
                    : game.getBoard().getDataStructure().getNumStones(beforeStore) - beforeStoreOG;
            int afterStoreIncrease = game.getBoard().getDataStructure().getNumStones(afterStore) - afterStoreOG;
            int newTurn = (storeIncrease != 0 && storeIncrease > afterStoreIncrease
                    && beforeStoreIncrease == storeIncrease) ? 1 : 0;

            if (game.getBoard().getDataStructure().getStoreCount(p1orP2) == playerOG + storeIncrease && newTurn == 1
                    && abc == 5) {
                System.out.println("another turn");
            } else {
                game.setCurrentPlayer(currentPlayer == game.getPlayers().get(0) ? game.getPlayers().get(1)
                        : game.getPlayers().get(0));
                turn *= -1;
            }
        }

        System.out.println(game.toString());

        try {
            Player winner = game.getWinner();
            System.out.println(game.toString());
            // System.out.println("The winner is " + winner.getName());
            // System.out.println("Enter a filename to save the game:");
            // filename = scanner.next();
            // Saver.saveObject(game, filename);

            // } catch (IOException e) {
            // System.out.println("Error saving the game: " + e.getMessage());
        } catch (GameNotOverException e) {
            System.out.println("The game is not over yet");
        }

        scanner.close();
    }
}
