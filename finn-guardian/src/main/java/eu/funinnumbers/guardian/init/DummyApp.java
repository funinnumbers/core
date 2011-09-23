/*
 * SGApp.java
 *
 * Created on 28 ��������� 2008, 4:00 ��
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.funinnumbers.guardian.init;

import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.sensorboard.peripheral.LEDColor;
import eu.funinnumbers.db.model.Avatar;
import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.Team;
import eu.funinnumbers.db.model.GuardianInfo;
import eu.funinnumbers.guardian.communication.echoprotocol.EchoProtocolManager;
import eu.funinnumbers.guardian.communication.echoprotocol.Station;
import eu.funinnumbers.guardian.storage.StorageService;
import eu.funinnumbers.guardian.ui.misc.LEDManager;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observable;
import eu.funinnumbers.util.Observer;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import java.io.IOException;

/**
 * This is the class that will be deployed to the sunSPOTs when joining a game.
 * <p/>
 * The class runs the echo protocol and awaits for a Battle Station.
 * Then it requests wireless (Over The Air) initialization with the game's jar.
 * After the OTA deploy the SunSPOT's leds flash to show that a reset is needed.
 * If the initialization fails, the sunSPOT should be reset. In this case the
 * initialization will restart.
 *
 * @author loukasa
 */
public class DummyApp extends MIDlet implements Observer { //NOPMD

    /**
     * The port in which the eu.funinnumbers.guardian will receive the termination message.
     */
    protected static final int REPLYPORT = 148;

    /**
     * The termination message.
     */
    protected static final String REPLY_TOKEN = "terminate";

    /**
     * A variable to specify the progress of the initialization.
     */
    private boolean waiting = false;

    /**
     * This is the thread responsible for creating the initializing led effect that signifies that
     * a BattleStation is present and the INIT_NOJAR request is being handled.
     * The led color is a moderate blue.
     */
    private final LedEffects ledInit = new LedEffects((new LEDColor(0, 0, 100)), (new LEDColor(0, 0, 15)));

    /**
     * The rest is boiler plate code, for Java ME compliance
     * startApp() is the MIDlet call that starts the application.
     *
     * @throws MIDletStateChangeException State Change Exception
     */
    protected void startApp() throws MIDletStateChangeException {

        /**
         * create unitialized Avatar class
         */
        final Avatar avatar = new Avatar();
        avatar.setID(-1);
        avatar.setName("Unitialized");

        /**
         * create unitialized Guardian class
         */
        final Guardian guardian = new Guardian();
        guardian.setAddress(new Long(RadioFactory.getRadioPolicyManager().getIEEEAddress()));
        guardian.setID(avatar.getID());
        final int ledId = 99999;
        guardian.setLedId(ledId);
        guardian.setInitPhase(Guardian.INIT_NOJAR);

        /**
         * create unitialized Team class
         */
        final Team team = new Team();
        team.setTeamId(-1);
        team.setName("Uninitialized");

        GuardianInfo.getInstance().setAvatar(avatar);
        GuardianInfo.getInstance().setGuardian(guardian);
        GuardianInfo.getInstance().setTeam(team);

        /** Initialize Echo Protocol and start broadcasting beacons and listening. */
        final EchoProtocolManager echo = EchoProtocolManager.getInstance();

        // Running on eu.funinnumbers.guardian mode (isStation=false)
        echo.setMode(false);

        // Debug Info
        Logger.getInstance().debug("Hard coded ID = " + 0 + ", LEDID = " + 0 + ", TEAMID = " + 0);
        Logger.getInstance().debug("MAC = " + echo.getMyAddress());
        Logger.getInstance().debug("ID = " + guardian.getID());
        Logger.getInstance().debug("LED_ID = " + guardian.getLedId());
        Logger.getInstance().debug("Team = " + team.getTeamId());

        // Clear the flash memory of the sunspot
        StorageService.getInstance().clearAll();
    }

    /**
     * When the Spot receives a broadcast message from a neighbouring Station
     * the remoteDeploy process begins.
     *
     * @param obj Observable Object
     * @param arg Observable Arguement
     */
    public void update(final Observable obj, final Object arg) { //NOPMD
        // Ignore updates from other managers
        if (!(obj instanceof EchoProtocolManager)) {
            return;
        }

        // If we just heard a beacon from a nearby eu.funinnumbers.station -- ignore it
        if (!(arg instanceof Station)) {
            return;
        }

        if (!waiting) {
            // So this is a neighbouring eu.funinnumbers.station
            final Station station = (Station) arg;

            if (station.isActive()) {
                waiting = true;
            }
            remoteDeploy();
        }
    }

    /**
     * This is called when the Spot is near an active Station
     * and its request to be flashed is being proccessed.
     */
    public void remoteDeploy() { //NOPMD

        /**
         * The Datagram Connection used for termination message.
         */
        DatagramConnection dgConnection;

        /**
         * The Datagram to be sent.
         */
        Datagram datagram;

        // this starts the waiting effect on the leds
        ledInit.start();

        try {
            // Creates a Server Radiogram Connection on port REPLYPORT
            dgConnection = (DatagramConnection) Connector.open("radiogram://:" + REPLYPORT);

            // Creates a Datagram using the above Connection
            datagram = dgConnection.newDatagram(dgConnection.getMaximumLength());

        } catch (IOException e) {
            Logger.getInstance().debug("Could not open radiogram receiver connection on port " + REPLYPORT);
            Logger.getInstance().debug(e);
            return;
        }

        // the application waits for the battleStation's confirmation that the remoteDeploy has finished
        boolean state = true;
        while (state) {
            try {
                // Clean the Datagram
                datagram.reset();

                // Receive from Datagram
                dgConnection.receive(datagram);

                // if a "terminate" message is received
                if (datagram.readUTF().equals(REPLY_TOKEN)) {
                    ledInit.terminate();

                    // this starts the blinking led effect to suggest the end of the remoteDeploy and the need to reset.
                    final int colorR = 100;
                    final int colorG = 0;
                    final int colorB = 100;
                    final int blinkInter = 300;
                    final int blinkTimes = 10;
                    LEDManager.getInstance().blinkAllLED((new LEDColor(colorR, colorG, colorB)), //NOPMD
                            blinkInter, blinkTimes); //NOPMD

                    state = false;
                }

            } catch (IOException ex) {
                Logger.getInstance().debug(ex);
            }
        }

    }

    /**
     * This will never be called by the Squawk VM.
     */
    protected void pauseApp() {
        // never used
    }

    /**
     * Called if the MIDlet is terminated by the system.
     * I.e. if startApp throws any exception other than MIDletStateChangeException,
     * if the isolate running the MIDlet is killed with Isolate.exit(), or
     * if VM.stopVM() is called.
     * <p/>
     * It is not called if MIDlet.notifyDestroyed() was called.
     *
     * @param unconditional If true when this method is called, the MIDlet must
     *                      cleanup and release all resources. If false the MIDlet may throw
     *                      MIDletStateChangeException  to indicate it does not want to be destroyed
     *                      at this time.
     * @throws MIDletStateChangeException State Change Exception
     */
    protected void destroyApp(final boolean unconditional)
            throws MIDletStateChangeException {
        // never used
    }
}
