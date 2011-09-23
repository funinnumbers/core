package eu.funinnumbers.engine.playertracking;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.TimerTask;


public class CleaningTask extends TimerTask {

    public static final int TIMEOUT = 5000;

    private final PlayersTracker ptracker;


    public CleaningTask(final PlayersTracker ptracker) {
        this.ptracker = ptracker;

    }

    public void run() {
        final HashMap<String, Long> activePlayers = ptracker.getActivePlayers();
        final long now = System.currentTimeMillis();
        // Logger.getInstance().debug("track: Cleaning Active players [" + activePlayers.size() +"]");

        try {
            // iterate active players
            for (String mac : activePlayers.keySet()) {
                if (now - activePlayers.get(mac) > 30 * TIMEOUT) {
                    // remove this entry
                    ptracker.accessActivePlayers(false, mac);
                }
            }
        }
        catch (ConcurrentModificationException exe) {
            //hm...
        }
        //Logger.getInstance().debug("track: Active players cleaned [" + ptracker.getNumberOfActivePlayers()+"]");

    }
}
