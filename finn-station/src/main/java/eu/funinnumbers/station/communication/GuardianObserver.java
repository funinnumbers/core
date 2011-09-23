package eu.funinnumbers.station.communication;

import com.sun.spot.peripheral.radio.RadioFactory;
import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.Station;
import eu.funinnumbers.db.model.event.MotionEvent;

import eu.funinnumbers.guardian.communication.echoprotocol.EchoProtocolManager;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observable;
import eu.funinnumbers.util.Observer;

import java.util.Vector;

/**
 * Observes the EchoProtocol for incomming/outgoing guardians.
 * <p/>
 * Maintains an internal collection of the guardians that are connected to this Station.
 *
 * @author marlog
 */
public class GuardianObserver implements Observer { //NOPMD

    /**
     * The event id of the motion event.
     */
    public static final int MOTIONEVENT_ID = -101;

    /**
     * A vector containing all the Guardians connected to this eu.funinnumbers.station.
     */
    private final Vector<Guardian> connGuardians = new Vector<Guardian>(); // NOPMD

    /**
     * The information stored in the DB for this eu.funinnumbers.station.
     */
    private final Station station;

    /**
     * Creates a new instance of GuardianObserver.
     *
     * @param stat the object describing this Station
     */
    public GuardianObserver(final Station stat) {
        station = stat;

        /**
         * Initialize Echo Protocol and start broadcasting beacons and listening.
         */
        EchoProtocolManager.getInstance().setStation(new eu.funinnumbers.guardian.communication.echoprotocol.Station(station));
        EchoProtocolManager.getInstance().setMode(true);
        EchoProtocolManager.getInstance().addObserver(this);
        EchoProtocolManager.getInstance().setAddress(new Long(RadioFactory.getRadioPolicyManager().getIEEEAddress()));
    }

    /**
     * Returns the connected guardians.
     *
     * @return a vector with all the guardians connected to this eu.funinnumbers.station
     */
    public final Vector getConnGuardians() {
        return connGuardians;
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param obj the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     *            method.
     */
    public void update(final Observable obj, final Object arg) { //NOPMD
        // if this is an updates from EchoProtocolManager or GuardianInitializer
        if (!(obj instanceof EchoProtocolManager) && !(obj instanceof GuardianInitializer)) {
            return;
        }

        // If we just heard a beacon from a nearby eu.funinnumbers.station -- ignore it
        if (!(arg instanceof Guardian)) {
            return;
        }

        // So this is a neighbouring spot
        final Guardian guardian = (Guardian) arg;

        // Make sure that the eu.funinnumbers.guardian is properly initialized
        if (guardian.getInitPhase() != Guardian.INIT_COMPLETE) {
            return;
        }

        // Check if this is a SPOT entering our transmission range
        if (guardian.isAlive()) {
            // Check if this SPOT is connected to us
            if (guardian.getStation().equals(EchoProtocolManager.getInstance().getMyAddress())) {
                Logger.getInstance().debug("MotionEvent: SPOT just connected addr=" + guardian.getAddress());

                // Send a BattleEvent to the Coordinator, informing it for the new arrival
                final MotionEvent event = new MotionEvent(guardian.getID(),
                        MOTIONEVENT_ID,
                        guardian.getAddress().toString(),
                        guardian.getLastAlive());
                event.setIsLeaving(false);
                event.setStation(station);
                connGuardians.addElement(guardian);

                // Send event to Battle EngineRMIImpl
                EventForwarder.getInstance().sendEvent(event);
            }

        } else {
            // This is a SPOT that has left our transmission range
            // and EchoProtocol has decided to remove it from its list
            // (after EchoProtocolManager.BEACON ms)

            // Check if this SPOT was connected to us
            if (guardian.getStation().equals(EchoProtocolManager.getInstance().getMyAddress())) {
                Logger.getInstance().debug("MotionEvent: SPOT just DISconnected addr=" + guardian.getAddress());

                // Send a BattleEvent to the Coordinator, informing it for the new arrival
                final MotionEvent event = new MotionEvent(guardian.getID(),
                        MOTIONEVENT_ID,
                        guardian.getAddress().toString(),
                        guardian.getLastAlive());
                event.setIsLeaving(true);
                event.setStation(station);
                connGuardians.removeElement(guardian);

                // Send event to Battle EngineRMIImpl
                EventForwarder.getInstance().sendEvent(event);
            }

        }
    }
}

