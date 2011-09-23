package eu.funinnumbers.station.communication.dtservice;

import com.sun.spot.peripheral.Spot;
import eu.funinnumbers.db.model.Station;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.engine.rmi.SGEngineInterface;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observable;
import eu.funinnumbers.util.Observer;

import javax.microedition.io.Datagram;
import java.lang.reflect.Constructor;

/**
 * Process Datagrams and construct Events for Second Generation Games.
 */
public class SGEventProcessor implements Observer {

    /**
     * Engine RMI Interface.
     */
    private final SGEngineInterface engine;

    /**
     * EventProcessor Constructor.
     *
     * @param engineIF The Engine RMI Interface
     */
    public SGEventProcessor(final SGEngineInterface engineIF) {
        this.engine = engineIF;
    }


    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param obs the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     *            method.
     */
    public void update(final Observable obs, final Object arg) {
        // only interested in DTSManager
        if (!(obs instanceof DTSManager)) {
            return;
        }

        // If Datagram object
        if (arg instanceof Datagram) {
            try {
                // Converts the object to proper type
                final Datagram datagram = (Datagram) arg;

                //Event's classType
                final String classType = datagram.readUTF();

                //Locate Class based on class type name
                final Class eventClass = Class.forName(classType);

                // Get Default Constructor
                @SuppressWarnings("unchecked")
                final Constructor eventConstructor = eventClass.getConstructor(new Class[]{});

                // Construct new Instance for this event type
                final Event event = (Event) eventConstructor.newInstance(new Object[]{});

                // Extract Byte array length
                final int length = datagram.readInt();

                // Extract byte array
                final byte[] eventArray = new byte[length];
                datagram.readFully(eventArray, 0, length);

                // Convert byte array to event object
                event.fromByteArray(eventArray);

                //Add eu.funinnumbers.station's information to event
                final Station station = new Station();
                station.setIpAddr(System.getProperty("java.rmi.server.hostname"));
                station.setStationId((int) Spot.getInstance().getRadioPolicyManager().getIEEEAddress());
                event.setStation(station);
                event.setSourceAddress(datagram.getAddress());

                //Debug message
                Logger.getInstance().debug(event.getDebugInfo());

                // fwd to the eu.funinnumbers.engine
                engine.addEvent(event);

            } catch (Exception ex) {
                Logger.getInstance().debug("Cannot forward new event", ex);
            }
        }
    }

}
