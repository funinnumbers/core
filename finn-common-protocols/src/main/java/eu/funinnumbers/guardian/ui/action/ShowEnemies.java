package eu.funinnumbers.guardian.ui.action;

import eu.funinnumbers.guardian.ui.misc.LEDManager;
import eu.funinnumbers.util.Logger;

/**
 * Displays the identities of neighboring guardians.
 */
public class ShowEnemies extends AbstractAction {

    /**
     * The identity of the enemy eu.funinnumbers.guardian.
     */
    private final int enemyID;

    /**
     * Constructor of ShowEnemies class.
     *
     * @param guardianID ID of an "enemy eu.funinnumbers.guardian" found within the radio range of the player
     */
    public ShowEnemies(final int guardianID) {
        super();
        this.enemyID = guardianID;
    }

    /**
     * Invoked when an action occurs in a Checkable MenuItem ( Knife, Gun e.t.c ).
     */
    public void actionPerformed() {
        Logger.getInstance().debug("Target confirmed with id" + enemyID);

    }

    /**
     * Invoked when an action occurs in a Checkable ( Knife, Gun e.t.c ) and
     * non Checkable MenuItem , primarily a display action regarding user interaction( Health, Ammo e.t.c ).
     */
    public void actionSelected() {
        final int lastLed = 6;
        LEDManager.getInstance().setOffLEDS(2, lastLed);
        LEDManager.getInstance().showID(enemyID);
    }

    /**
     * Invoked when an action occurs in a Checkable ( Knife, Gun e.t.c ) and
     * non Checkable MenuItem , primarily to stop action displaying in user interface( Health, Ammo e.t.c ).
     */
    public void actionDeselected() {
        final int lasLed = 6;
        LEDManager.getInstance().setOffLEDS(2, lasLed);
    }

}
