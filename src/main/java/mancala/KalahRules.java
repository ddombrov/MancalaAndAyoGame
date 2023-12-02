package mancala;

public class KalahRules extends GameRules {

    private static final int LAST_PIT=12;
    private static final int FIRST_PIT=1;
    private static final int LAST_P1_PIT=6;
    private static final int FIRST_P2_PIT=7;
    private static final long serialVersionUID = 125;

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
        if (startPit < FIRST_PIT || startPit > LAST_PIT) {
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
        if (startingPoint < FIRST_P2_PIT) {
            whichPlayer = 1;
        } else {
            whichPlayer = 2;
        }

        while (stones > 0) {

            if (currentPit == FIRST_PIT && startingPoint < FIRST_P2_PIT || currentPit == LAST_PIT && startingPoint > LAST_P1_PIT) {
                getDataStructure().addToStore(whichPlayer, 1);
                stones--;
            }

            if (currentPit == FIRST_PIT) {
                currentPit = 7;
            } else if (currentPit == LAST_PIT) {
                currentPit = 6;
            } else {
                if (currentPit < FIRST_P2_PIT) {
                    currentPit = currentPit - 1;
                } else if (currentPit > LAST_P1_PIT) {
                    currentPit = currentPit + 1;
                }
            }

            if (stones > 0) {
                getDataStructure().addStones(currentPit, 1);
                stones--;
            }
        }

        final int whichSide = currentPit < FIRST_P2_PIT ? 1 : 2;
        int capturedStones=0;
        if (getDataStructure().getNumStones(currentPit) == FIRST_PIT && whichPlayer == whichSide) {

            capturedStones = captureStones(currentPit);
            getDataStructure().addToStore(whichPlayer, capturedStones);
        }

        if (currentPit == FIRST_P2_PIT && startingPoint < FIRST_P2_PIT || currentPit == LAST_P1_PIT && startingPoint > LAST_P1_PIT) {
            setPlayer((whichPlayer == FIRST_PIT) ? 2 : 1);
        }
            return stonesOG + capturedStones;

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
        if (stoppingPoint < FIRST_P2_PIT) {
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
