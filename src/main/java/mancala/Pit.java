package mancala;

public class Pit implements Countable {

    // instance vars
    private int stones;

    /**
     * Default constructor for the Pit class.
     * Initializes the number of stones in the pit to 0 and the owner to null.
     */
    public Pit() {
        this.stones = 0;
    }

    /**
     * Gets the number of stones in the pit.
     * 
     * @return the number of stones in the pit
     */
    public int getStoneCount() {
        return this.stones;
    }

    /**
     * Adds a stone to the pit.
     */
    public void addStone() {
        this.stones++;
    }

    public void addStones(int num) {
        for (int i = 0; i < num; i++) {
            addStone();
        }
    }

    /**
     * Removes and returns the stones from the pit.
     * 
     * @return the number of stones removed from the pit
     */
    public int removeStones() {
        int removedStones = this.stones;
        this.stones = 0;
        return removedStones;
    }

    /**
     * Overrides the toString method in the Object class.
     * Returns a string representation of the number of stones in the pit.
     * 
     * @return a string representation of the number of stones in the pit
     */
    @Override
    public String toString() {
        return String.valueOf(this.stones);
    }

}
