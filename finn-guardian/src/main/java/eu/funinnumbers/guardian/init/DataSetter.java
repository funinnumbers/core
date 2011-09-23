package eu.funinnumbers.guardian.init;

import com.sun.spot.sensorboard.peripheral.LEDColor;
import eu.funinnumbers.db.model.Avatar;
import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.Team;
import eu.funinnumbers.db.model.GuardianInfo;
import eu.funinnumbers.guardian.AbstractGuardianApp;
import eu.funinnumbers.guardian.communication.echoprotocol.EchoProtocolManager;
import eu.funinnumbers.guardian.storage.PointsOfInterest;
import eu.funinnumbers.guardian.storage.StorageService;
import eu.funinnumbers.guardian.ui.misc.LEDManager;
import eu.funinnumbers.util.Logger;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import java.io.IOException;


/**
 * This thread sets all needed data to the eu.funinnumbers.guardian after the remote deployment of the game's code.
 * <p/>
 * while setting the data the leds show the waiting effect in green colour and at the end the leds flash briefly red.
 *
 * @author loukasa
 */
public class DataSetter extends Thread { //NOPMD

    /**
     * The port in which the eu.funinnumbers.guardian will receive the data.
     */
    protected static final int REPLYPORT = 148;

    /**
     * This boolean signifies the termination of the while loop of the thread and
     * becomes true when the eu.funinnumbers.guardian is initialized successfully.
     */
    private boolean finished = false;

    /**
     * The Datagram Connection used for receiving the data.
     */
    private DatagramConnection dgConnection;

    /**
     * The Datagram to be sent.
     */
    private Datagram datagram;

    /**
     * The GuardianApp to run when data setter is done.
     */
    private final AbstractGuardianApp gameApp; //NOPMD

    /**
     * Default constructor.
     *
     * @param guardianApp the GuardianApp to start when data are fully loaded
     */
    public DataSetter(final AbstractGuardianApp guardianApp) {
        super();
        gameApp = guardianApp;
    }

    /**
     * The main method of the DataSetter thread. It receives the data and sets the corresponding
     * values of the sunSPOT's Guardian class.
     * <p/>
     * Althought it may seem  necessary to include a confirmation stage at the end, if a problem occurs, the eu.funinnumbers.guardian
     * will continue to broadcast its request for data initialization and the Battle Station will resend the data.
     */
    public final void run() { //NOPMD

        // setup Datagram connection
        setupConnection();

        while (!finished) {
            try {
                // clean the Datagram
                datagram.reset();
                // receive from Datagram
                dgConnection.receive(datagram);

                Logger.getInstance().debug("Initialization Datagram received");

                // if this is indeed the correct datagram
                if (datagram.readUTF().equals("data")) {

                    // extract Byte array length
                    int length = datagram.readInt();

                    // extract byte array
                    byte[] byteArray = new byte[length]; //NOPMD
                    datagram.readFully(byteArray, 0, length);

                    // initializing the eu.funinnumbers.guardian from the byteArray
                    final Guardian guardian = new Guardian(); //NOPMD
                    guardian.fromByteArray(byteArray);

                    Logger.getInstance().debug("GuardianID = " + guardian.getID());

                    // extract second Byte array length
                    length = datagram.readInt();

                    // extract second byte array
                    byteArray = new byte[length]; //NOPMD
                    datagram.readFully(byteArray, 0, length);

                    // initialize the avatar class
                    final Avatar avatar = new Avatar(); //NOPMD
                    avatar.fromByteArray(byteArray);

                    Logger.getInstance().debug("AvatarID = " + avatar.getID());

                    // extract third Byte array length
                    length = datagram.readInt();

                    // extract second byte array
                    byteArray = new byte[length]; //NOPMD
                    datagram.readFully(byteArray, 0, length);

                    // Receive the number of available stations
                    final int totStations = datagram.readInt();
                    PointsOfInterest.getInstance().setTotalPOIs(totStations);


                    PointsOfInterest.getInstance().setDSStation(datagram.getAddress());

                    // Extract the ids of the stations
                    for (int stationCounter = 0; stationCounter < totStations; stationCounter++) {
                        PointsOfInterest.getInstance().addPointOfInterest(datagram.readInt());
                    }

                    // initialize the team class
                    final Team team = new Team(); //NOPMD
                    team.fromByteArray(byteArray);

                    Logger.getInstance().debug("TeamID = " + team.getID());

                    /** Change the initPhase of the eu.funinnumbers.guardian to INIT_COMPLETE
                     * to signify that the eu.funinnumbers.guardian is ready to start playing the game.
                     */
                    guardian.setInitPhase(Guardian.INIT_COMPLETE);

                    // set the received data to the GuardianInfo class
                    GuardianInfo.getInstance().setAvatar(avatar);
                    GuardianInfo.getInstance().setGuardian(guardian);
                    GuardianInfo.getInstance().setTeam(team);

                    // Update the Beacon including the correct initPhase
                    EchoProtocolManager.getInstance().updateBcast();

                    // Add received information to the flash memory
                    StorageService.getInstance().clearAll();
                    StorageService.getInstance().add(guardian);
                    StorageService.getInstance().add(avatar);
                    StorageService.getInstance().add(team);
                    StorageService.getInstance().add(PointsOfInterest.getInstance());
                    // This thread should terminate
                    finished = true;

                    // start the blinking effect on the leds
                    final int blinkInter = 300;
                    final int blinkTimes = 3;
                    LEDManager.getInstance().blinkAllLED(LEDColor.GREEN, blinkInter, blinkTimes);
                    LEDManager.getInstance().setOnLEDS(LEDManager.FIRST_LED, LEDManager.LAST_LED, LEDColor.GREEN);

                    // Start Game Application
                    //gameApp.startOldGameApp();

                } else {
                    Logger.getInstance().debug("Data Setter: received unexpected datagram");
                }

            } catch (IOException ex) {
                Logger.getInstance().debug("Error occured in DataSetter", ex);
            }
        }
    }

    /**
     * Setting up the connection with the Battle Station's GuardianInitializer.
     */
    protected final void setupConnection() {

        try {
            // Creates a Server Radiogram Connection on port REPLYPORT
            dgConnection = (DatagramConnection) Connector.open("radiogram://:" + REPLYPORT);

            // Creates a Datagram using the above Connection
            datagram = dgConnection.newDatagram(dgConnection.getMaximumLength());

        } catch (IOException e) {
            Logger.getInstance().debug("Could not open radiogram receiver connection on port " + REPLYPORT);
            Logger.getInstance().debug(e);
        }
    }

}

