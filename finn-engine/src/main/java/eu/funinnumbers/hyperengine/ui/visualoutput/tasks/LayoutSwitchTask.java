package eu.funinnumbers.hyperengine.ui.visualoutput.tasks;

import eu.funinnumbers.hyperengine.ui.visualoutput.HyperEngine;

import java.util.TimerTask;

/**
 * Switches layouts.
 */
public class LayoutSwitchTask extends TimerTask {

    /**
     * Parent applet.
     */
    private final HyperEngine parent;

    /**
     * Default Constructor.
     *
     * @param thisPanel the Processing applet.
     */
    public LayoutSwitchTask(final HyperEngine thisPanel) {
        parent = thisPanel;
    }

    /**
     * Switch layout.
     */
    public void run() {
        parent.setLayout2x2(parent.random(1) > 0.5);
    }

}
