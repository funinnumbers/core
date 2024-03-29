package eu.funinnumbers.engine.event;

import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observable;
import eu.funinnumbers.util.Observer;
import eu.funinnumbers.util.eventconsumer.EventConsumer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


/**
 * Puprose of the Coordinator is to synchronize events delivered to the Engine.
 */
public class Coordinator extends Observable implements Observer { //NOPMD

    /**
     * The event id of the motion event.
     */
    public static final int MOTIONEVENT_ID = -101;

    /**
     * The IDs of the Guardians associated with a counter.
     * (needed for future look-ups)
     */
    private final HashMap<Integer, Integer> guardianMap = new HashMap<Integer, Integer>();

    /**
     * A counter for all events sent to the EngineInterface.
     */
    private int eventCounter;

    /**
     * A buffer that stores events that cannot be processed at this time instance because there are other
     * events still missing (clock gap).
     */
    private final HashSet<Event> eventBuffer = new HashSet<Event>();

    /**
     * The constuctor of the class.
     *
     * @param guardians collection with all guardians
     */
    public Coordinator(final Collection guardians) {
        Logger.getInstance().debug("Coordinator: Initializing... ");

        // Construct HashMap for Stations indexed by IP
        final Iterator iterGuardians = guardians.iterator();
        while (iterGuardians.hasNext()) {
            final Guardian guardian = (Guardian) iterGuardians.next();
            guardianMap.put(guardian.getID(), 0);
            Logger.getInstance().debug("Guardian ID=" + guardian.getID()
                    + ", MAC=" + guardian.getAddress()
                    + ", LEDID=" + guardian.getLedId());
        }
    }

    /**
     * Get the counter of a eu.funinnumbers.guardian.
     *
     * @param guardianID The id of the eu.funinnumbers.guardian
     * @return The event counter for the particular eu.funinnumbers.guardian
     */
    private int getCounter(final int guardianID) {
        return guardianMap.get(guardianID);
    }

    /**
     * Increment the counter of a eu.funinnumbers.guardian.
     *
     * @param guardianID The id of the eu.funinnumbers.guardian
     */
    private void incCounter(final int guardianID) {
        final int counter = getCounter(guardianID);
        guardianMap.put(guardianID, counter + 1);
    }

    /**
     * Check if the event is a legal continuation of the current situation.
     *
     * @param event The event to be checked
     * @return The value 0 if the event is valid; the value -1 if the event has already been received;
     *         the value 1 if the event is not yet valid
     */
    private int checkEvent(final Event event) {
        // Check if Guardian ID is known
        if (!guardianMap.containsKey(event.getGuardianID())) {
            Logger.getInstance().debug("\t\tNo such eu.funinnumbers.guardian");
            return -1;
        }

/* TODO check this Super by-passing! 
        // Check if this is an Event generated by the Station
        if (event.getGuardianCounter() == MOTIONEVENT_ID) {
            return 0;
        }

        // Locate current counter for particular eu.funinnumbers.guardian
        final int curCount = getCounter(event.getGuardianID());

        // Check if event is old
        if (event.getGuardianCounter() <= curCount) {
            Logger.getInstance().debug("\t\tOld event");
            return -1;
        }

        // Check if event is in the future
        if (event.getGuardianCounter() > curCount + 1) {
            return 1;
        }
*/

        return 0;
    }

    /**
     * Process a new event.
     *
     * @param event The event to be processed
     */
    private void processEvent(final Event event) { //NOPMD
        final int result = checkEvent(event);
        // Logger.getInstance().debug("result=" + result);
        //result = 0;
        if (result == 0) {
            /**
             * if the event is valid, remove it (in case it exists) from the buffer,
             * update the values of the counters and send it to the EngineRMIImpl.
             * Also check the buffer for events that may now be legal
             */
            removeFromBuffer(event);

            // Check if this is an Event generated by the Station -- if not increase the counter
            if (event.getGuardianCounter() != MOTIONEVENT_ID) {
                incCounter(event.getGuardianID());
            }

            /*    Logger.getInstance().debug("Coordinator: "
    + event.getClass().getName() + " submitted --> " + event.toString());*/
            submitEvent(event);
            checkBuffer();

        } else if (result == -1) {
            // if we have already received the event, do nothing
            Logger.getInstance().debug("Coordinator: "
                    + event.getClass().getName() + " dumped --> " + event.toString());

        } else if (result == 1) {
            // if the event cannot yet be sent, store it in the buffer
            addToBuffer(event);
        }
    }

    /**
     * Add an event to the buffer, after checking first if the event already exists.
     *
     * @param event The event to be stored
     */
    private void addToBuffer(final Event event) {
        //if event is already in buffer don't add it
        if (!eventBuffer.contains(event)) {
            // Add to buffer
            eventBuffer.add(event);
            Logger.getInstance().debug("Added to buffer. Current size: "
                    + eventBuffer.size()
                    + " --> "
                    + event.toString());
        }
    }

    /**
     * Remove an event from the buffer, if it exists.
     *
     * @param event The event to be removed
     */
    private void removeFromBuffer(final Event event) {

        //check if event is in buffer
        if (eventBuffer.contains(event)) {
            // remove from buffer
            eventBuffer.remove(event);
            //Logger.getInstance().debug("Removed from buffer. Current size: " + eventBuffer.size());
        }
    }

    /**
     * Check the buffer for events that might be valid.
     */
    private void checkBuffer() {
        //Logger.getInstance().debug("Checking buffer..");
        Iterator<Event> iter = eventBuffer.iterator();
        while (iter.hasNext()) {
            final Event event = iter.next();
            final int result = checkEvent(event);
            if (result == 0) {
                // if the event is valid, remove it from the buffer, update the values of the counters
                // and send it to the FGEngineInterface. Then continue searching from the top again.
                removeFromBuffer(event);
                incCounter(event.getGuardianID());
                submitEvent(event);
                //Logger.getInstance().debug("Buffer: Event submitted --> restarting check");
                iter = eventBuffer.iterator();
            }
        }
    }

    /**
     * This is the remote method which is called by the BattleStations to add new events.
     *
     * @param event The new BattleEvent
     */
    public final void addEvent(final Event event) {
        processEvent(event);
    }

    /**
     * Submit an Event to the Engine.
     *
     * @param event The event to be submitted
     */
    private void submitEvent(final Event event) {
        event.setID(eventCounter++);
        setChanged();
        notifyObservers(event);
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

        if (obs instanceof EventConsumer && arg instanceof Event) {
            addEvent((Event) arg);
        }


    }
}
