package eu.funinnumbers.guardian.ui.misc;

import com.sun.spot.sensorboard.peripheral.LEDColor;
import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.guardian.communication.echoprotocol.EchoProtocolManager;
import eu.funinnumbers.guardian.communication.echoprotocol.Station;
import eu.funinnumbers.guardian.ui.action.ShowEnemies;
import eu.funinnumbers.guardian.ui.menu.SunSpotMenuItem;
import eu.funinnumbers.guardian.ui.menu.SunSpotMenuVector;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observable;
import eu.funinnumbers.util.Observer;

/**
 * Observes the EchoProtocol and uses the 8th LED to indicate network connectivity.
 */
public class NetworkConnectivity extends Thread implements Observer { //NOPMD

    /**
     * The network led.
     */
    private static final int NETWORK_LED = 7;

    /**
     * Number of times to blink on every event.
     */
    private static final int TIMES_TO_BLINK = 2;

    /**
     * for a 500 ns blinking interval.
     */
    private static final int BLINK_INTERVAL = 0;

    /**
     * Menu where enemy guardians will be reported.
     */
    private SunSpotMenuVector enemies = null;

    /**
     * Default constructor -- registers with EchoProtocolManager.
     */
    public NetworkConnectivity() {
        super();
        EchoProtocolManager.getInstance().addObserver(this);
    }

    /**
     * Constructor for reporting enemy guardians.
     *
     * @param enemiesVector the menu to hold ShowEnemies menu items.
     */
    public NetworkConnectivity(final SunSpotMenuVector enemiesVector) {
        this();
        enemies = enemiesVector;
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
    public void update(final Observable obs, final Object arg) { //NOPMD
        if (!(obs instanceof EchoProtocolManager)) {
            return;
        }

        // Check if this is a FGStationApp
        if (arg instanceof Station) {
            final Station tempStation = (Station) arg;
            if (tempStation.isActive()) {
                Logger.getInstance().debug("UI:Entering new STATION");
                LEDManager.getInstance().blinkLED(NETWORK_LED, LEDColor.BLUE, BLINK_INTERVAL, TIMES_TO_BLINK);
            } else {
                Logger.getInstance().debug("UI:Leaving STATION");
                LEDManager.getInstance().blinkLED(NETWORK_LED, LEDColor.RED, BLINK_INTERVAL, TIMES_TO_BLINK);
            }

        } else if (arg instanceof Guardian) {
            // So this is a neighbouring eu.funinnumbers.guardian
            final Guardian guardian = (Guardian) arg;

            // Check if this is a SPOT entering our transmission range
            if (guardian.isAlive()) {
                // This is a new SPOT
                Logger.getInstance().debug("UI:New SPOT just arrived addr=" + guardian.getAddress());
                LEDManager.getInstance().blinkLED(NETWORK_LED, LEDColor.WHITE, BLINK_INTERVAL, TIMES_TO_BLINK);

                // Update Enemies MenuVector
                if (enemies != null) {
                    // Create new ShowEnemy MenuItem based on this Guardian
                    //if (GuardianInfo.getInstance().getAvatar().getTeamId() != g.getAvatar().getTeamId())
                    enemies.addItemToVector(new SunSpotMenuItem(guardian.getID(),
                            new ShowEnemies(guardian.getLedId()), LEDColor.BLUE));
                }

            } else {
                // This is a SPOT that has left our transmission range
                // and EchoProtocol has decided to remove it from its list
                // (after EchoProtocolManager.BEACON ms)
                Logger.getInstance().debug("UI:SPOT has left addr=" + guardian.getAddress());
                LEDManager.getInstance().blinkLED(NETWORK_LED, LEDColor.ORANGE, BLINK_INTERVAL, TIMES_TO_BLINK);

                // Update Enemies MenuVector
                if (enemies != null) {
                    // Remove ShowEnemy MenuItem based on this Guardian
                    // if (GuardianInfo.getInstance().getAvatar().getTeamId() != g.getAvatar().getTeamId())
                    enemies.removeItemFromVectorWithID(guardian.getID());
                }
            }
        }
    }

    /**
     * This method returns the index of LED used for network activity.
     *
     * @return a constant integer NETWORK_LED that provides the network activity LED
     */
    public static int getNetworkLED() {
        return NETWORK_LED;
    }

    /**
     * This method returns how many times the network activity LED will blink.
     *
     * @return a constant integer TIMES_TO_BLINK that provides the number of times
     *         the network activity led will blink
     */
    public static int getTimesBlinkingLED() {
        return TIMES_TO_BLINK;
    }

    /**
     * This method returns the blinking interval of the network activity led.
     *
     * @return a constant integer BLINK_INTERVAL that provides the interval period
     *         between network activity's LED blinks
     */
    public static int getBlinkInterval() {
        return BLINK_INTERVAL;
    }
}
