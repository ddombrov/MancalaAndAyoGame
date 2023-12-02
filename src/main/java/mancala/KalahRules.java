package mancala;

public class KalahRules extends GameRules {

    /**
     * Perform a move in the Kalah game and return the number of stones added to the player's store.
     *
     * @param startPit  The starting pit
     * @param playerNum The current player's num
     * @return The number of stones added to the player's store
     * @throws InvalidMoveException If the move is invalid
     */    
    @Override
    public int moveStones(final int startPit, final int playerNum) throws InvalidMoveException {

        // make sure pit is valid
        if (startPit < 1 || startPit > 12) {
            throw new InvalidMoveException("Invalid starting pit");
        }

        distributeStones(startPit);
        return getDataStructure().getStoreCount(playerNum);
    }

    /**
     * Distribute stones from a starting pit and return the number distributed.
     *
     * @param startingPoint The starting pit for distribution.
     * @return The number of stones distributed.
     */    
    @Override
    public int distributeStones(final int startingPoint) {

        int currentPit = startingPoint;
        int stones = getDataStructure().getNumStones(startingPoint);
        final int stonesOG = stones;
        getDataStructure().removeStones(startingPoint);

        int whichPlayer;
        if (startingPoint < 7) {
            whichPlayer = 1;
        } else {
            whichPlayer = 2;
        }

        while (stones > 0) {

            if (currentPit == 1 && startingPoint < 7 || currentPit == 12 && startingPoint > 6) {
                getDataStructure().addToStore(whichPlayer, 1);
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

            if (stones > 0) {
                getDataStructure().addStones(currentPit, 1);
                stones--;
            }
        }

        final int whichSide = (currentPit < 7) ? 1 : 2;

        if (getDataStructure().getNumStones(currentPit) == 1 && whichPlayer == whichSide) {

            final int capturedStones = captureStones(currentPit);
            getDataStructure().addToStore(whichPlayer, capturedStones);
            return stonesOG + capturedStones;

        }

        if (currentPit == 7 && startingPoint < 7 || currentPit == 6 && startingPoint > 6) {
            setPlayer((whichPlayer == 1) ? 2 : 1);
        }
        return stonesOG;

    }

    /**
     * Capture stones from the opponent's pit and return the number captured.
     *
     * @param stoppingPoint The stopping point for capturing stones.
     * @return The number of stones captured.
     */
    @Override
    public int captureStones(final int stoppingPoint) {

        // set oppositeStore to be the pit across from the stoppingPoint
        int oppositeStore;
        if (stoppingPoint < 7) {
            oppositeStore = stoppingPoint + 6;
        } else {
            oppositeStore = stoppingPoint - 6;
        }

        // take the stones in opposite store and current store
        final int capturedStones = getDataStructure().getNumStones(oppositeStore)
                + getDataStructure().getNumStones(stoppingPoint);

        getDataStructure().removeStones(oppositeStore);
        getDataStructure().removeStones(stoppingPoint);

        // return how many stones were captured
        return capturedStones;
    }

}
