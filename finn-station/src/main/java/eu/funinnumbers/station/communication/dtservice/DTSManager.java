package eu.funinnumbers.station.communication.dtservice;

import eu.funinnumbers.util.Observable;

import javax.microedition.io.Datagram;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Observes the DTS for incomming events.
 * <p/>
 * Forwards received Events to EventBuffer
 *
 * @author ichatz
 * @see eu.funinnumbers.station.communication.EventForwarder
 */
public class DTSManager extends Observable { //NOPMD

    /**
     * The Guardians DTSPORT.
     */
    public static final int EVENTPORT = 110;

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static DTSManager ourInstance = null;

    /**
     * The receiver thread.
     */
    private final Receiver thisReceiver;

    /**
     * The distributer thread.
     */
    private final Distributer thisDistributer;

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private DTSManager() {
        super();

        // The Blocking Queue for datagrams (containing events) received from guardians.
        final BlockingQueue<Datagram> datagramBuffer = new LinkedBlockingQueue<Datagram>();

        // Initialize Receiver
        thisReceiver = new Receiver(datagramBuffer);
        thisReceiver.start();

        // Initialize Distributer
        thisDistributer = new Distributer(datagramBuffer);
        thisDistributer.start();
    }

    /**
     * EventReceiver is loaded on the first execution of EventProcessor.getInstance()
     * or the first access to EventProcessor.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static DTSManager getInstance() {
        synchronized (DTSManager.class) {
            if (ourInstance == null) {
                ourInstance = new DTSManager();
            }
        }

        return ourInstance;
    }

    /**
     * Stop all threads.
     */
    public void kill() {
        thisReceiver.kill();
        thisDistributer.kill();
    }

}

