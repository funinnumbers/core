package eu.funinnumbers.guardian.communication.actionprotocol;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import eu.funinnumbers.guardian.ui.misc.NetworkConnectivity;
import eu.funinnumbers.util.Logger;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;

/**
 * the receiver's end of action protocol.
 */
public class Receiver extends Thread { //NOPMD

    /**
     * from NetworkConnectivity : Needed for NetworkActivity blinking.
     */
    private static final int NETWORK_LED = NetworkConnectivity.getNetworkLED(); //NOPMD

    /**
     * from NetworkConnectivity : Needed for NetworkActivity blinking.
     */
    private static final int TIMES_TO_BLINK = NetworkConnectivity.getTimesBlinkingLED(); //NOPMD

    /**
     * from NetworkConnectivity : Needed for NetworkActivity blinking.
     */
    private static final int BLINK_INTERVAL = NetworkConnectivity.getBlinkInterval(); //NOPMD

    /**
     * this method initiates the application.
     */
    public void run() { //NOPMD

        RadiogramConnection dataConn = null;
        Datagram datagram = null;

        try {
            // Creates a Server Radiogram Connection on port 120
            dataConn = (RadiogramConnection) Connector.open("radiogram://:120");
            // Creates a Datagram using the above Connection
            datagram = dataConn.newDatagram(dataConn.getMaximumLength());

        } catch (Exception e) {
            Logger.getInstance().debug("Could not open radiogram receiver connection");
            Logger.getInstance().debug(e);
            return;
        }
        Logger.getInstance().debug("Listening for action protocol transmissions");
        while (true) {
            try {
                // Clean the Datagram
                datagram.reset();

                // Receive from Datagram
                dataConn.receive(datagram);

                // The Action ID
                final int actionID = datagram.readInt();

                // The Action Type
                final int type = datagram.readInt();

                // The Action Phase
                final int phase = datagram.readInt();

                // The Action Params
                final String params = datagram.readUTF();

                switch (phase) {
                    // This is the PHASE_INIT message
                    case Sender.PHASE_INIT:
                        Logger.getInstance().debug("Phase " + phase
                                + " from "
                                + datagram.getAddress()
                                + " id " + actionID);
                        doPhaseInit(datagram.getAddress(), type, actionID, params);
                        break;

                    // This is the PHASE_ACK message
                    case Sender.PHASE_ACK:
                        Logger.getInstance().debug("Phase " + phase
                                + " from " + datagram.getAddress()
                                + " id " + actionID);
                        doPhaseAck(actionID);
                        break;

                    // This is the PHASE_COMMIT message
                    case Sender.PHASE_COMMIT:
                        Logger.getInstance().debug("Phase " + phase
                                + " from " + datagram.getAddress()
                                + " id " + actionID);
                        doPhaseCommit(type, params);
                        break;

                    // This is the Default message
                    default:
                        Logger.getInstance().debug("This means something is wrong");
                        break;

                }

            } catch (Exception e) {
                Logger.getInstance().debug("Nothing received");
            }
        }

    }

    /**
     * This method send a message to the initiator of this agreement execution.
     *
     * @param address  the address of the initiator (to whom it will send the ack).
     * @param type     the type of the GuardianAction (retrieved from ActionProtocolManager).
     * @param actionId the identity of the action.
     * @param params   the parameters of the action.
     */
    private void doPhaseInit(final String address, final int type, final int actionId, final String params) {
        // Retrieve AbstractGuardianAction based on type
        final AbstractGuardianAction agaction = ActionProtocolManager.getInstance().getType(type);
        agaction.setID(actionId);
        agaction.setParams(params);

        // Instantiate new Sender to respond with ack to phase init
        final Sender actProtAck = new Sender(agaction);
        Logger.getInstance().debug("something null? ad=" + address
                + ", type =" + type + ", id=" + actionId + " params " + params);

        // Send ack back to A
        actProtAck.sendAck(address);
    }

    /**
     * This method confirms that it received the message from the previous phase.
     *
     * @param actionID the identity of the action.
     */
    private void doPhaseAck(final int actionID) {
        // Retrieve Sender
        final Sender actProt = ActionProtocolManager.getInstance().getActionProtocol(actionID);

        // Just received a message from a pending target
        actProt.reducePendingTargets();

        // Check if we have received all ACKs
        if (actProt.getPendingTargets() == 0) {
            // PHASE 2 has just concluded
            // execute the actions
            actProt.confirmCommit();
        }
    }

    /**
     * end of two-phase-commit algorithm.
     *
     * @param type   the type of the action
     * @param params the parameters of the action
     */
    private void doPhaseCommit(final int type, final String params) {
        Logger.getInstance().debug("commiting type " + type + "params");
        // Retrieve AbstractGuardianAction
        final AbstractGuardianAction action = ActionProtocolManager.getInstance().getType(type);

        // Set the parameters for this execution
        action.setParams(params);

        // Execute part B
        action.executePartB();

        // Flash the LED
        //    LEDManager.getInstance().blinkLED(NETWORK_LED, LEDColor.MAUVE, BLINK_INTERVAL, TIMES_TO_BLINK);
    }


}
