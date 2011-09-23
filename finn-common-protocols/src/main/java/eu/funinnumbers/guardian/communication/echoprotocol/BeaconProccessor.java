package eu.funinnumbers.guardian.communication.echoprotocol;

import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.guardian.util.HashMap;
import eu.funinnumbers.util.Logger;

import java.util.Enumeration;


/**
 * A Thread responsible for the proccess of receieved beacons.
 */
public class BeaconProccessor extends Thread { //NOPMD
    /**
     * Contains all eu.funinnumbers.guardian devices from which a beacon has been received.
     */
    private final HashMap stationProcQueue = new HashMap();

    /**
     * Contains all eu.funinnumbers.station devices from which a beacon has been received.
     */
    private final HashMap guardianProcQueue = new HashMap();

    /**
     * Overriding Thread's run.
     */
    public void run() { //NOPMD

        // Always check the Vectors.
        while (true) {

            if (!guardianProcQueue.isEmpty()) {
                // Create an enumerator for this pass
                //final Enumeration enumeration = guardianProcQueue.elements();
                final Enumeration enumer = guardianProcQueue.getKeys();
                Logger.getInstance().debug("proccessing " + guardianProcQueue.size() + " messages");
                while (enumer.hasMoreElements()) {
                    // Get the element
                    final Guardian procGuardian = (Guardian) guardianProcQueue.get(enumer.nextElement());

                    // Proccess element
                    // TODO move the update method inside this class
                    EchoProtocolManager.getInstance().updateNeighbour(procGuardian);

                    // Remove element from the Vector
                    //synchronized (this){
                    guardianProcQueue.remove(procGuardian.getAddress());
                    //}
                }

            }
            if (!stationProcQueue.isEmpty()) {
                final Enumeration enumer = stationProcQueue.getKeys();
                int counter = 0;
                Logger.getInstance().debug(" BEACONPROC: Processing " + ++counter + " station");
                while (enumer.hasMoreElements()) {
                    // Get the element
                    final Station procStation = (Station) stationProcQueue.get(enumer.nextElement());

                    // Proccess element
                    // TODO move the update method inside this class
                    EchoProtocolManager.getInstance().updateStation(procStation);

                    // Remove element from the Vector
                    //synchronized (this){
                    guardianProcQueue.remove(procStation);
                    //}
                }

            }

        }


    }

    /**
     * Adds a eu.funinnumbers.guardian to the queue. The beacon of the device will be proccessed later on.
     *
     * @param guardian the eu.funinnumbers.guardian to be added
     */
    public void addToGuardianQueue(final Guardian guardian) {
        // Add the element to the queue
        //synchronized (this){
        guardianProcQueue.put(guardian.getAddress(), guardian);
        //}

    }

    /**
     * Adds a eu.funinnumbers.station to the queue. The beacon of the device will be proccessed later on.
     *
     * @param station the eu.funinnumbers.station to be added
     */
    public void addToStationQueue(final Station station) {
        // Add the elemtent to the queue

        stationProcQueue.put(station.getAddress(), station);
    }

}
