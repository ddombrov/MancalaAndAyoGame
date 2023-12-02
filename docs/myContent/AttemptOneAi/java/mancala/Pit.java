package mancala;

public class Pit {
   private int stoneCount;

   public Pit() {
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

