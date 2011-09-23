/**
 *
 * Delay Tolerance Service accepts Events and either sends them to
 * the active Battle Station on port [DTSPORT]
 * or stores them until next connection.
 *
 * DTSManager follows the Singleton Design Pattern.
 */

package eu.funinnumbers.guardian.communication.dtservice;

import com.sun.spot.util.Utils;
import eu.funinnumbers.db.model.StorableEntity;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.guardian.communication.echoprotocol.EchoProtocolManager;
import eu.funinnumbers.guardian.communication.echoprotocol.Station;
import eu.funinnumbers.guardian.storage.StorageService;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observable;
import eu.funinnumbers.util.Observer;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import java.util.Enumeration;
import java.util.Vector;

/**
 * DTSManager observes the EchoProtocolManager.
 * This way DTSManager is aware of the Connection status
 */
public final class DTSManager implements Observer { //NOPMD

    /**
     * The Datagram Port the service is using to transmit events.
     */
    public static final int DTSPORT = 110;

    /**
     * Unique instance of this class.
     */
    private static DTSManager thisDTS;

    /**
     * Connection with nearby eu.funinnumbers.station.
     */
    private DatagramConnection dgConnection = null;

    /**
     * Indicates if a previous attempt to update Station has failed.
     */
    private boolean failedUpdate;

    /**
     * Instance of Event Sender.
     */
    private final EventSender eventSender;
    /**
     * Enable storing events.
     */
    private boolean storageEnabled = true;

    /**
     * Creates a new instance of DTSManager.
     */
    private DTSManager() {
        // Observer EchoProtocol events etc.
        EchoProtocolManager.getInstance().addObserver(this);
        eventSender = new EventSender();
        eventSender.start();
        failedUpdate = false;
    }

    /**
     * getInstance should be used each time access to the DTSManager instance is needed.
     *
     * @return the DTSManager intance
     */
    public static DTSManager getInstance() {
        synchronized (DTSManager.class) {
            // Check if an instance has already been created
            if (thisDTS == null) {
                // Create a new instance if not
                /*debug msg*/
                //Logger.getInstance().debug("DTService is now running");
                thisDTS = new DTSManager();
            }
        }
        // Return the DTSManager instance
        return thisDTS;
    }

    /**
     * Invoked when an <code>Observable</code> object notifies the <code>Observer</code>.
     *
     * @param observable the <code>Observable</code> object which notified the <code>Observer</code>
     * @param arg        the changed object
     */
    public void update(final Observable observable, final Object arg) { //NOPMD

        //       Logger.getInstance().debug("\tDTS received update");
        if (observable instanceof EchoProtocolManager && arg instanceof Station) {
            final Station station = (Station) arg;
            if (station.isActive() && !station.isMobileStation()) {

                failedUpdate = false;
                //emptyEventBuffer();
                eventSender.doNotify();


            } else if (!station.isActive()) {
                setDisconnected();
            }
        }

    }

    /**
     * Tries to send an event to it's recipient.
     * If there is no connection to some eu.funinnumbers.station event is saved in the event buffer.
     *
     * @param event the event to be sent
     */
    public synchronized void sendEvent(final Event event) { //NOPMD
        //Check if event should be sent to a Battle FGStationApp (checking type? id?)

        if (EchoProtocolManager.getInstance().getMyStation().isActive()) {
            try {
                // Creates a broadcast Datagram Connection
                dgConnection = (DatagramConnection) Connector.open("radiogram://"
                        + EchoProtocolManager.getInstance().getMyStation().getAddress().longValue()
                        + ":" + DTSPORT);

                // Creates a Datagram using the above Connection
                final Datagram datagram = dgConnection.newDatagram(dgConnection.getMaximumLength());

                // Clean the Datagram
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
                dgConnection.send(datagram);

                dgConnection.close();

                // There was a succefull update attempt after a failed one.
                if (failedUpdate) {
                    Logger.getInstance().debug("Connection to eu.funinnumbers.station now seems to be OK");
                    failedUpdate = false;

                    // Try to empty events buffer again
                    emptyEventBuffer();
                }
                Logger.getInstance().debug("Event sent succefully");


            } catch (Exception ex) {
                Logger.getInstance().debug("Unable to send event", ex);
                Logger.getInstance().debug("Adding event to storage: " + event.getDescription());
                // Close the open connection
                setDisconnected();

                // The attempt has failed
                failedUpdate = true;

                // Add event back to storage
                StorageService.getInstance().add(event);
                //FinnLogger.getInstance().increaseStoredEvents();

            }

        } else if (storageEnabled) {

            Logger.getInstance().debug("The connection seems to be inactive. Saving to buffer");
            // Add event to the corresponding recordstore
            StorageService.getInstance().add(event);
            //FinnLogger.getInstance().increaseStoredEvents();
        } else {
            Logger.getInstance().debug("Event lost!!!");
        }

    }

    /**
     * Loops through the eventBuffer and sends every entry to Battle Station.
     */
    protected void emptyEventBuffer() { //NOPMD
        /*TODO find an efficient way to empty send multiple *EVENTENTITIES*/

//        time1 = System.currentTimeMillis();
        //Send EVENTENTITIES
        // Read all events from the corresponding recordstore
        Vector eventBuffer = StorageService.getInstance().listEntities(StorableEntity.EVENTENTITY);

        // Clear the event recordstore
        StorageService.getInstance().clear(StorableEntity.EVENTENTITY);
//        time2 = System.currentTimeMillis();
//       Logger.getInstance().debug("\t\t***Events: "+ eventBuffer.size());
//        time2 = time2 - time1;
//       Logger.getInstance().debug("\t\t***Storage time: " + time2);

        // Send each event to the Battle FGStationApp
        Enumeration eve;
        for (eve = eventBuffer.elements(); eve.hasMoreElements();) {

            final Event event = (Event) eve.nextElement();
            // Previous attempt sending the event failed
            // don't try to send next event, add it to storage
            if (failedUpdate) {
                Logger.getInstance().debug("Failed.Adding event back to storage.");
                StorageService.getInstance().add(event);
                //FinnLogger.getInstance().increaseStoredEvents();
                // Send the event normally
            } else {
                sendEvent(event);
                //FinnLogger.getInstance().decreaseStoredEvents();
                final int tmpVar = 5;
                Utils.sleep(tmpVar * (1 + EchoProtocolManager.getInstance().getNeighbours().size()));
                // Logger.getInstance().debug("Updating eu.funinnumbers.station event: " + i + "/" + eventBuffer.size());
            }


        }

        // Sending ACTIONEVENTENTITIES

//        time1 = System.currentTimeMillis();
        // Read all events from the corresponding recordstore
        eventBuffer = StorageService.getInstance().listEntities(StorableEntity.ACTIONEVENTENTITY);

        // Clear the event recordstore
        StorageService.getInstance().clear(StorableEntity.ACTIONEVENTENTITY);

//        time2 = System.currentTimeMillis();
//      Logger.getInstance().debug("\t\t***Events: "+ eventBuffer.size());
//        time2 = time2 - time1;
//       Logger.getInstance().debug("\t\t***Storage time: " + time2);

        // Send each event to the Battle FGStationApp
        Enumeration act;
        for (act = eventBuffer.elements(); act.hasMoreElements();) {

            final Event event = (Event) act.nextElement();

            // Previous attempt sending the event failed
            // don't try to send next event, add it to storage
            if (failedUpdate) {
                Logger.getInstance().debug("Failed.Adding event back to storage.");
                StorageService.getInstance().add(event);
                //FinnLogger.getInstance().increaseStoredEvents();
                // Send the event normally
            } else {

                sendEvent(event);
                //FinnLogger.getInstance().decreaseStoredEvents();
                // Variable rate
                final int tmpVar = 5;
                Utils.sleep(tmpVar * (1 + EchoProtocolManager.getInstance().getNeighbours().size()));
                //Logger.getInstance().debug("Updating eu.funinnumbers.station event: " + i + "/" + eventBuffer.size());
            }


        }


    }

    /**
     * The connection to the Battle Station was lost.
     */
    private void setDisconnected() {
        // Close a possible connection to the Battle FGStationApp
        try {
            if (dgConnection != null
                    && !EchoProtocolManager.getInstance().getMyStation().isActive()) {
                dgConnection.close();
            }
        } catch (Exception ex) {
            Logger.getInstance().debug("Unable to disconnect", ex);
        }
    }

    /**
     * Enables or disables event storage.
     *
     * @param active true or false
     */
    public void setStorageEnabled(final boolean active) {
        storageEnabled = active;

    }

    public boolean sendEventSync(final Event event) {
        boolean succesfull = false;
        if (EchoProtocolManager.getInstance().getMyStation().isActive()) {
            try {
                // Creates a broadcast Datagram Connection
                dgConnection = (DatagramConnection) Connector.open("radiogram://"
                        + EchoProtocolManager.getInstance().getMyStation().getAddress().longValue()
                        + ":" + DTSPORT);

                // Creates a Datagram using the above Connection
                final Datagram datagram = dgConnection.newDatagram(dgConnection.getMaximumLength());

                // Clean the Datagram
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
                dgConnection.send(datagram);

                dgConnection.close();

                Logger.getInstance().debug("Event sent succefully");
                succesfull = true;


            } catch (Exception ex) {
                Logger.getInstance().debug("Unable to send event", ex);

                // Close the open connection
                try {
                    dgConnection.close();
                } catch (Exception e) {
                    //eat it
                }

                succesfull = false;
            }
        }
        return succesfull;
    }
}
