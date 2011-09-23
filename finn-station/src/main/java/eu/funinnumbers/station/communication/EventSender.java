package eu.funinnumbers.station.communication; //NOPMD

import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.guardian.communication.echoprotocol.EchoProtocolManager;
import eu.funinnumbers.guardian.communication.eventlistener.Receiver;
import eu.funinnumbers.util.Logger;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

/**
 * Transmints Event objects to nearby guardians.
 */
public class EventSender { //NOPMD

    /**
     * Broadcasts an Event to all connected guardians.
     *
     * @param event the event object to broadcast.
     */
    public static void sendEvent(final Event event) {

        DatagramConnection dgConnection;

        try {
            // Creates a broadcast Datagram Connection
            final int guardianID = event.getGuardianID();
            final Guardian guardian = getGuardianByID(guardianID);
            if (guardian == null) {
                throw new IOException();
            }

            final Long guardianAddress = getGuardianByID(guardianID).getAddress();

            Logger.getInstance().debug("I should send " + event.getDescription()
                    + " to " + guardianAddress);

            dgConnection = (DatagramConnection) Connector.open("radiogram://"
                    + guardianAddress
                    + ":" + eu.funinnumbers.guardian.communication.eventlistener.Receiver.EVENTPORT);

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

            //Logger.getInstance().debug("Event Sent");

        } catch (IOException ex) {
            Logger.getInstance().debug("Unable to send event", ex);
        }
    }

    /**
     * Retrieve a specific eu.funinnumbers.guardian based on its ID.
     *
     * @param guardianID the guardian ID
     * @return the Object retrieved based on ID lookup.
     */
    private static Guardian getGuardianByID(final int guardianID) {
        final Vector guardians = EchoProtocolManager.getInstance().getNeighbours();
        final Iterator iter = guardians.iterator();
        while (iter.hasNext()) {
            final Guardian guardian = (Guardian) iter.next();
            if (guardian.getID() == guardianID) {
                return guardian;
            }
        }
        return null;
    }
}
