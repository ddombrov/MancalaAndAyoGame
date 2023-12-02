/**
 * Took concepts from the lab and online tutorial but not done
 */

// package ui;

// import javax.swing.*;
// import java.awt.*;
// import java.awt.event.ActionEvent;

// public class MancalaGUI extends JFrame {
// private JPanel gameContainer;
// private PositionAwareButton[][] buttons;
// private MancalaGame game;

// public MancalaGUI(String windowTitle, int width, int height) {

// // MancalaGame game;
// // System.out.println("k or a?");
// // GameRules theBoard;
// // int abc;
// // Scanner scanner = new Scanner(System.in);

// // if (scanner.nextLine().equals("k")) {
// // game = new MancalaGame(0);
// // theBoard = new KalahRules();
// // abc = 5;
// // } else {
// // game = new MancalaGame(1);
// // theBoard = new AyoRules();
// // abc = 10;
// // }

// super();
// setTitle(windowTitle);
// setSize(width, height);
// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

// game = new MancalaGame();
// Container contentPane = getContentPane();
// contentPane.setLayout(new BorderLayout());

// gameContainer = new JPanel();
// gameContainer.setLayout(new GridLayout(2, 7));
// populateGameGrid();

// JScrollPane scrollPane = new JScrollPane(gameContainer);

// contentPane.add(scrollPane, BorderLayout.CENTER);
// contentPane.add(setupActionPanel(),BorderLayout.EAST);

// pack();
// }

// private void populateGameGrid(MancalaGame game) {
// buttons = new PositionAwareButton[2][7];
// for (int row = 0; row < 2; row++) {
// for (int col = 0; col < 7; col++) {
// buttons[row][col] = new PositionAwareButton();
// buttons[row][col].setAcross(col + 1);
// buttons[row][col].setDown(row + 1);
// buttons[row][col].addActionListener(this::enterMove);
// gameContainer.add(buttons[row][col]);
// }
// }
// }

// private void enterMove(ActionEvent e) {
// PositionAwareButton clicked = (PositionAwareButton) e.getSource();
// int row = clicked.getDown() - 1;
// int col = clicked.getAcross() - 1;
// game.move(row, col);
// updateView();
// }

// private void updateView() {
// for (int row = 0; row < 2; row++) {
// for (int col = 0; col < 7; col++) {
// buttons[row][col].setText(Integer.toString(game.getStoneCount(row, col)));
// }
// }
// }

// public static void main(String[] args) {
// Mancala gui = new MancalaGUI("Mancala Game", 600, 400);
// gui.setVisible(true);
// }
// }
