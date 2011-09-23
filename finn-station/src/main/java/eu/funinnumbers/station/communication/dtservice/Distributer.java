package eu.funinnumbers.station.communication.dtservice;

import eu.funinnumbers.util.Logger;

import javax.microedition.io.Datagram;
import java.util.concurrent.BlockingQueue;

/**
 * Continuously monitor event buffer and send to observers.
 */
public class Distributer extends Thread {

    /**
     * Signify if Receiver thread is enabled.
     */
    private boolean enabled = true;

    /**
     * The Blocking Queue for datagrams (containing events) received from guardians.
     */
    private final BlockingQueue<Datagram> datagramBuffer;

    /**
     * Default Constructor.
     *
     * @param queue the event queue
     */
    public Distributer(final BlockingQueue<Datagram> queue) {
        super();

        datagramBuffer = queue;
    }

    /**
     * Terminate the tread.
     */
    public void kill() {
        enabled = false;
    }

    /**
     * If this thread was constructed using a separate
     * <code>Runnable</code> run object, then that
     * <code>Runnable</code> object's <code>run</code> method is called;
     * otherwise, this method does nothing and returns.
     * <p/>
     * Subclasses of <code>Thread</code> should override this method.
     *
     * @see #start()
     * @see #stop()
     * @see #Thread(ThreadGroup, Runnable, String)
     */
    public void run() {

        while (enabled) {

            try {
                // Retrieves and removes the head of this buffer
                final Datagram datagram = datagramBuffer.take();

                // Update all observers
                DTSManager.getInstance().setChanged();
                DTSManager.getInstance().notifyObservers(datagram);

            } catch (InterruptedException e) {
                Logger.getInstance().debug("Cannot pop datagram from Queue", e);
            }

        }
    }

}
