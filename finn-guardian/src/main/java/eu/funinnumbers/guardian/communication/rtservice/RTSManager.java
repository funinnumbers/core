/**
 *
 * Delay Tolerance Service accepts Events and either sends them to
 * the active Battle Station on port [DTSPORT]
 * or stores them until next connection.
 *
 * DTSManager follows the Singleton Design Pattern.
 */

package eu.funinnumbers.guardian.communication.rtservice;

import com.sun.spot.util.IEEEAddress;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.guardian.communication.echoprotocol.EchoProtocolManager;
import eu.funinnumbers.guardian.communication.echoprotocol.Station;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observable;
import eu.funinnumbers.util.Observer;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import java.io.IOException;

/**
 * DTSManager observes the EchoProtocolManager.
 * This way DTSManager is aware of the Connection status
 */
public final class RTSManager implements Observer { //NOPMD

    /**
     * The Datagram Port the service is using to transmit events.
     */
    public static final int RTSPORT = 110;

    /**
     * Unique instance of this class.
     */
    private static RTSManager thisRTS;

    /**
     * Connection with nearby eu.funinnumbers.station.
     */
    private DatagramConnection connection = null;

    Datagram datagram;

    private String sink = null;

    /**
     * Creates a new instance of DTSManager.
     */
    private RTSManager() {
        // Observer EchoProtocol events etc.
        EchoProtocolManager.getInstance().addObserver(this);

        if (EchoProtocolManager.getInstance().getMyStation().isActive()) {
            openConnection(IEEEAddress.toDottedHex(EchoProtocolManager.getInstance().getMyStation().getAddress().longValue()));

        }


    }

    /**
     * getInstance should be used each time access to the DTSManager instance is needed.
     *
     * @return the DTSManager intance
     */
    public static RTSManager getInstance() {
        synchronized (RTSManager.class) {
            // Check if an instance has already been created
            if (thisRTS == null) {
                // Create a new instance if not
                /*debug msg*/
                //Logger.getInstance().debug("DTService is now running");
                thisRTS = new RTSManager();
            }
        }
        // Return the DTSManager instance
        return thisRTS;
    }

    /**
     * Invoked when an <code>Observable</code> object notifies the <code>Observer</code>.
     *
     * @param observable the <code>Observable</code> object which notified the <code>Observer</code>
     * @param arg        the changed object
     */
    public void update(final Observable observable, final Object arg) { //NOPMD

        if (observable instanceof EchoProtocolManager && arg instanceof Station) {
            final Station station = (Station) arg;
            if (station.isActive() && !station.isMobileStation()) {
                openConnection(IEEEAddress.toDottedHex(station.getAddress().longValue()));

            } else if (!station.isActive()) {
                closeConnection();
            }
        }

    }

    /**
     * Open a datagram connection to some sink.
     *
     * @param stationMac the mac address of the eu.funinnumbers.station.
     */
    private void openConnection(final String stationMac) {
        sink = stationMac;
        try {
            connection =
                    (DatagramConnection) Connector.open("radiogram://" + sink + ":" + RTSPORT);
            datagram = connection.newDatagram(connection.getMaximumLength());

        } catch (IOException e) {
            Logger.getInstance().debug("Failed to open connection " + e);
        }
    }

    /**
     * Closes the Connection to the sink.
     */
    private void closeConnection() {
        sink = null;
        try {
            connection.close();
        } catch (IOException e) {
            Logger.getInstance().debug("Failed to close connection " + e);
        }

    }

    /**
     * Tries to send an event to the sink.
     *
     * @param event the event to be sent
     */
    public synchronized void send(final Event event) { //NOPMD
        Logger.getInstance().debug("Trying to send to " + sink);
        try {
            if (sink != null) {
                Logger.getInstance().debug(" Sending to " + sink);
                datagram.reset();

                // Convert event to byte Array
                final byte[] eventArray = event.toByteArray();

                // Send Class Type
                datagram.writeUTF(event.getClass().getName());

                // Send length
                datagram.writeInt(eventArray.length);

                // Send array
                datagram.write(eventArray, 0, eventArray.length);

                // Send the datagram
                connection.send(datagram);
            }

        } catch (IOException e) {
            Logger.getInstance().debug("Failed to send " + e);
            closeConnection();
        }
    }

    /**
     * Sets the mac address of the sink.
     *
     * @param newSink the mac of the new sink.
     */
    public void setSink(final String newSink) {
        sink = newSink;

    }


}
