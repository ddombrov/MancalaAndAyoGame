package mancala;

import java.util.ArrayList;

public class Board {
  private ArrayList<Pit> pits;
  private ArrayList<Store> stores;
  private ArrayList<Player> players;

public Board() {
   this.pits = new ArrayList<>();
   this.stores = new ArrayList<>();
   this.players = new ArrayList<>();
   this.setUpPits();
}

  public void setUpPits() {
      for (int i = 0; i < 12; i++) {
          this.pits.add(new Pit());
      }
  }

  public ArrayList<Pit> getPits() {
      return this.pits;
  }

  public ArrayList<Store> getStores() {
      return this.stores;
  }

  public void setUpStores() {
      this.stores.add(new Store());
      this.stores.add(new Store());
  }

public void initializeBoard() {
   this.pits = new ArrayList<>();
   for (int i = 0; i < 12; i++) {
       Pit pit = new Pit();
       pit.setStones(4);
       this.pits.add(pit);
   }
   this.stores = new ArrayList<>();
   for (int i = 0; i < 2; i++) {
       Store store = new Store();
       store.setStones(0);
       this.stores.add(store);
   }
}



  public void resetBoard() {
      this.initializeBoard();
  }

  public void registerPlayers(Player one, Player two) {
      this.players.add(one);
      this.players.add(two);
      one.setStore(this.stores.get(0));
      two.setStore(this.stores.get(1));
  }
// Check if the pit is valid
public boolean isValidPit(int pit) {
  return pit >= 1 && pit <= 12;
}

// Get the pit to play
public Pit getPit(int pit) throws InvalidMoveException {
  if (!isValidPit(pit)) {
      throw new InvalidMoveException("Invalid pit number");
  }
  return this.pits.get(pit - 1);
}

public int moveStones(int startPit, Player currentPlayer) throws InvalidMoveException {
  // Get the pit to play
  Pit pit = getPit(startPit);

  // Check if the pit is empty
  if (pit.getStones() == 0) {
      throw new InvalidMoveException("The pit is empty");
  }

  // Check if the pit belongs to the current player
  if (!pit.belongsTo(currentPlayer)) {
      throw new InvalidMoveException("The pit does not belong to the current player");
  }

  // Distribute the stones
  int stones = pit.getStones();
  pit.setStones(0);
  int currentPit = startPit;
  while (stones > 0) {
      currentPit = (currentPit + 1) % 14;
      if (currentPit == 6 || currentPit == 13) {
          continue;
      }
      pits.get(currentPit - 1).addStone();
      stones--;
  }

  // Check if the last stone landed in an empty pit of the same player
  if (pits.get(currentPit).belongsTo(currentPlayer) && pits.get(currentPit).getStones() == 1) {
      // Capture the stones
      try {
   captureStones(currentPit);
} catch (PitNotFoundException e) {
   e.printStackTrace();
}

  }

  return pits.get(currentPit).getStones();
}


   public int distributeStones(int startingPoint) throws PitNotFoundException {
       if (startingPoint < 0 || startingPoint > 13) {
           throw new PitNotFoundException("Invalid starting pit");
       }
       int stones = pits.get(startingPoint).getStones();
       pits.get(startingPoint).setStones(0);
       int currentPit = startingPoint;
       while (stones > 0) {
           currentPit = (currentPit + 1) % 14;
           if (currentPit == 6 || currentPit == 13) {
               continue;
           }
           pits.get(currentPit).addStone();
           stones--;
       }
       return pits.get(currentPit).getStones();
   }

/**
 * Captures stones from the opponent's pits.
 * @param stoppingPoint - The stopping pit
 * @return The number of stones captured, if any
 * @throws PitNotFoundException - If the pit number is invalid
 */
public int captureStones(int stoppingPoint) throws PitNotFoundException {
   if (stoppingPoint < 0 || stoppingPoint > 13) {
       throw new PitNotFoundException("Invalid stopping pit");
   }
   int oppositeStore = 6 + 12 - stoppingPoint;
   int capturedStones = pits.get(stoppingPoint).getStones();
   pits.get(stoppingPoint).setStones(0);
   pits.get(oppositeStore).addStones(capturedStones);
   return capturedStones;
}


public int getNumStones(int pitNum) throws PitNotFoundException {
 if (pits.isEmpty() || pitNum < 1 || pitNum > 12) {
   throw new PitNotFoundException("Invalid pit number");
 }
 return pits.get(pitNum - 1).getStones();
}





   public boolean isSideEmpty(int pitNum) throws PitNotFoundException {
       if (pitNum < 0 || pitNum > 13) {
           throw new PitNotFoundException("Invalid pit number");
       }
       boolean isSide1Empty = true;
       boolean isSide2Empty = true;
       for (int i = 0; i < 6; i++) {
           if (pits.get(i).getStones() > 0) {
               isSide1Empty = false;
           }
           if (pits.get(i + 7).getStones() > 0) {
               isSide2Empty = false;
           }
       }
       return pitNum < 6 ? isSide1Empty : isSide2Empty;
   }
@Override
public String toString() {
   StringBuilder sb = new StringBuilder();

   // Print the pits
   if (!getPits().isEmpty()) {
       for (int i = 0; i < getPits().size(); i++) {
           sb.append("pit").append(i + 1).append(": ").append(getPits().get(i).getStones()).append("\n");
       }
   } else {
       sb.append("No pits available.\n");
   }

   // Print the stores
   if (!getStores().isEmpty()) {
       sb.append("player1 storing pit: ").append(getStores().get(0).getStones()).append("\n");
       sb.append("player2 storing pit: ").append(getStores().get(1).getStones()).append("\n");
   } else {
       sb.append("No stores available.\n");
   }

   return sb.toString();
}



   public void removeStones(int pit, int stones) throws PitNotFoundException {
   if (pit < 0 || pit > 13) {
       throw new PitNotFoundException("Invalid pit number");
   }
   pits.get(pit).setStones(pits.get(pit).getStones() - stones);
}
public void addStone(int pit, int stone) throws PitNotFoundException {
   if (pit < 0 || pit > 13) {
       throw new PitNotFoundException("Invalid pit number");
   }
   pits.get(pit).addStone();
}

}
