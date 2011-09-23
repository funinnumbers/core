package eu.funinnumbers.guardian.ui.misc;

import com.sun.spot.sensorboard.peripheral.LEDColor;
import com.sun.spot.util.Utils;

/**
 * Led Manager Blink Thread.
 */
public class LEDManagerBLinkThread {

    /**
     * Blink Interval.
     */
    private static int blinkInterval; //NOPMD

    /**
     * Blink Color.
     */
    private final LEDColor color;

    /**
     * Defualt Consttructor.
     *
     * @param blinkIntervalP blink Interval
     * @param colorP         blink Color
     */
    public LEDManagerBLinkThread(final int blinkIntervalP, final LEDColor colorP) {
        this.blinkInterval = blinkIntervalP;
        this.color = colorP;
    }

    /**
     * The main method of the DataSetter thread. It receives the data and sets the corresponding
     * values of the sunSPOT's Guardian class.
     * <p/>
     * Althought it may seem  necessary to include a confirmation stage at the end, if a problem occurs, the eu.funinnumbers.guardian
     * will continue to broadcast its request for data initialization and the Battle Station will resend the data.
     */
    public final void run() {
        LEDManager.getInstance().setOffLEDS(LEDManager.FIRST_LED, LEDManager.LAST_LED);
        LEDManager.getInstance().setOnLEDS(LEDManager.FIRST_LED, LEDManager.LAST_LED, color);
        Utils.sleep(blinkInterval);
        LEDManager.getInstance().setOffLEDS(LEDManager.FIRST_LED, LEDManager.LAST_LED);


    }
}




