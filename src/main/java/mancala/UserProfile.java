package mancala;

import java.io.Serializable;

public class UserProfile implements Serializable {

        // instance vars
        private String name;
        final private int kGamesPlayed;
        final private int aGamesPlayed;
        final private int kWins;
        final private int aWins;
        private static final long serialVersionUID = 7000;

        /**
         * Default constructor for the Store class.
         * Initializes the number of stones in the store to 0.
         */
        public UserProfile() {
                this.name = "Player";
                this.kGamesPlayed = 0;
                this.aGamesPlayed = 0;
                this.kWins = 0;
                this.aWins = 0;
        }

        /**
         * Gets the name of the player.
         * 
         * @return the player's name
         */
        public String getName() {
                return this.name;
        }

}
