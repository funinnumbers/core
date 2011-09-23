package eu.funinnumbers.hyperengine.rmi;

import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.util.eventconsumer.EventConsumer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * HyperEngine RMI implementation.
 */
public class HyperEngineRMIImpl extends UnicastRemoteObject implements HyperEngineInterface {

    /**
     * Version for Serialization.
     */
    static final long serialVersionUID = 47382L;

    /**
     * Default Constructor.
     *
     * @throws java.rmi.RemoteException if RMI is unable to connect
     */
    public HyperEngineRMIImpl() throws RemoteException {
        super();
    }


    /**
     * Method used from Engines to send events to HyperEngine.
     *
     * @param eventFromEngine the received event from the eu.funinnumbers.engine.
     * @throws RemoteException thrown when method cannot be invoked.
     */
    public void addEvent(final Event eventFromEngine) {
        EventConsumer.getInstance().addEvent(eventFromEngine);

    }
}
