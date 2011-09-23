package eu.funinnumbers.engine.playertracking;

import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observable;

import java.util.HashMap;
import java.util.Timer;

public final class PlayersTracker extends Observable {

    /**
     * Contains the MAC addresses of active players along with their timestamp of latest activity.
     */
    private HashMap<String, Long> activePlayers; //NOPMD

    private static PlayersTracker thisTracker;

    /**
     * Default constractor.
     */
    private PlayersTracker() {
        Logger.getInstance().debug("Starting Players Tracker");
        activePlayers = new HashMap<String, Long>();
        new Timer().scheduleAtFixedRate(new CleaningTask(this), CleaningTask.TIMEOUT, CleaningTask.TIMEOUT);
    }


    public static PlayersTracker getInstance() {
        synchronized (PlayersTracker.class) {
            // Check if an instance has already been created
            if (thisTracker == null) {
                // Create a new instance if not
                Logger.getInstance().debug("PlayersTracker Created");
                thisTracker = new PlayersTracker();
            }
        }
        // Return the  instance
        return thisTracker;
    }

    /**
     * Updates the last known timestamp of players activity to current time.
     *
     * @param macAddress the MAC Address of the player as String.
     */
    public void trackMac(final String macAddress) {
        // Update timestamp of player or add him
        if (macAddress != null) {
            accessActivePlayers(true, macAddress);
        }
    }


    /**
     * Provides synchronized access to activePlayers hashmap.
     *
     * @param put        indicates a "put" action if true, and a "remove" action if false.
     * @param macAddress the MAC address of the player to be accessed.
     */
    protected synchronized void accessActivePlayers(final boolean put, final String macAddress) {

        if (put) {
            if (activePlayers.containsKey(macAddress)) {
                //Logger.getInstance().debug("track: Player added [" + macAddress + "]");

                this.setChanged();
                notifyObservers((Integer) activePlayers.size());

            }
            // Update anyhow
            activePlayers.put(macAddress, System.currentTimeMillis());


        } else {
            activePlayers.remove(macAddress);
            //Logger.getInstance().debug("track: Player removed [" + macAddress + "]");

            this.setChanged();
            notifyObservers((Integer) activePlayers.size());


        }

    }


    /**
     * Provides access to activePlayers hashmap.
     *
     * @return the hashmap containing the active mac addresses of players as keys.
     */
    public synchronized HashMap<String, Long> getActivePlayers() {
        return activePlayers;

    }

    /**
     * Returns the number of active players.
     *
     * @return the number of active players as int.
     */
    public int getNumberOfActivePlayers() {
        return activePlayers.size();

    }


}
