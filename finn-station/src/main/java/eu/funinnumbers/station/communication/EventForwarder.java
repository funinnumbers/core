package eu.funinnumbers.station.communication;

import eu.funinnumbers.db.model.event.Event;

import eu.funinnumbers.engine.rmi.BaseEngineInterface;
import eu.funinnumbers.util.Logger;

/**
 * Listens for events received from the Guardian DTS and forwards them to the Engine
 * via the RMI interface EngineInterface.
 *
 * @see eu.funinnumbers.engine.rmi.FGEngineInterface
 */
public final class EventForwarder {

    /**
     * Unique instance of this class.
     */
    private static EventForwarder instance;

    /**
     * Remote Engine Interface.
     */
    private BaseEngineInterface engineIF;

    /**
     * Default (private) constructor.
     */
    private EventForwarder() {
        // do nothing
    }

    /**
     * Returns the unique instance of the EventForwarder.
     *
     * @return an instance of the EventForwarder
     */
    public static EventForwarder getInstance() {
        synchronized (EventForwarder.class) {
            if (instance == null) {
                instance = new EventForwarder();
            }
        }

        return instance;
    }

    /**
     * Set the Engine RMI Interface.
     *
     * @param eng the RMI Interface of the Engine
     */
    public void setEngineInterface(final BaseEngineInterface eng) {
        engineIF = eng;
    }

    /**
     * Connects to the Coordinator and sends the specified event.
     *
     * @param event The event to send to the coordinator
     */

    public void sendEvent(final Event event) {

        try {

            engineIF.addEvent(event);


        } catch (Exception e) {
            Logger.getInstance().debug("Unable to send event to Engine", e);
        }
    }

    /**
     * Access the EngineInterface assosiated with this eu.funinnumbers.station.
     *
     * @return the EngineInterface of this Stations Engine.
     */
    public BaseEngineInterface getEngineInterface() {
        return engineIF;
    }
}
