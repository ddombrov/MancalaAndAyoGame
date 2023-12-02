// package mancala;

// import java.util.ArrayList;

// public class Board {

// // instance vars
// private ArrayList<Pit> pits;
// private ArrayList<Store> stores;
// private ArrayList<Player> players;

// /**
// * Default constructor for the Board class.
// * Initializes the board with 12 pits, 2 stores, and an empty list of players.
// * It also calls the initializeBoard() and method.
// */
// public Board() {
// this.pits = new ArrayList<>();
// this.stores = new ArrayList<>();
// this.players = new ArrayList<>();
// this.setUpPits();
// this.setUpStores();
// initializeBoard();
// }

// /**
// * Sets up the pits on the board.
// * Creates 12 new Pit objects and adds them to the pits ArrayList.
// */
// public void setUpPits() {
// for (int i = 1; i <= 12; i++) {
// this.pits.add(new Pit());
// }
// }

// /**
// * Sets up the stores on the board.
// * Creates 2 new Store objects and adds them to the stores ArrayList.
// */
// public void setUpStores() {
// this.stores.add(new Store());
// this.stores.add(new Store());
// }

// /**
// * Returns the list of pits on the board.
// *
// * @return an ArrayList of Pit objects.
// */
// public ArrayList<Pit> getPits() {
// return this.pits;
// }

// /**
// * Returns the list of stores on the board.
// *
// * @return an ArrayList of Store objects.
// */
// public ArrayList<Store> getStores() {
// return this.stores;
// }

// /**
// * Initializes the board by setting up the pits and stores, and distributing
// * stones.
// */
// public void initializeBoard() {

// for (Pit pit : this.pits) {
// pit.addStone();
// pit.addStone();
// pit.addStone();
// pit.addStone();
// }
// for (Store store : this.stores) {
// store.setStones(0);
// }
// }

// /**
// * Resets the board to its initial state.
// * This is done by calling the initializeBoard() method.
// */
// public void resetBoard() {
// for (Pit pit : this.pits) {
// pit.removeStones();
// }
// for (Store store : this.stores) {
// store.emptyStore();
// }
// this.initializeBoard();
// }

// /**
// * Registers two players with their corresponding stores.
// *
// * @param one the first player
// * @param two the second player
// */
// public void registerPlayers(Player one, Player two) {
// this.players.add(one);
// this.players.add(two);
// one.setStore(this.stores.get(0));
// two.setStore(this.stores.get(1));
// }

// /**
// * Moves stones from a starting pit to the player's store.
// *
// * @param startPit the pit where the move starts
// * @param player the player making the move
// * @return the total number of stones added to the corresponding store
// * @throws InvalidMoveException if the starting pit is invalid
// */
// public int moveStones(int startPit, Player player) throws
// InvalidMoveException {
// startPit -= 1;

// // make sure pit is valid
// if (startPit < 0 || startPit > 11) {
// throw new InvalidMoveException("Invalid starting pit");
// }

// try {
// distributeStones(startPit + 1);
// } catch (PitNotFoundException e) {
// System.out.println("Pit not found: " + e.getMessage());
// }
// return player.getStoreCount();
// }

// /**
// * Distributes stones from a starting pit to other pits and stores.
// *
// * @param startingPoint the pit where the distribution starts
// * @return the total number of stones added to pits and stores
// * @throws PitNotFoundException if the starting pit is invalid
// */
// public int distributeStones(int startingPoint) throws PitNotFoundException {
// startingPoint -= 1;

// // make sure pit is valid
// if (startingPoint < 0 || startingPoint > 11) {
// throw new PitNotFoundException("Invalid starting pit");
// }

// int currentPit = startingPoint;
// int stones = pits.get(startingPoint).getStoneCount();
// int stonesOG = stones;
// pits.get(startingPoint).removeStones();

// int whichPlayer = (startingPoint < 6) ? 0 : 1;

// while (stones > 0) {

// if (currentPit == 0 && startingPoint < 6) {
// stores.get(whichPlayer).addStones(1);
// stones--;
// } else if (currentPit == 11 && startingPoint > 5) {
// stores.get(whichPlayer).addStones(1);
// stones--;
// }

// if (currentPit == 0) {
// currentPit = 6;
// } else if (currentPit == getPits().size() - 1) {
// currentPit = 5;
// } else {
// if (currentPit < getPits().size() / 2) {
// currentPit = currentPit - 1;
// } else if (currentPit > getPits().size() / 2 - 1) {
// currentPit = currentPit + 1;
// }
// }

// if (stones > 0) {
// pits.get(currentPit).addStone();
// stones--;
// }
// }
// int whichSide = (currentPit < 6) ? 0 : 1;

// if (pits.get(currentPit).getStoneCount() == 1 && whichPlayer == whichSide) {
// try {
// int capturedStones = captureStones(currentPit + 1);
// stores.get(whichPlayer).addStones(capturedStones);
// return stonesOG + capturedStones;
// } catch (PitNotFoundException e) {
// System.out.println("Pit not found: " + e.getMessage());
// }

// }
// return stonesOG;
// }

// /**
// * Captures stones from the opponent's pits.
// *
// * @param stoppingPoint the pit where the capture stops
// * @return the number of stones captured
// * @throws PitNotFoundException if the stopping pit is invalid
// */
// public int captureStones(int stoppingPoint) throws PitNotFoundException {
// stoppingPoint -= 1;

// // make sure pit is valid
// if (stoppingPoint < 0 || stoppingPoint > 11) {
// throw new PitNotFoundException("Invalid stopping pit");
// }

// // set oppositeStore to be the pit across from the stoppingPoint
// int oppositeStore;
// if (stoppingPoint < 6) {
// oppositeStore = stoppingPoint + 6;
// } else {
// oppositeStore = stoppingPoint - 6;
// }
// // take the stones in opposite store
// int capturedStones = pits.get(oppositeStore).getStoneCount();
// capturedStones += pits.get(stoppingPoint).getStoneCount();

// pits.get(oppositeStore).removeStones();
// pits.get(stoppingPoint).removeStones();

// // return how many stones were captured
// return capturedStones;
// }

// /**
// * Gets the number of stones in a specific pit.
// *
// * @param pitNum the number of the pit
// * @return the number of stones in the pit
// * @throws PitNotFoundException if the pit number is invalid
// */
// public int getNumStones(int pitNum) throws PitNotFoundException {
// pitNum -= 1;

// // throw excpetion if pit was not initalized or is not valid
// if (pits.isEmpty() || pitNum < 0 || pitNum > 11) {
// throw new PitNotFoundException("Invalid pit number");
// }

// // otherwise return the number of stones at that pit
// return pits.get(pitNum).getStoneCount();
// }

// /**
// * Checks if one side of the board is empty.
// *
// * @param pitNum the number of the pit
// * @return true if the side of the board that includes the parameter pit
// number
// * is empty
// * @throws PitNotFoundException if the pit number is invalid
// */
// public boolean isSideEmpty(int pitNum) throws PitNotFoundException {
// pitNum -= 1;

// // make sure pit valid
// if (pitNum < 0 || pitNum > 11) {
// throw new PitNotFoundException("Invalid pit number");
// }

// // start by assuming sides are both empty
// boolean isSide1Empty = true;
// boolean isSide2Empty = true;

// for (int i = 0; i <= 5; i++) {

// // if player 1 has pits then his side's not empty
// if (pits.get(i).getStoneCount() > 0) {
// isSide1Empty = false;
// }

// // if player 2 has pits then his side's not empty
// if (pits.get(i + 6).getStoneCount() > 0) {
// isSide2Empty = false;
// }
// }

// // return a boolean on side emptiness based on pitNum
// return pitNum < 6 ? isSide1Empty : isSide2Empty;
// }

// /**
// * Returns a string representation of the board.
// *
// * @return a string with the number of stones in each pit and store
// */
// @Override
// public String toString() {
// StringBuilder strBldr = new StringBuilder();

// // if the pits aren't empty
// if (getPits().isEmpty()) {
// // if the pits are empty, print that
// strBldr.append("No pits available.\n");

// } else {
// strBldr.append("\n");

// // go through the pits
// for (int i = 0; i <= getPits().size() / 2 - 1; i++) {

// // add to the string "Pit i: i's stones"
// strBldr.append("Pit").append(i + 1).append(":
// ").append(getPits().get(i).getStoneCount())
// .append("\t\t");

// }

// // if the stores aren't empty
// if (getStores().isEmpty()) {

// strBldr.append("No stores available.\n");

// } else {
// // add to the string player 1 and 2's store counts

// strBldr.append("\n");
// strBldr.append("Player1 Store: ").append(getStores().get(0).getStones());
// strBldr.append("\t\t\t\t\t\t");
// strBldr.append("Player2 Store:
// ").append(getStores().get(1).getStones()).append("\n");

// }

// // go through the pits
// for (int i = 6; i <= getPits().size() - 1; i++) {

// if (i >= 9) {
// strBldr.append("Pit").append(i + 1).append(":
// ").append(getPits().get(i).getStoneCount())
// .append("\t");

// } else {
// strBldr.append("Pit").append(i + 1).append(":
// ").append(getPits().get(i).getStoneCount())
// .append("\t\t");

// }

// }

// // return the string
// return strBldr.toString();
// }

// public int getPlayerStoreCount(Player player) throws NoSuchPlayerException {
// for (Player whichPlayer : players) {
// if (whichPlayer == player) {
// return whichPlayer.getStoreCount();
// }
// }
// throw new NoSuchPlayerException("No such player");
// }

// }
