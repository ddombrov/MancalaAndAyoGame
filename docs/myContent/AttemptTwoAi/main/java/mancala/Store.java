package mancala;
public class Store {
  private int stones;

  public Store() {
      this.stones = 0;
  }

  public int getStones() {
      return this.stones;
  }

  public void setStones(int stones) {
      this.stones = stones;
  }

  public void addStones(int stones) {
      this.stones += stones;
  }

  @Override
  public String toString() {
      return String.valueOf(this.stones);
  }
}

