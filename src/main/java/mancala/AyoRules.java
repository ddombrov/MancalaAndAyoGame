package mancala;

public class AyoRules extends GameRules {

    /**
     * Perform a move in the Ayo game and return the number of stones added to the player's store.
     *
     * @param startPit  The starting pit.
     * @param playerNum The current player's num.
     * @return The number of stones added to the player's store.
     * @throws InvalidMoveException If the move is invalid.
     */
    @Override
    public int moveStones(int startPit, int playerNum) throws InvalidMoveException {

        // make sure pit is valid
        if (startPit < 1 || startPit > 12) {
            throw new InvalidMoveException("Invalid starting pit");
        }
        int originalCount=getDataStructure().getStoreCount(playerNum);
        distributeStones(startPit);
        return getDataStructure().getStoreCount(playerNum)-originalCount;
    }

    /**
     * Distribute stones from a starting pit and return the number distributed.
     *
     * @param startingPoint The starting pit for distribution.
     * @return The number of stones distributed.
     */    
    @Override
    public int distributeStones(int startingPoint) {

        int currentPit = startingPoint;
        int stones = getDataStructure().getNumStones(startingPoint);
        // System.out.println(stones);
        int stonesOG = stones;
        getDataStructure().removeStones(startingPoint);
        int endedAtStore = 0;
        int whichPlayer;
        if (startingPoint < 6) {
            whichPlayer = 1;
        } else {
            whichPlayer = 2;
        }

        if (currentPit == 1) {
            currentPit = 7;
        } else if (currentPit == 12) {
            currentPit = 6;
        } else {

            if (currentPit < 7) {
                currentPit = currentPit - 1;
            } else if (currentPit > 6) {
                currentPit = currentPit + 1;
            }

        }

        while (stones > 0) {

            // skip start
            if (currentPit < 7 && currentPit == startingPoint) {
                currentPit = currentPit - 1;
                if (currentPit == 0) {
                    currentPit = 7;
                }
            } else if (currentPit > 6 && currentPit == startingPoint) {
                currentPit = currentPit + 1;
                if (currentPit == 13) {
                    currentPit = 6;
                }
            }

            // System.out.println(stones);

            // System.out.println(stones);

            // System.out.println(this);
            // System.out.println(stones);

            if (currentPit == 7 && startingPoint < 7 || currentPit == 6 && startingPoint > 6) {
                getDataStructure().addToStore(whichPlayer, 1);
                stones--;
                if (stones == 0) {
                    endedAtStore = 1;
                }

            }

            if (stones > 0) {
                if (currentPit != startingPoint) {
                    getDataStructure().addStones(currentPit, 1);
                    stones--;
                }

                if (currentPit == 1) {
                    currentPit = 7;
                } else if (currentPit == 12) {
                    currentPit = 6;
                } else {

                    if (currentPit < 7) {
                        currentPit = currentPit - 1;
                    } else if (currentPit > 6) {
                        currentPit = currentPit + 1;
                    }

                }

            }

            if (stones == 0 && getDataStructure().getNumStones(currentPit) > 0 && endedAtStore == 0) {
                // System.out.println(getDataStructure().getNumStones(currentPit));

                stones = getDataStructure().getNumStones(currentPit);
                getDataStructure().removeStones(currentPit);
            }

        }
        // System.out.println("ENDOFOLOOP");
        int capturedStones = 0;
        int whichSide = currentPit < 7 ? 1 : 2;
        int posOrNeg = currentPit < 7 ? -1 : 1;
        int posOrNegSix = posOrNeg * 6;

        if (endedAtStore == 0) {
            if (getDataStructure().getNumStones(currentPit) == 1 && whichSide == whichPlayer
                    && getDataStructure().getNumStones(currentPit + posOrNegSix) != 0) {

                capturedStones = captureStones(currentPit);
                getDataStructure().addToStore(whichPlayer, capturedStones);
            }
        }

        // System.out.println("RETURN OF DS");

        return stonesOG + capturedStones;
    }

    /**
     * Capture stones from the opponent's pit and return the number captured.
     *
     * @param stoppingPoint The stopping point for capturing stones.
     * @return The number of stones captured.
     */    
    @Override
    public int captureStones(int stoppingPoint) {

        // set oppositeStore to be the pit across from the stoppingPoint
        int oppositeStore;
        if (stoppingPoint < 7) {
            oppositeStore = stoppingPoint + 6;
        } else {
            oppositeStore = stoppingPoint - 6;
        }

        // take the stones in opposite store and current store
        int capturedStones = getDataStructure().getNumStones(oppositeStore);

        getDataStructure().removeStones(oppositeStore);

        // return how many stones were captured
        return capturedStones;
    }

}
