/**
 * The Brodacaster Thread of Echo Protocol broadacasts a message each [BEACON] ms at port [ECHOPORT].
 * Depending on the mode on which Echo Protocol runs the broadcast message could be either
 * eu.funinnumbers.station$[LEDiD] when running on Battle Station, either
 * eu.funinnumbers.guardian@[eu.funinnumbers.station address]$[LEDiD] when running on Guardian
 *
 */
package eu.funinnumbers.guardian.communication.echoprotocol;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.util.Utils;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.db.model.GuardianInfo;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import java.io.IOException;

/**
 * The Broadcaster thread implements the Observer interface.
 * Observing EchoProtocolManager allows changes on broadcast message regarding Battle Station Address
 */
public class Broadcaster extends Thread { //NOPMD

    /**
     * Beacon message.
     * (Device Identifier)@(Station's Address)$(Guardian Id)#(Led Id)!(Init Phase)
     */

    /**
     * The Broadcast Datagram Connection.
     */
    private DatagramConnection dgConnection = null;

    /**
     * The Datagram used for broadcasting beacons.
     */
    private Datagram datagram = null;

    /**
     * Guardian token indentifier.
     */

    public static final int TOKEN_GUARDIAN = 0;

    /**
     * Station token identifier.
     */
    public static final int TOKEN_STATION = 1;

    /**
     * Missing eu.funinnumbers.station token identifier.
     */
    public static final int TOKEN_NOSTATION = -1;

    /**
     * Indicates the device type, eu.funinnumbers.guardian or eu.funinnumbers.station.
     */
    private int deviceIdentifier = TOKEN_GUARDIAN;

    /**
     * Indicates the eu.funinnumbers.guardian Id.
     */
    private int guardianID = 0;

    /**
     * Indicates the led Id.
     */
    private int ledId = 0;

    /**
     * Indicates the eu.funinnumbers.station address.
     * If the device is proper infrastructure eu.funinnumbers.station the value is TOKEN_NOSTATION.
     */
    private Long stationAddress = new Long(TOKEN_NOSTATION);

    /**
     * Indicates if the Broadcaster runs on Station mode or not.
     */
    private boolean iAmStation = false;

    /**
     * Indicates if the Broadcaster runs on a Mobile eu.funinnumbers.station mode.
     */
    private boolean iAmMobiStation = false;

    /**
     * Indicates the initialization phase.
     */
    private int initPhase = 0;

    /**
     * Indicates the status of the Broadcaster.
     */
    protected boolean enabled = true;

    /**
     * Indicates the status of the Broadcaster.
     */
    protected boolean isRunning = true;


    /**
     * The broadcast message is build according to the running mode.
     *
     * @param isStation true if running as Battle Station
     *                  false if running as Guardian
     */
    public final void setMode(final boolean isStation) {

        if (isStation) {
            iAmStation = true;
        }

        constructBeaconMsg();
    }

    /**
     * Setups the broadcasting connection.
     */
    public final void setupConnection() {

        try {
            // Creates a broadcast Datagram Connection
            dgConnection = (DatagramConnection) Connector.open("radiogram://broadcast:" + EchoProtocolManager.ECHOPORT);

            // Set Maximum hops number of the broadcasted messages
            ((RadiogramConnection) dgConnection).setMaxBroadcastHops(1);


            // Creates a Datagram using the above Connection
            datagram = dgConnection.newDatagram(dgConnection.getMaximumLength());

        } catch (IOException ex) {
            Logger.getInstance().debug("Could not open radiogram broadcast connection", ex);
        }
    }

    /**
     * If this thread was constructed using a separate
     * <code>Runnable</code> run object, then that
     * <code>Runnable</code> object's <code>run</code> method is called;
     * otherwise, this method does nothing and returns.
     * <p/>
     * Subclasses of <code>Thread</code> should override this method.
     *
     * @see #start()
     */
    public final void run() { //NOPMD

        // Setup the datagram connection
        setupConnection();

        // Pause before sending first beacon to give other threads
        // chance to change mode
        Utils.sleep(EchoProtocolManager.getInstance().getInitTime());

        // Temporary neighbour object.
        String tmpSemiNeighMAC;

        // FinnLogging counter
        /*START_OF_FINNLOGGER
        short bcastedTimes = FinnLogger.SAMP_PERIOD;
        END_OF_FINNLOGGER*/

        while (isRunning) {

            if (enabled) {
                try {

                    // Clean the Datagram
                    datagram.reset();

                    // Write device identifier: eu.funinnumbers.station or eu.funinnumbers.guardian
                    datagram.writeInt(deviceIdentifier);

                    // Get the semi-neighbours MAC addresses.
/*LITE          final Enumeration semiNeighEnum = EchoProtocolManager.getInstance().getSemiNeighbours().getKeys();

                // Define the number of one-way neighbours
                final int totSemiNeighs = EchoProtocolManager.getInstance().getSemiNeighbours().size();

                // Write the number of neighbours
                datagram.writeInt(totSemiNeighs);

                // Write the corresponding MAC addresses
                while (semiNeighEnum.hasMoreElements()) {
                    tmpSemiNeighMAC = (String) semiNeighEnum.nextElement();
                    datagram.writeUTF(tmpSemiNeighMAC);

                }
LITE*/

                    // Write the connected eu.funinnumbers.station's address
                    datagram.writeLong(stationAddress.longValue());

                    // Write the guardianID
                    datagram.writeInt(guardianID);

                    // Write the Led ID
                    datagram.writeInt(ledId);

                    // Write the Initialization phase.
                    datagram.writeInt(initPhase);

                    // Send the Datagram
                    if (enabled) {
                        dgConnection.send(datagram);
                    }
                    //       Logger.getInstance().debug("\t\tBROADCST");

                    //Logger.getInstance().debug(bcastMsg);


                    /*         DON NOT CHANGE THE COMMENT LINE BELOW         */
                    /*START_OF_FINNLOGGER
                    if (bcastedTimes == FinnLogger.SAMP_PERIOD && FinnLogger.overAllcounter < FinnLogger.LOG_SIZE) {
                        logData();
                        bcastedTimes = 0;
                    }
                    bcastedTimes++;
                    END_OF_FINNLOGGER*/
                    /*         DON NOT CHANGE THE COMMENT LINE ABOVE      */


                } catch (Exception ex) {
                    Logger.getInstance().debug("Could not send radiogram", ex);
                }
            }
            EchoProtocolManager.getInstance().cleanDeadNeighbours();
            Utils.sleep(EchoProtocolManager.getInstance().getBeaconInterval());
        }

    }

    /**
     * Logs various system platform parameters.
     */
    private void logData() {
        /*         DON NOT CHANGE THE COMMENT LINE BELOW         */
        /*START_OF_FINNLOGGER*/
        //FinnLogger.getInstance().logCounters();
        //long logTime = System.currentTimeMillis();
        //FinnLogger.getInstance().batteryLevel();
        //FinnLogger.getInstance().memory();
        //FinnLogger.getInstance().uniDirectional(EchoProtocolManager.getInstance().getSemiNeighbours().size());
        //FinnLogger.getInstance().biDirectional(EchoProtocolManager.getInstance().getNeighbours().size());
        //if (EchoProtocolManager.getInstance().getMyStation().isActive() == true) {
        //    FinnLogger.getInstance().stations(1);
        //} else {
        //    FinnLogger.getInstance().stations(0);
        //}
        //(new LonglogTime = System.currentTimeMillis() - logTime;
        //Logger.getInstance().debug("logging took " + logTime );
        // THIS IS ABOUT 6 ms


        /*END_OF_FINNLOGGER*/
        /*         DON NOT CHANGE THE COMMENT LINE ABOVE      */
    }

    /**
     * Constructs the beacon message.
     */
    protected void constructBeaconMsg() {
        //Reformatting  broadcast message

        // The device is a mobile eu.funinnumbers.station
        if (iAmMobiStation) {
            deviceIdentifier = TOKEN_STATION;
            stationAddress = EchoProtocolManager.getInstance().getMyAddress();
            guardianID = EchoProtocolManager.getInstance().getMyStation().getStationId();
            ledId = EchoProtocolManager.getInstance().getMyStation().getLEDId();

            // The device is a eu.funinnumbers.station
        } else if (iAmStation) {

            deviceIdentifier = TOKEN_STATION;
            stationAddress = new Long(TOKEN_NOSTATION);

            // The device is a eu.funinnumbers.guardian
        } else {
            deviceIdentifier = TOKEN_GUARDIAN;
            stationAddress = EchoProtocolManager.getInstance().getMyStation().getAddress();
            guardianID = GuardianInfo.getInstance().getGuardian().getID();
            ledId = GuardianInfo.getInstance().getGuardian().getLedId();
            initPhase = GuardianInfo.getInstance().getGuardian().getInitPhase();
        }

        Logger.getInstance().debug(" Now broadcasting : " + deviceIdentifier + " - " + stationAddress + "- guardianID " + guardianID + "- phase " + initPhase);
    }

    /**
     * Set eu.funinnumbers.guardian as mobile eu.funinnumbers.station.
     *
     * @param isMobileStation True if it is a mobile eu.funinnumbers.station
     */
    public final void setMobileStation(final boolean isMobileStation) {
        this.iAmStation = true;
        this.iAmMobiStation = isMobileStation;
    }

    /**
     * Set the Station ID.
     *
     * @param stationID an int with the Station  ID.
     */
    public void setStationID(final int stationID) {
        this.guardianID = stationID;
    }

    /**
     * Set the LED ID of the eu.funinnumbers.station.
     *
     * @param ledID an int with the ledID
     */
    public void setStationLedId(final int ledID) {
        this.ledId = ledID;
    }
}
