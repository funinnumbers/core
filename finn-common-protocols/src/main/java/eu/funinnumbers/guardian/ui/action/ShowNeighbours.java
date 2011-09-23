/*
 * ShowNeighbours.java
 *
 * Created on 20  2008, 10:11
  *  ShowNeighbours Class Definition
 */
package eu.funinnumbers.guardian.ui.action;

import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.guardian.communication.echoprotocol.EchoProtocolManager;
import eu.funinnumbers.guardian.ui.misc.LEDManager;
import eu.funinnumbers.util.Observable;
import eu.funinnumbers.util.Observer;

/**
 * Displays the identities of the neighboring nodes.
 */
public class ShowNeighbours extends AbstractAction implements Observer {

    /**
     * Invoked when an action occurs in a Checkable MenuItem ( Knife, Gun e.t.c ).
     */
    public void actionPerformed() {
        // Does nothing
    }

    /**
     * Invoked when an action occurs in a Checkable ( Knife, Gun e.t.c ) and non Checkable MenuItem,
     * primarily a display action regarding user interaction( Health, Ammo e.t.c ).
     */
    public void actionSelected() {
        final int lastLed = 6;
        LEDManager.getInstance().setOffLEDS(2, lastLed);

        // Show the current count of neighbors
        LEDManager.getInstance().showCount(EchoProtocolManager.getInstance().getNeighbours().size());

        // Set as an echoprotocol observer
        EchoProtocolManager.getInstance().addObserver(this);
    }

    /**
     * Invoked when an action occurs in a Checkable ( Knife, Gun e.t.c ) and non Checkable MenuItem ,
     * primarily to stop action displaying in user interface( Health, Ammo e.t.c ).
     */
    public void actionDeselected() {
        // unset this observer
        EchoProtocolManager.getInstance().deleteObserver(this);
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param obs the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     *            method.
     */
    public void update(final Observable obs, final Object arg) {
        if (!(obs instanceof EchoProtocolManager)) {
            return;
        }

        // Only interested for Guardian related events
        if (!(arg instanceof Guardian)) {
            return;
        }

        // Show the new count of neighbours
        LEDManager.getInstance().showCount(EchoProtocolManager.getInstance().getNeighbours().size());
    }


}
