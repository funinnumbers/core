package eu.funinnumbers.station.rmi;

import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.util.eventconsumer.EventConsumer;
import eu.funinnumbers.util.Logger;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Station RMI Implementation.
 */
public class StationRMIImpl extends UnicastRemoteObject implements StationInterface, Serializable {

    /**
     * Version for Serialization.
     */
    static final long serialVersionUID = 42L;

    /**
     * Default Constructor.
     *
     * @throws RemoteException in case of RMI error
     */
    public StationRMIImpl() throws RemoteException {
        super();
        // Do nothing
    }

    /**
     * This is the remote method which is called by the Engine to deliver new events.
     *
     * @param event The new Event
     * @throws RemoteException in case of an RMI error
     */
    public void addEvent(final Event event) throws RemoteException {
        Logger.getInstance().debug("Event Received from Engine");
        EventConsumer.getInstance().addEvent(event);
    }

    /**
     * Completes eu.funinnumbers.station registration.
     *
     * @throws RemoteException in case of an RMI erro
     */
    public void completeRegistration() throws RemoteException {
        Logger.getInstance().debug("Registration with Engine completed.");
    }

}
