package eu.funinnumbers.station.communication.dtservice;


import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import eu.funinnumbers.util.Logger;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Continuously listen for incoming datagrams and send to event buffer.
 */
public class Receiver extends Thread {

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
    public Receiver(final BlockingQueue<Datagram> queue) {
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
     * @see Thread#Thread(ThreadGroup, Runnable, String)
     */
    public final void run() {
        RadiogramConnection dgConnection;

        try {
            // Creates a Server Radiogram Connection on port EVENTPORT
            dgConnection = (RadiogramConnection) Connector.open("radiogram://:" + DTSManager.EVENTPORT);

        } catch (IOException e) {
            Logger.getInstance().debug("Could not open radiogram receiver connection on port "
                    + DTSManager.EVENTPORT, e);
            return;
        }

        while (enabled) {
            try {
                // Creates a Datagram using the above Connection
                final Datagram datagram = dgConnection.newDatagram(dgConnection.getMaximumLength());

                // Clean the Datagram
                datagram.reset();

                // Receive from Datagram
                dgConnection.receive(datagram);

                // Insert the datagram to buffer
                datagramBuffer.offer(datagram);

            } catch (IOException e) {
                Logger.getInstance().debug("No event received", e);
            }
        }
    }
}

