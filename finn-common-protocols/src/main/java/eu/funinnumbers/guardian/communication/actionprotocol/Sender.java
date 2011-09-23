package eu.funinnumbers.guardian.communication.actionprotocol;

import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.GuardianInfo;
import eu.funinnumbers.guardian.communication.echoprotocol.EchoProtocolManager;
import eu.funinnumbers.guardian.ui.misc.NetworkConnectivity;
import eu.funinnumbers.util.Logger;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import java.io.IOException;
import java.util.Vector;

/**
 * Responsible for transmitting GuardianAction to specified target guardians.
 */
public class Sender {
    /**
     * initialisation of two phase commit variables.
     */
    public static final int PHASE_INIT = 1;

    /**
     * initialisation of two phase commit variables.
     */
    public static final int PHASE_ACK = 2;

    /**
     * initialisation of two phase commit variables.
     */
    public static final int PHASE_COMMIT = 3;

    /**
     * from NetworkConnectivity : Needed for NetworkActivity blinking.
     */
    private static final int NETWORK_LED = NetworkConnectivity.getNetworkLED(); //NOPMD

    /**
     * from NetworkConnectivity : Needed for NetworkActivity blinking.
     */
    private static final int TIMES_TO_BLINK = 1; //NOPMD

    /**
     * from NetworkConnectivity : Needed for NetworkActivity blinking.
     */
    private static final int BLINK_INTERVAL = 500; //NOPMD

    /**
     * declaration of the action.
     */
    private final AbstractGuardianAction action;

    /**
     * the number of targets due to confirm.
     */
    private int pendingTargets;

    /**
     * Default Constructor.
     *
     * @param act the GuardianAction to execute .
     */
    public Sender(final AbstractGuardianAction act) {
        action = act;
        pendingTargets = 0;

        // Store for latter usage
        ActionProtocolManager.getInstance().addActionProtocol(this);

        // Set pending targets
        pendingTargets = action.getTargets().size();
    }

    /**
     * used to retrieve action.
     *
     * @return action.
     */
    public AbstractGuardianAction getAction() {
        return action;
    }

    /**
     * reduce the number of targets left to confirm.
     */
    public void reducePendingTargets() {
        pendingTargets--;
    }

    /**
     * get the number of targets that haven't confirmed yet.
     *
     * @return pendingTargets.
     */
    public int getPendingTargets() {
        return pendingTargets;
    }

    /**
     * Executes this instance of the ActionSender -- that is, it tries to agree with all targets to commit to this
     * particular GuardianAction.
     */
    public void run() {
        Logger.getInstance().debug("Starting Commit");

        // Indicate that the Action Protol has started
        // LEDManager.getInstance().setOnLED(NETWORK_LED, LEDColor.BLUE);

        // Send the initiator's message
        initCommit();
    }

    /**
     * Initiate commit protocol.
     */
    public void initCommit() {

        // This is an actions without partB.
        final Long targetAddress = ((Guardian) action.getTargets().elementAt(0)).getAddress();
        if (!EchoProtocolManager.getInstance().isStation()
                && targetAddress.equals(GuardianInfo.getInstance().getGuardian().getAddress())) {
            Logger.getInstance().debug("Single-Guardian Action");
            action.executePartA();
            action.setComplete(true);
            action.setSuccess(true);


            // Indicate that the Action Protocol has finished succefully
            // LEDManager.getInstance().blinkLED(NETWORK_LED, LEDColor.GREEN, BLINK_INTERVAL, TIMES_TO_BLINK);
            return;
        }

        // This is a normal actions that involves many participants
        try {
            // Send to all neighbours involved the initCommit message
            sendPhaseDatagram(PHASE_INIT);

            // Flash the LED
            // LEDManager.getInstance().blinkLED(NETWORK_LED, LEDColor.BLUE, 100, 1);
        } catch (Exception ex) {
            // Indicate that the Action Protocol has failed to finish succefully
            // LEDManager.getInstance().blinkLED(NETWORK_LED, LEDColor.RED, BLINK_INTERVAL, TIMES_TO_BLINK);

            Logger.getInstance().debug("Could not send init message", ex);
            action.setComplete(true);
            action.setSuccess(false);
        }

    }

    /**
     * All involved parties have agreed to commit. Send them the decision.
     */
    public void confirmCommit() {

        // Now execute Part A
        action.executePartA();

        // Indicate that Action Protocol has finished succefully
        // LEDManager.getInstance().blinkLED(NETWORK_LED, LEDColor.GREEN, BLINK_INTERVAL, TIMES_TO_BLINK);

        try {
            // Send to all neighbours involved the confirmCommit message
            sendPhaseDatagram(PHASE_COMMIT);


        } catch (Exception ex) {
            Logger.getInstance().debug("Could not open radiogram broadcast connection", ex);
            // Indicate that the Action Protocol has failed to finish succefully
            // LEDManager.getInstance().blinkLED(NETWORK_LED, LEDColor.RED, 2*BLINK_INTERVAL, TIMES_TO_BLINK);
            action.setComplete(true);
            action.setSuccess(false);
        }
    }

    /**
     * This method send a message to the initiator of this agreement execution.
     *
     * @param address the address of the initiator (to whom it will send the ack).
     */
    public void sendAck(final String address) {
        Logger.getInstance().debug("sendAck invoked");
        // SEND PHASE_ACK
        try {
            // Creates a broadcast Datagram Connection
            final DatagramConnection dataConn = (DatagramConnection) Connector.open("radiogram://" + address + ":120");

            // Creates a Datagram using the above Connection
            final Datagram datagram = dataConn.newDatagram(dataConn.getMaximumLength());
            datagram.reset();

            // Write the ID of this Action
            datagram.writeInt(action.getID());

            // Write the Type of this Action
            datagram.writeInt(action.getType());

            // Write the phase number
            datagram.writeInt(PHASE_ACK);

            // Write the params
            datagram.writeUTF(action.getParams());

            dataConn.send(datagram);
            dataConn.close();

            // Flash the LED
            //  LEDManager.getInstance().blinkLED(NETWORK_LED, LEDColor.CHARTREUSE, BLINK_INTERVAL, TIMES_TO_BLINK);

        } catch (Exception ex) {
            // Indicate that the Action Protocol has failed to finish succefully
            // LEDManager.getInstance().blinkLED(NETWORK_LED, LEDColor.RED, 2*BLINK_INTERVAL, TIMES_TO_BLINK);
            Logger.getInstance().debug("Could not send ack message", ex);

        }
    }

    /**
     * This method sends a new datagram to all parties involved.
     * Each datagram contains: the ID of the action, the TYPE of the action and the phase of the protocol.
     *
     * @param phase the phase of the protocol .
     * @throws IOException if atleast 1 message transmission fails.
     */
    private void sendPhaseDatagram(final int phase) throws IOException {
        Logger.getInstance().debug("Entering phase " + phase);
        final Vector enemies = action.getTargets();
        final int totEnemies = enemies.size();
        Logger.getInstance().debug("Sending to " + enemies.size() + " enemies");
        if (totEnemies > 0) {

            for (int i = 0; i < totEnemies; i++) {

                final Guardian neigh = (Guardian) enemies.elementAt(i);
                Logger.getInstance().debug("Sender tries to send to " + neigh.getAddress());

                // Creates a broadcast Datagram Connection
                final DatagramConnection dataConn =
                        (DatagramConnection) Connector.open("radiogram://" + neigh.getAddress() + ":120");

                // Creates a Datagram using the above Connection
                final Datagram datagram = dataConn.newDatagram(dataConn.getMaximumLength());
                try {
                    datagram.reset();

                    // Write the ID of this Action
                    Logger.getInstance().debug("ID " + action.getID());
                    datagram.writeInt(action.getID());

                    // Write the Type of this Action
                    Logger.getInstance().debug("Type " + action.getType());
                    datagram.writeInt(action.getType());

                    // Write the phase number
                    Logger.getInstance().debug("Phase " + phase);
                    datagram.writeInt(phase);

                    // Write the params
                    Logger.getInstance().debug("Params " + action.getParams());
                    datagram.writeUTF(action.getParams());

                    dataConn.send(datagram);
                    dataConn.close();

                } catch (Exception ex) {
                    // Indicate that the Action Protocol has failed to finish succefully
                    // LEDManager.getInstance().blinkLED(NETWORK_LED, LEDColor.RED, 2*BLINK_INTERVAL, TIMES_TO_BLINK);

                    Logger.getInstance().debug(" Unable to send phase  " + phase, ex);

                    // Close the Datagram connection 
                    dataConn.close();
                }
            }


        } else {
            Logger.getInstance().debug("No targets!");
        }

        // Flash the LED
        // LEDManager.getInstance().blinkLED(NETWORK_LED, LEDColor.GREEN,250,1);
    }

}
