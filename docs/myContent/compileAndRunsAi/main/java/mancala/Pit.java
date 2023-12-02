package mancala;
public class Pit {
   private int stones;
   private Player owner;

   public Pit() {
       this.stones = 0;
       this.owner = null;
   }

   public Pit(Player owner) {
       this.stones = 0;
       this.owner = owner;
   }

   public int getStones() {
       return this.stones;
   }

   public void setStones(int stones) {
       this.stones = stones;
   }

   public void addStone() {
       this.stones++;
   }

   public Player getOwner() {
       return this.owner;
   }

   public void setOwner(Player owner) {
       this.owner = owner;
   }

   public boolean belongsTo(Player player) {
       return this.owner == player;
   }

   @Override
   public String toString() {
       return String.valueOf(this.stones);
   }

   public void addStones(int stones) {
   this.stones += stones;
}

}
