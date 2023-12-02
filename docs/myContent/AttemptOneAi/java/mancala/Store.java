package mancala;
public class Store {
   private int stoneCount;

   public Store() {
       this.stoneCount = 0;
   }

   public int getStoneCount() {
       return this.stoneCount;
   }

   public void addStones(int count) {
       this.stoneCount += count;
   }

   public void removeStones(int count) {
       this.stoneCount -= count;
   }
}
