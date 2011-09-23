package eu.funinnumbers.guardian.ui.misc;

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.sensorboard.peripheral.LEDColor;
import com.sun.spot.util.Utils;

/**
 * Responsible for manipulating the leds.
 */
public final class LEDManager extends Thread { //NOPMD

    /**
     * Position of the first led.
     */
    public static final int FIRST_LED = 0;

    /**
     * Position of the last led.
     */
    public static final int LAST_LED = 7;

    /**
     * Position of the first safe led.
     * What safe means:
     * the first 2 leds are used by the menu system,
     * the last led is used by the network activity class.
     * so the first safe led is the 3rd one.
     */
    public static final int SAFE_FIRST_LED = 2;

    /**
     * Position of the last safe led.
     * What safe means:
     * the first 2 leds are used by the menu system,
     * the last led is used by the network activity class.
     * so the last safe led is the 7th one.
     */
    public static final int SAFE_LAST_LED = 6;

    /**
     * Position of the led used to indicate the top-menu.
     */
    public static final int MENU_TOP_LED = 0;

    /**
     * Position of the led used to indicate the bottom-menu.
     */
    public static final int MENU_BOTTOM_LED = 1;

    /**
     * Minimum blinking of leds.
     */
    private static final int MIN_BLINK = 1;

    /**
     * Maximum blinking of leds.
     */
    private static final int MAX_BLINK = 10;

    /**
     * Unique instance of this class.
     */
    private static LEDManager instance;

    /**
     * Unique instance of leds used by this class methods.
     */
    private static ITriColorLED[] leds; //NOPMD

    /**
     * Array of available colors for a LED.
     */
    public static final LEDColor[] LED_COLORS = {LEDColor.BLUE,
            LEDColor.CHARTREUSE,
            LEDColor.CYAN,
            LEDColor.GREEN,
            LEDColor.MAGENTA,
            LEDColor.ORANGE,
            LEDColor.RED,
            LEDColor.TURQUOISE,
            LEDColor.WHITE,
            LEDColor.YELLOW};

    /**
     * Interval period required for blinking.
     */
    private static final int BLINK_INTERVAL = 500;

    /**
     * Simple Constructor.
     */
    private LEDManager() {
        super();
        leds = EDemoBoard.getInstance().getLEDs();
    }

    /**
     * Returns the current instance of the singleton class. If it does not exist, it constructs a new class.
     *
     * @return the current global GuardianInfo instance
     */
    public static LEDManager getInstance() {
        synchronized (LEDManager.class) {
            if (instance == null) {
                instance = new LEDManager();
            }
        }

        return instance;
    }

    /**
     * Blinks all LEDs once based on the given color.
     *
     * @param color         the color to use
     * @param blinkInterval the blinking interval period in ns . Set 0 to apply the default value(500)
     * @param times         how many times the LEDs are going to blink (1...10)
     */
    public void blinkAllLED(final LEDColor color, final int blinkInterval, final int times) { //NOPMD
        final int blinkTimes = (times < MIN_BLINK) ? MIN_BLINK : ((times > MAX_BLINK) ? MAX_BLINK : times);
        for (int j = 0; j < blinkTimes; j++) {
            setOnLEDS(FIRST_LED, LAST_LED, color);

            // Try to sleep
            try {
                Utils.sleep((blinkInterval != 0) ? blinkInterval : BLINK_INTERVAL); //NOPMD
            } catch (Exception e) {
                e.notifyAll();
            }

            setOffLEDS(FIRST_LED, LAST_LED);

            // Try to sleep
            try {
                Utils.sleep((blinkInterval != 0) ? blinkInterval : BLINK_INTERVAL); //NOPMD
            } catch (Exception e) {
                e.notifyAll();
            }
        }
    }

    /**
     * Blinks all LEDs once based on the given color.
     *
     * @param index         the index number (0...7) of a LED to blink
     * @param color         the color to use
     * @param blinkInterval the blinking interval period in ns . Set 0 to apply the default value(500)
     * @param times         how many times the LEDs are going to blink (1...10)
     */
    public void blinkLED(final int index, final LEDColor color, final int blinkInterval, final int times) { //NOPMD
        final int blinkTimes = (times < MIN_BLINK) ? MIN_BLINK : ((times > MAX_BLINK) ? MAX_BLINK : times);
        for (int j = 0; j < blinkTimes; j++) {
            setOnLED(index, color);

            // Try to sleep
            try {
                Utils.sleep((blinkInterval != 0) ? blinkInterval : BLINK_INTERVAL); //NOPMD
            } catch (Exception e) {
                e.notifyAll();
            }

            leds[index].setOff();

            // Try to sleep
            try {
                Utils.sleep((blinkInterval != 0) ? blinkInterval : BLINK_INTERVAL); //NOPMD
            } catch (Exception e) {
                e.notifyAll();
            }
        }
    }

    /**
     * Blinks all LEDs once based on the given color.
     *
     * @param indexA        the first index number (0...7) of a LED to blink
     * @param indexB        the second index number (0...7) of a LED to blink
     * @param color         the color to use
     * @param blinkInterval the blinking interval period in ns . Set 0 to apply the default value(500)
     * @param times         how many times the LEDs are going to blink (1...10)
     */
    public void blinkRangeLED(final int indexA,
                              final int indexB,
                              final LEDColor color,
                              final int blinkInterval,
                              final int times) { //NOPMD
        final int fromLED = (indexA < indexB) ? indexA : indexB;
        final int toLED = (indexA < indexB) ? indexB : indexA;
        final int blinkTimes = (times < MIN_BLINK) ? MIN_BLINK : ((times > MAX_BLINK) ? MAX_BLINK : times);
        for (int j = 0; j < blinkTimes; j++) {
            setOnLEDS(fromLED, toLED, color);

            // Try to sleep
            try {
                Utils.sleep((blinkInterval != 0) ? blinkInterval : BLINK_INTERVAL); //NOPMD
            } catch (Exception e) {
                e.notifyAll();
            }

            setOffLEDS(fromLED, toLED);

            // Try to sleep
            try {
                Utils.sleep((blinkInterval != 0) ? blinkInterval : BLINK_INTERVAL); //NOPMD
            } catch (Exception e) {
                e.notifyAll();
            }
        }
    }

    /**
     * Turns the LEDS ON based on the color provided.
     *
     * @param fromLED the position of the first LED (0...7)
     * @param toLED   the position of the last LED (0...7)
     * @param color   the color of the LED
     */
    public void setOnLEDS(final int fromLED, final int toLED, final LEDColor color) {
        for (int i = fromLED; i <= toLED; i++) {
            if (color != null) {
                leds[i].setColor(color);
            }
            leds[i].setOn();
        }
    }


    /**
     * Turns the LEDS ON based on the color provided.
     *
     * @param fromLED the position of the first LED (0...7)
     * @param toLED   the position of the last LED (0...7)
     */
    public void setOnLEDS(final int fromLED, final int toLED, final Integer R, final Integer G, final Integer B) {
        for (int i = fromLED; i <= toLED; i++) {

            leds[i].setRGB(R.intValue(), G.intValue(), B.intValue());

            leds[i].setOn();
        }
    }

    /**
     * Turns the LED ON based on the color provided.
     *
     * @param index the position of the LED (0...7)
     * @param color the color of the LED
     */
    public void setOnLED(final int index, final LEDColor color) {
        if (color != null) {
            leds[index].setColor(color);
        }
        leds[index].setOn();
    }


    /**
     * Turns the LEDS OFF.
     *
     * @param fromLED the position of the first LED (0...7)
     * @param toLED   the position of the last LED (0...7)
     */
    public void setOffLEDS(final int fromLED, final int toLED) {
        for (int i = fromLED; i <= toLED; i++) {
            leds[i].setOff();
        }
    }

    /**
     * This uses the 5 LEDs to show the number of neighbours in binary.
     * It can display numbers up to 2^5 = 32
     *
     * @param cnt the number of neighbours
     */
    public void showCount(final int cnt) {
        setOffLEDS(SAFE_FIRST_LED, SAFE_LAST_LED);
        int index = SAFE_LAST_LED;
        int count = cnt;
        while ((count > 0) && (index >= 2)) {
            if (count % 2 != 0) {
                setOnLEDS(index, index, LEDColor.RED);
            }
            count = count >> 1;
            index--;
        }
    }

    /**
     * This uses the 5 LEDs to show the number of neighbours in binary.
     * It can display numbers up to 2^5 = 32
     *
     * @param cnt the number of neighbours
     */
    public void showID(final int cnt) {
        setOffLEDS(SAFE_FIRST_LED, SAFE_LAST_LED);
        int index = SAFE_LAST_LED;
        int mod;
        int count = cnt;
        while ((count > 0) && (index >= 2)) {
            mod = count % 10;
            setOnLEDS(index, index, LED_COLORS[mod]);
            count = count / 10;
            index--;
        }
    }

    /**
     * Method that returns in a boolean array which LED from the board is on.
     *
     * @return an array of boolean vars that indicates which LEDs are already led in the sensorboard
     */
    public boolean[] getLEDBoardActivity() {
        final boolean[] activePosition = new boolean[8];
        for (int i = FIRST_LED; i < LAST_LED; i++) {
            activePosition[i] = leds[i].isOn();
        }
        return activePosition;
    }

    /**
     * Method that handles time of pressure (switch sw is closed) longer than the current threshold.
     *
     * @param state          represantes an on/off blinking state
     * @param activePosition is an array of boolean vars that indicates which LEDs are already led in the sensorboard
     * @throws InterruptedException if the sleep was interrupted
     */
    public void longPressedSwitch(final boolean state, final boolean[] activePosition)
            throws java.lang.InterruptedException {
        if (state) {
            setOffLEDS(FIRST_LED, LAST_LED - 1);
        } else {
            for (int i = FIRST_LED; i < LAST_LED; i++) {
                if (activePosition[i]) {
                    leds[i].setOn();
                }
            }
        }

        sleep(BLINK_INTERVAL);
    }

    /**
     * Method that lights up the leftmost and the second leftmost LEDs on the SunSPOT board.
     * Indicates the top & low level vector/item of the game menu
     *
     * @param topLED a LEDColor instance which indicates a specific vector of the game menu
     */
    public void displayMenuLEDS(final LEDColor topLED) {
        leds[MENU_TOP_LED].setColor(topLED);
        leds[MENU_TOP_LED].setOn();
        leds[MENU_BOTTOM_LED].setOff();
    }

    /**
     * Method that lights up the leftmost and the second leftmost LEDs on the SunSPOT board.
     * Indicates the top & low level vector/item of the game menu.
     *
     * @param topLED a LEDColor instance which indicates a specific vector of the game menu
     * @param lowLED a LEDColor instance which indicates a specific item of the game menu
     */
    public void displayMenuLEDS(final LEDColor topLED, final LEDColor lowLED) {
        leds[MENU_TOP_LED].setColor(topLED);
        leds[MENU_TOP_LED].setOn();
        leds[MENU_BOTTOM_LED].setColor(lowLED);
        leds[MENU_BOTTOM_LED].setOn();
    }

    /**
     * Method which indicates if a specific LED is on or off.
     *
     * @param index the position of the LED (0...7)
     * @return boolean value true if led[index] is on
     */
    public boolean isLEDon(final int index) {
        return leds[index].isOn();
    }

    /**
     * Method which returns the color set of a specific LED.
     *
     * @param index the position of the LED (0...7)
     * @return the LEDColor instance of a specific LED
     */
    public LEDColor getColor(final int index) {
        return leds[index].getColor();
    }

    /**
     * Method which sets the color of a specific LED.
     *
     * @param index the position of the LED (0...7)
     * @param color the color to set on the LED
     */
    public void setColor(final int index, final LEDColor color) {
        leds[index].setColor(color);
    }
}
