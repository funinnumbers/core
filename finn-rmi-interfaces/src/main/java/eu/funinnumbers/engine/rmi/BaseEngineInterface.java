package eu.funinnumbers.engine.rmi;

import eu.funinnumbers.db.model.event.Event;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Base methods implemented in both First and Second generation Engines.
 */
public interface BaseEngineInterface extends Remote {

    /**
     * This is the remote method which is called by the Stations to send new events to the Engine.
     *
     * @param event The new Event
     * @throws RemoteException if RMI was unable to invoke method
     */
    void addEvent(Event event) throws RemoteException;

}
