package eu.funinnumbers.guardian.communication.eventlistener;

import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observable;

/**
 * Controls all events received from stations (and eu.funinnumbers.engine) and distributes them to all listening objects.
 */
public final class EventManager extends Observable {

    /**
     * Unique instance of EventManager.
     */
    private static EventManager thismanager;

    /**
     * The EchoProtocol Receiver Thread.
     */
    private final Receiver rcvr = new Receiver();

    /**
     * Creates the single instance of EchoProtocolManager.
     */
    private EventManager() {
        super();
        // Staring the Receiver Thread
        rcvr.start();
    }

    /**
     * Is invoked each time access to the EventManager instance is needed.
     *
     * @return the EventManager instance
     */
    public static EventManager getInstance() {
        synchronized (EventManager.class) {
            // Check if an instance has already been created
            if (thismanager == null) {
                // Create a new instance if not
                Logger.getInstance().debug("EchoProtocol Created");
                thismanager = new EventManager();
            }
        }
        // Return the EventManager instance
        return thismanager;

    }

    /**
     * Notify Observers with the new Event.
     *
     * @param event the new Event Object
     */
    public void addEvent(final Event event) {
        this.notifyObservers(event);
    }
}
