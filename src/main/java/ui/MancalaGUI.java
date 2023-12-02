/**
 * Took concepts from the lab and online tutorial but not done
 */

package ui;
import mancala.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class MancalaGUI extends JFrame {
    private JPanel gameContainer;
    private PositionAwareButton[][] buttons;
    private MancalaGame game;
    //private List<Item> itemList = new ArrayList<>();


    public MancalaGUI(String windowTitle, int width, int height) {
        // MancalaGame game;
        // System.out.println("k or a?");
        // GameRules theBoard;
        // int abc;
        // Scanner scanner = new Scanner(System.in);

        // if (scanner.nextLine().equals("k")) {
        //     game = new MancalaGame(0);
        //     theBoard = new KalahRules();
        //     abc = 5;
        // } else {
        //     game = new MancalaGame(1);
        //     theBoard = new AyoRules();
        //     abc = 10;
        // }

        super();
        setTitle(windowTitle);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        game = new MancalaGame();  
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        gameContainer = new JPanel();
        gameContainer.setLayout(new GridLayout(2, 7)); 
        populateGameGrid(game);

        JScrollPane scrollPane = new JScrollPane(gameContainer);

        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(setupActionPanel(),BorderLayout.EAST);

        pack();
    }
    private JPanel setupActionPanel() {
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.PAGE_AXIS));

        JButton addItemBtn = new JButton("Add New Item");
        addItemBtn.addActionListener(event -> addItemOption());
        actionPanel.add(addItemBtn);

        JButton completeItemBtn = new JButton("Complete An Item");
        actionPanel.add(completeItemBtn);

        return actionPanel;
    }


    private void addItemOption() {
        String title = JOptionPane.showInputDialog("Enter item title");
        String desc = JOptionPane.showInputDialog("Enter item desc");


        int priority;
        try {
            priority = Integer.parseInt(JOptionPane.showInputDialog("Enter item priority"));
        } catch (Exception err) {
            JOptionPane.showMessageDialog(this, "Invalid Priority! Defaulting to 1", "Priority Error", JOptionPane.ERROR_MESSAGE);
            priority = 1;
        }

        //list.addItem(title, desc, priority);
        refreshListPanel();
    }

    private void refreshListPanel() {
       // listPanel.removeAll();
        //populateListPanel(list);
        
        //listPanel.repaint();
        //listPanel.revalidate();
    }



    private void populateGameGrid(MancalaGame game) {
        buttons = new PositionAwareButton[2][7];
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 7; col++) {
                buttons[row][col] = new PositionAwareButton();
                buttons[row][col].setAcross(col + 1);
                buttons[row][col].setDown(row + 1);
                buttons[row][col].addActionListener(this::enterMove);
                gameContainer.add(buttons[row][col]);
            }
        }
    }

    private void enterMove(ActionEvent e) {
        PositionAwareButton clicked = (PositionAwareButton) e.getSource();
        int row = clicked.getDown() - 1;
        int col = clicked.getAcross() - 1;
        int pitNumber = (row - 1) * 6 + col;
        try {
            game.move(pitNumber);
        } catch (InvalidMoveException e2) {
            System.err.println("Invalid move: " + e2.getMessage());
        }
        updateView();
    } 
    private void updateView() {
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 7; col++) {
                buttons[row][col].setText(Integer.toString(game.getBoard().getDataStructure().getNumStones((row - 1) * 6 + col)));
            }
        }
    }

    public static void main(String[] args) {
        MancalaGUI gui = new MancalaGUI("Mancala Game", 600, 400);
        gui.setVisible(true);
    }
}
