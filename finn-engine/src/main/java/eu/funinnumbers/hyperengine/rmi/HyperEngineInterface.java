package eu.funinnumbers.hyperengine.rmi;

import eu.funinnumbers.db.model.event.Event;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI interface for the HyperEngine.
 */
public interface HyperEngineInterface extends Remote {

    /**
     * The name of the RMI class.
     */
    String RMI_NAME = "HyperEngine";

    /**
     * Method used from Engines to send events to HyperEngine.
     *
     * @param eventFromEngine the received event from the eu.funinnumbers.engine.
     * @throws RemoteException thrown when method cannot be invoked.
     */
    void addEvent(Event eventFromEngine)  throws RemoteException;

}
