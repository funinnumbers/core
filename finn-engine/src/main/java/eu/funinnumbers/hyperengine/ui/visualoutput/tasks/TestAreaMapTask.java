package eu.funinnumbers.hyperengine.ui.visualoutput.tasks;

import eu.funinnumbers.hyperengine.HyperEngineLogic;
import eu.funinnumbers.hyperengine.ui.visualoutput.HyperEngine;

import java.util.TimerTask;
import java.util.HashMap;

import eu.funinnumbers.util.Logger;

/**
 * Simple test for AreaMap.
 */
public class TestAreaMapTask extends TimerTask { //NOPMD

    /**
     * Parent applet.
     */
    private final HyperEngine parent;

    /**
     * Default Constructor.
     *
     * @param thisPanel the Processing applet.
     */
    public TestAreaMapTask(final HyperEngine thisPanel) {
        parent = thisPanel;
    }

    public void run() { //NOPMD
        final HashMap<String, String> playersMap = HyperEngineLogic.getInstance().getPlayersMap();

        if ((playersMap.size() < 45) && (parent.random(1) > 0.5)) {
            HyperEngineLogic.getInstance().initEvent("" + playersMap.size(), HyperEngineLogic.HYPERAPP);

        } else if (playersMap.size() > 0) {
            // pick random map
            final String mac = "" + (int) parent.random(playersMap.size());

            // pick random game
            final int game = (int) parent.random(5);
            String gameID;
            switch (game) {
                case 0:
                    gameID = HyperEngineLogic.TOW;
                    break;
                case 1:
                    gameID = HyperEngineLogic.CI;
                    break;
                case 2:
                    gameID = HyperEngineLogic.CIMAGE;
                    break;
                case 3:
                    gameID = HyperEngineLogic.MAGNETIZE;
                    break;
                default:
                    gameID = HyperEngineLogic.HYPERAPP;
                    break;
            }

            Logger.getInstance().debug("Moving Player [" + mac + "]" + gameID);

            // update map
		if (playersMap.get(mac) == null) {
			return;
		}
            if (!playersMap.get(mac).equals(gameID)) {
                HyperEngineLogic.getInstance().initEvent(mac, gameID);
            }
        }

    }
}



