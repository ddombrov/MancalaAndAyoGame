package mancala;

import java.io.Serializable;

public class UserProfile implements Serializable {

        // instance vars
        private String name;
        private int kGamesPlayed;
        private int aGamesPlayed;
        private int kWins;
        private int aWins;

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
