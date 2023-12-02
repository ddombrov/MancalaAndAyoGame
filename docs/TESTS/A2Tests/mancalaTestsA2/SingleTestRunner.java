package mancala;

public class SingleTestRunner {
    private Board board;
    private Player player1;
    private Player player2;


public void setUp() {
        board = new Board();
        board.initializeBoard();
        player1 = new Player("Player1");
        player2 = new Player("Player2");
        board.registerPlayers(player1, player2);

    }
    private int getPitValue(int pit){
        return board.getPits().get(pit-1).getStoneCount();
    }

    private int getStoreValue(int store){
        return board.getStores().get(store-1).getTotalStones();
    }


    public void testSetUpPitsAndGetPits() {

        for (Pit pit : board.getPits()) {
            System.out.println("expected: 4, actual: " + pit.getStoneCount())
        }
    }



    public void testMoveStonesValidMove() throws InvalidMoveException,PitNotFoundException {
        // Assuming you have a valid move (e.g., startPit = 1)
        int startPit = 3;

        // Perform the move
        int stonesAddedToStore = board.moveStones(startPit, player1);
        System.out.println("expected: 5, Actual: "+ getPitValue(4));
   
        //
        //assertEquals(1,player1.getStoreCount());

    }


    public void testMoveStonesInvalidMove() {
        // Assuming you have an invalid move (e.g., startPit = 0, which is out of bounds)

        // The method should throw an InvalidMoveException for an invalid move
        assertThrows(InvalidMoveException.class, () -> board.moveStones(14, player1));
    }


    public void testMoveStonesCaptureStones() throws InvalidMoveException ,Exception{
        // Manually set up the board for capturing opponent's stones
        // For example, arrange the pits and stones to simulate capturing
        // Player1's last stone lands in an empty pit on their side, capturing opponent's stones

        // Set up the board state


      

        // Perform the move that captures opponent's stones
 

        // Assertions
        // You can assert the expected captured stones and other game conditions
        // For example, assert that player1's store has increased by the expected amount


    }


public static void main(String[] args){
    SingleTestRunner test = new SingleTestRunner();
    test.setUp();

    test.testMoveStonesValidMove();


}



}
