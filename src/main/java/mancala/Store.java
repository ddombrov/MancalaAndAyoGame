package mancala;
import java.io.Serializable;

public class Store implements Countable, Serializable {

    // instance var
    private int stones;
    private Player owner;

    /**
     * Default constructor for the Store class.
     * Initializes the number of stones in the store to 0.
     */
    public Store() {
        this.stones = 0;
    }

    /**
     * Gets the number of stones in the store.
     * 
     * @return the number of stones in the store
     */
    public int getStones() {
        return this.stones;
    }

    /**
     * Sets the number of stones in the store.
     * 
     * @param stones the new number of stones in the store
     */
    public void setStones(final int stones) {
        this.stones = stones;
    }

    /**
     * Adds a specified number of stones to the store.
     * 
     * @param stones the number of stones to add to the store
     */
    public void addStones(final int stones) {
        this.stones += stones;
    }

    public void addStone() {
        this.stones += 1;
    }

    /**
     * Sets the owner of the store.
     * 
     * @param player the Player object to set as the owner of the store
     */
    public void setOwner(final Player player) {
        this.owner = player;
    }

    /**
     * Gets the owner of the store.
     * 
     * @return the Player object that owns the store
     */
    public Player getOwner() {
        return this.owner;
    }

    /**
     * Gets the total number of stones in the store.
     * 
     * @return the total number of stones in the store
     */
    public int getTotalStones() {
        return this.stones;
    }

    public int getStoneCount() {
        return this.stones;
    }

    /**
     * Empties the store and returns the number of stones that were in it.
     * 
     * @return the number of stones in the store before it was emptied
     */
    public int emptyStore() {
        final int removedStones = this.stones;
        this.stones = 0;
        return removedStones;
    }

    public int removeStones() {
        return emptyStore();
    }

    /**
     * Overrides the toString method in the Object class.
     * Returns a string representation of the number of stones in the store.
     * 
     * @return a string representation of the number of stones in the store
     */
    @Override
    public String toString() {
        return String.valueOf(this.stones);
    }
}
