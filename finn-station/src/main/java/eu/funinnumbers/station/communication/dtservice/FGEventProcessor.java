package eu.funinnumbers.station.communication.dtservice;

import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.station.communication.EventForwarder;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observable;
import eu.funinnumbers.util.Observer;

import javax.microedition.io.Datagram;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;

import eu.funinnumbers.station.FileFinnLogger;

/**
 * Process Datagrams and construct Events for First Generation Games.
 */
public class FGEventProcessor implements Observer { //NOPMD

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     *
     * @param guardians a collection of Guardian objects
     */
    public FGEventProcessor(final Collection<Guardian> guardians) {
        super();

        // Initialize the Map
        guardiansMap = new HashMap<Long, Guardian>();

        // Associates every eu.funinnumbers.guardian based on its MAC Address
        for (Guardian guardian : guardians) {
            guardiansMap.put(guardian.getAddress(), guardian);
        }
    }

    /**
     * A Map of Guardians by MAC address.
     */
    private final HashMap<Long, Guardian> guardiansMap;

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
    public void update(final Observable obs, final Object arg) { //NOPMD
        // only interested in DTSManager
        if (!(obs instanceof DTSManager)) {
            return;
        }

        // Only interested for Datagram objects
        if (!(arg instanceof Datagram)) {
            return;
        }

        try {
            // Converts the object to proper type
            final Datagram datagram = (Datagram) arg;

            //Event's classType
            final String classType = datagram.readUTF();

            //Locate Class based on class type name
            final Class eventClass = Class.forName(classType);

            //Get Default Constructor
            @SuppressWarnings("unchecked")
            final Constructor eventConstructor = eventClass.getConstructor(new Class[]{});

            //Construct new Instance for this event type
            final Event actEvent = (Event) eventConstructor.newInstance(new Object[]{});

            //Extract Byte array length
            final int length = datagram.readInt();

            //Extract byte array
            final byte[] eventArray = new byte[length];
            datagram.readFully(eventArray, 0, length);

            //Convert byte array to event object
            actEvent.fromByteArray(eventArray);

            Logger.getInstance().debug(actEvent.getDebugInfo());

            //Check if eu.funinnumbers.guardian is inside the list of guardians participating the game
            /*WARNING! remove true when logger is inactive*/
            if (true || guardiansMap.containsKey(datagram.getAddress())) {
                /********************************************/
                /*         FINNLOGGER STUFF                */
                /********************************************/
                if (actEvent.getType().equals("LOG")) {
                    FileFinnLogger.getInstance().writeToFile(datagram.getAddress(), actEvent.getDescription());
                } else if (actEvent.getType().equals("CPULOG")) {
                    FileFinnLogger.getInstance().writeToFile(datagram.getAddress() + "CPU", actEvent.getDescription());

                    /********************************************/
                    /*      END OF FINNLOGGER STUFF             */
                    /********************************************/

                } else {
                    // Send event to Coordinator
                    EventForwarder.getInstance().sendEvent(actEvent);
                }
            } // FINNLOGGER 
        } catch (IllegalAccessException ex) {
            Logger.getInstance().debug("Cannot construct new event", ex);

        } catch (InvocationTargetException ex) {
            Logger.getInstance().debug("Cannot construct new event", ex);

        } catch (InstantiationException ex) {
            Logger.getInstance().debug("Cannot construct new event", ex);

        } catch (NoSuchMethodException ex) {
            Logger.getInstance().debug("Cannot locate constructor for new event", ex);

        } catch (ClassNotFoundException ex) {
            Logger.getInstance().debug("Unknown event type received", ex);

        } catch (IOException e) {
            Logger.getInstance().debug("No event received", e);

        }

    }
}


