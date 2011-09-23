package eu.funinnumbers.guardian.ui.menu.util;

import com.sun.spot.sensorboard.peripheral.ISwitch;
import eu.funinnumbers.guardian.ui.misc.LEDManager;

import java.util.Date;

/**
 * The <code> MenuHelperThread </code> extends <code> Thread </code> class
 * and provides a trivial user interaction that goes beyond the scope
 * of the game's menu.
 */
public class MenuHelperThread extends Thread { //NOPMD

    /**
     * Initial Counter.
     */
    private static int intCount = 0; //NOPMD

    /**
     * Variable for time computation.
     */
    private long startTime = 0;

    /**
     * The threshold for time period pressing the button.
     */
    private static final int SWITCHTHRESHOLD = 800;

    /**
     * The button pressed.
     */
    private final ISwitch swButton;

    /**
     * Constructor for a new instance of MenuHelperThread.
     */
    public MenuHelperThread() {
        super("MenuHelperThread");
        this.swButton = null; //NOPMD
    }

    /**
     * Constructor for a new instance of MenuHelperThread.
     * This constructor is primarily used for instantiating a thread object regarding the
     * long pressure of the left switch of the SunSPOT.
     *
     * @param swButtonP  is a switch from the sensorboard
     * @param startTimeP is a representation of the time that <code>sw</code> was pressed in milliseconds
     */
    public MenuHelperThread(final ISwitch swButtonP, final long startTimeP) {
        super("MenuHelperThread");
        this.swButton = swButtonP;
        this.startTime = startTimeP;
    }

    /**
     * run() method implementation.
     */
    public void run() { //NOPMD
        //All the required LEDS for one menu item are already on
        boolean state = true;

        // Which LEDs positions are active
        final boolean[] activePositions = LEDManager.getInstance().getLEDBoardActivity();

        //This thread runs indefinatelly
        while (true) {
            //A new instance of time
            final long endTime = (new Date()).getTime(); //NOPMD

            if ((endTime - startTime) > SWITCHTHRESHOLD && swButton.isClosed()) {

                try {
                    // if swButton is pressed beyond threshold time
                    LEDManager.getInstance().longPressedSwitch(state, activePositions);

                } catch (InterruptedException ie) {
                    // this thread will be interupted when the switch will be released
                    // thread terminates upon swButton release
                    return;
                }
                //state indicates a 0.5 s blinking
                state = !state;
                // if the swButton is released before it reaches this time threshold the thread selfterminates
            } else if (swButton.isOpen()) {
                return;
            }
        }
    }
}
