package eu.funinnumbers.hyperengine.ui.visualoutput.tasks;

import eu.funinnumbers.hyperengine.ui.visualoutput.AreaMap;

import java.util.TimerTask;

/**
 * Updates the positions of the particles.
 */
public class UpdateAreaMapTask extends TimerTask {

    /**
     * Area map to update.
     */
    private final AreaMap map;

    /**
     * Default Constructor.
     *
     * @param thisMap the area map panel.
     */
    public UpdateAreaMapTask(final AreaMap thisMap) {
        map = thisMap;
    }

    public void run() {
        map.fixParticlesPosition();
        //map.tick();
    }
}
