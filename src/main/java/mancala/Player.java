package mancala;

public class Player {

    // instance vars
    private String name;
    private Store store;
    private UserProfile profile;

    /**
     * Default constructor for the Player class.
     * Initializes the player's name to null and the store to a new Store object.
     */
    public Player() {
        this.name = profile.getName();
        this.store = new Store();
    }

    /**
     * Parameterized constructor for the Player class.
     * Initializes the player's name to the given name and the store to a new Store
     * object.
     * 
     * @param name the name of the player
     */
    public Player(String name) {
        this.store = new Store();
        this.name = name;
    }

    /**
     * Gets the name of the player.
     * 
     * @return the player's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the player's name.
     * 
     * @param name the new name of the player
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the player's store.
     * 
     * @param store the new Store object for the player
     */
    public void setStore(Store store) {
        this.store = store;
    }

    /**
     * Gets the count of the number of stones in the player's store.
     * 
     * @return the count of stones in the player's store
     */
    public int getStoreCount() {
        return this.store.getTotalStones();
    }

    /**
     * Add one stone to store count
     */
    public void addStone() {
        this.store.setStones(this.store.getTotalStones() + 1);
    }

    /**
     * Gets the player's store where they collect stones.
     * 
     * @return the player's store
     */
    public Store getStore() {
        return this.store;
    }

    /**
     * Overrides the toString method in the Object class.
     * Returns a string representation of the player, including their name and
     * store.
     * 
     * @return a string representation of the player
     */
    @Override
    public String toString() {
        return "Player: " + this.name + ", store: " + this.store;
    }
}
