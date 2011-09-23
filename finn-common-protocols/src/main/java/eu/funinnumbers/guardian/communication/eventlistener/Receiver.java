package eu.funinnumbers.guardian.communication.eventlistener;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import eu.funinnumbers.db.model.Station;
import eu.funinnumbers.guardian.communication.echoprotocol.EchoProtocolManager;
import eu.funinnumbers.util.Logger;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import java.io.IOException;

/*
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
 */

/**
 * The Receiver thread is constantly listening for incoming messages on port [EVENTPORT].
 */
public class Receiver extends Thread { //NOPMD

    /**
     * The EchoProtocol port.
     */
    public static final int EVENTPORT = 101;


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
     * @see java.lang.Thread#Thread(ThreadGroup, Runnable, String)
     */
    public final void run() { //NOPMD
        //System.setSecurityManager(new RMISecurityManager());
        RadiogramConnection dgConnection;
        Datagram datagram;
        final Station tmpStation = new Station();

        // Get the Echo Protocol instance
        final EchoProtocolManager echo = EchoProtocolManager.getInstance();

        try {
            // Creates a Server Radiogram Connection on port ENENTPORT
            dgConnection = (RadiogramConnection) Connector.open("radiogram://:" + EVENTPORT);

            // Creates a Datagram using the above Connection
            datagram = dgConnection.newDatagram(dgConnection.getMaximumLength());

        } catch (IOException e) {
            Logger.getInstance().debug("Could not open radiogram receiver connection on port " + EVENTPORT, e);
            return;
        }

        while (true) {
            try {
                // Clean the Datagram
                datagram.reset();

                // Receive from Datagram
                dgConnection.receive(datagram);

                // Set the actual address of the sender
                tmpStation.setAddress(datagram.getAddress());

                // if the event comes from the guardiansStation
                if (!datagram.getAddress().equals(echo.getMyAddress())
                        && (echo.getMyStation().getAddress().equals(tmpStation.getAddress()))) {
                    //Event's classType
                    final String classType = datagram.readUTF();

                    Logger.getInstance().debug("Received <" + classType
                            + "> from Station "
                            + tmpStation.getAddress());

                    // Locate Class based on class type name
                    //final Class eventClass = Class.forName(classType);

                    // Get Default Constructor
                    //final Constructor eventConstructor = eventClass.getConstructor(new Class[]{});

                    // Construct new Instance for this event type
                    //final Event actEvent = (Event) eventConstructor.newInstance(new Object[]{});

                    // Extract Byte array length
                    //final int length = datagram.readInt();

                    // Extract byte array
                    //final byte[] eventArray = new byte[length];
                    // datagram.readFully(eventArray, 0, length);

                    // Convert byte array to event object
                    //actEvent.fromByteArray(eventArray);

                    // Send event to EventManager
                    //EventManager.getInstance().addEvent(actEvent);
                }
                /*
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
                */
            } catch (IOException e) {
                Logger.getInstance().debug("No event received", e);
            }
        }
    }

}
