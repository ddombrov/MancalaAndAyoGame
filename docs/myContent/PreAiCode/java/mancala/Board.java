/*TODO: FAQ
How do the different methods in the Board class work to make a move? I would suspect that the MancalaGame class called moveStones() which uses distributeStones() and captureStones() but which methods are meant to do each part of the move?
Answer: You're correct that the MancalaGame class uses moveStones(),to make a move. The moveStones() method then calls distributeStones(), and captureStones() as helper methods. It might even make more sense to have distributeStones() call captureStones()

Use the javadocs to give you more information about the context for the parameters and the return types for each of those methods. Remember that the methods are all listed as public in the javadocs but that they likely should not all be public. Some of them are helper methods that should have more restricted visibility.
*/

/* TODO: FAQ
What does the toString() method for each class display?
Answer: The toString() method for each class is responsible for returning a string that represents the current state of the class in a user-friendly format. This string is how the class's state will be displayed for the person using the game, and it will be printed by the TextUI.
*/

/* TODO: FAQ
Is the game meant to be 2 players, or 1 player versus the computer?
Answer: The game is meant to be used by 2 players sharing a computer. Don't try to implement an AI player for this assignment. That would be a good project for you to do on your own time so that you could have it for your CV.
*/

/* TODO: FAQ
Are we supposed to copy all the methods in the javadocs or just copy the class diagram?
Answer: The class diagram should match the javadocs. If it doesn't, assume the javadocs are correct. You should implement all of the methods shown in the javadocs.
*/

/* TODO: FAQ
Can you give an example of how to import the given classes' javadocs?
Answer: No. There is no simple way to import javadocs to create code. I did this intentionally so that you'd get some experience building classes from scratch.
*/

/* TODO: FAQ
Is it best to do JUnit tests in parallel with our code or all at the end?
Answer: Yes. I would recommend that you write the test cases as you are working on the methods. It will be easier to think of the different pathways through the execution as you are working on it.
*/

/* TODO: FAQ
Would you say a good first step is to figure out folder structure, and maybe start stubs, and playing around with GhatGPT?
Answer: Absolutely! That's a great approach.
*/
/* TODO: FAQ
Do we copy the config file from A1?
Answer: Yes, just copy the config folder from Assignment one.
*/

/* TODO: FAQ
How should we number the pits?
Answer: Number the pits from 1-12. Pit 1 starts at the leftmost pit on Player1’s side of the board. Numbering continues counter clockwise ending at pit 12 on the rightmost side of Player2’s side of the board. Player’s stores are on the right hand side of the board with respect to the Player. See attached image.
*/

/* TODO: FAQ
I don't know how I should debug my code with the junit tests. My code runs and the game plays but it is failing tests.
Answer: Your game may play properly for the situations you have tested but still fail the junit tests. A Junit test examines single methods at a time. If you have game mechanics that are executed outside of the the required methods, then they may not be called properly in junit tests.
*/

package mancala;
import java.util.ArrayList;

public class Board {

    //default constructor
    public Board() {
        
    }

    void setUpPits() {

    }
    
    ArrayList getPits() {
        return null;

    }
    
    ArrayList getStores() {
        return null;
        
    }
    
    void initalizeBoard() {
        
    }
    
    void resetBoard() {
        
    }
    
    void registerPlayers(Player one, Player two) {
        
    }
    
    int moveStones(int startPit, Player player) {
        return 0;
        
    }
    
    int distributeStones(int startingPoint) {
        return 0;

    }
    
    int captureStones(int stoppingPoint) {
        return 0;

    }
    
    int getNumStones(int pitNum) {
        return 0;

    }
    
    boolean isSideEmpty(int pitNum){
        return false;       

    }

}