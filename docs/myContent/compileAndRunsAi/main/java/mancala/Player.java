package mancala;
public class Player {
   private String name;
   private Store store;
   private int score;

   public Player(String name) {
       this.name = name;
       this.store = new Store();
       this.score = 0;
   }

   public String getName() {
       return this.name;
   }

   public void setName(String name) {
       this.name = name;
   }

   public Store getStore() {
       return this.store;
   }

   public void setStore(Store store) {
       this.store = store;
   }

   public int getScore() {
       return this.score;
   }

   public void setScore(int score) {
       this.score = score;
   }

   public void incrementScore(int increment) {
       this.score += increment;
   }

   public void resetScore() {
       this.score = 0;
   }

   @Override
   public String toString() {
       return "Player: " + this.name + ", Score: " + this.score;
   }
}
