package mancala;

public class Player {
    private String name;
    private Store store;

    public Player() {
        this.store = new Store();
    }

    public Player(String name) {
        this.name = name;
        this.store = new Store();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public int getStoreCount() {
        return store.getTotalStones();
    }

    public Store getStore() {
       return this.store;
    }

}
