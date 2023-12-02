package mancala;

import java.util.ArrayList;

public class Board {
    private ArrayList<Pit> pits;
    private ArrayList<Store> stores;
    private Player playerOne;
    private Player playerTwo;

   public Board() {
       this.pits = new ArrayList<>();
       this.stores = new ArrayList<>();
   }

    public void initializeBoard() {
        for (int i = 0; i < 6; i++) {
            Pit pit = new Pit();
            pit.addStone();
            pit.addStone();
            pit.addStone();
            pit.addStone();
            pits.add(pit);
        }
        for (int i = 0; i < 2; i++) {
            Store store = new Store();
            stores.add(store);
        }
    }

    public ArrayList<Pit> getPits() {
        return pits;
    }

    public ArrayList<Store> getStores() {
        return stores;
    }

    public void registerPlayers(Player one, Player two) {
        playerOne = one;
        playerTwo = two;
    }

    public int moveStones(int startPit, Player player) {
        int stonesToMove = pits.get(startPit).removeStones();
        int stoppingPoint = (startPit + stonesToMove) % 12;
        if (stoppingPoint == 0) {
            stoppingPoint = 12;
        }
        int capturedStones = captureStones(stoppingPoint);
        if (capturedStones > 0) {
            player.getStore().addStones(capturedStones);
        }
        return stonesToMove;
    }

    public int distributeStones(int startingPoint) {
        int stonesToDistribute = pits.get(startingPoint).removeStones();
        for (int i = startingPoint + 1; i != startingPoint; i = (i + 1) % 12) {
            pits.get(i).addStone();
        }
        return stonesToDistribute;
    }


    public int captureStones(int stoppingPoint) {
        int capturedStones = 0;
        if (stoppingPoint > 0 && stoppingPoint <= 6 && pits.get(stoppingPoint - 1).getStoneCount() > 0) {
            capturedStones += pits.get(stoppingPoint - 1).removeStones();
        }
        if (stoppingPoint > 6 && stoppingPoint <= 12 && pits.get(stoppingPoint - 1).getStoneCount() > 0) {
            capturedStones += pits.get(stoppingPoint - 1).removeStones();
        }
        return capturedStones;
    }


   public int getNumStones(int pitNum) {
       return this.pits.get(pitNum).getStoneCount();
   }

    //changed the variable AI gave from side to pitNum 
    public boolean isSideEmpty(int pitNum) {
       for (int i = pitNum * 6; i < (pitNum + 1) * 6; i++) {
           if (getNumStones(i) > 0) {
               return false;
           }
       }
       return true;
    }

    public void setUpPits() {
       for (int i = 0; i < 12; i++) {
           this.pits.add(new Pit());
       }
    }

    void resetBoard() {
        for (Pit pit : pits) {
            pit.addStones(4);
        }
        for (Store store : stores) {
            store.emptyStore();
        }
    }

}