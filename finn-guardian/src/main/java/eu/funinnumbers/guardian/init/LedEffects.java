package eu.funinnumbers.guardian.init;

import com.sun.spot.sensorboard.peripheral.LEDColor;
import com.sun.spot.util.Utils;
import eu.funinnumbers.guardian.ui.misc.LEDManager;


/**
 * This thread is rensponsible for creating led effects by manipulating LEDManager during
 * the initialization process.
 *
 * @author loukasa
 */
public class LedEffects extends Thread { //NOPMD

    /**
     * This is changed to true to terminate the thread.
     */
    private boolean end;

    /**
     * The color of the led effect.
     */
    private final LEDColor color;

    /**
     * A lighter color for the led effect.
     */
    private final LEDColor colorLight;

    /**
     * The remoteDeploy interval.
     */
    private final int interval;

    /**
     * The default blink interval.
     */
    private static final int BLINK_INTERVAL = 300;

    /**
     * Creates a new instance of LedEffects.
     *
     * @param colorArg      the colorArg of the effects
     * @param colorLightReg the lighter colorArg used in led effects
     */
    public LedEffects(final LEDColor colorArg, final LEDColor colorLightReg) {
        super();
        this.end = false;
        this.color = colorArg;
        this.colorLight = colorLightReg;
        interval = BLINK_INTERVAL;
    }


    /**
     * Runs the thread.
     */
    public void run() { //NOPMD

        boolean firstTime = true;
        LEDManager.getInstance().setOffLEDS(LEDManager.FIRST_LED, LEDManager.LAST_LED);
        final int led5 = 5;
        final int led6 = 6;
        final int led7 = 7;

        while (!end) {
            for (int j = LEDManager.FIRST_LED; j <= LEDManager.LAST_LED; j++) {
                if (j == 0 && firstTime && !end) {
                    LEDManager.getInstance().setOnLED(0, color);
                    LEDManager.getInstance().setOnLED(1, colorLight);

                } else if (j == 1 && firstTime && !end) {
                    LEDManager.getInstance().setOnLED(0, colorLight);
                    LEDManager.getInstance().setOnLED(1, color);
                    LEDManager.getInstance().setOnLED(2, colorLight);
                    firstTime = false;

                } else if (j == 0 && !firstTime && !end) {
                    LEDManager.getInstance().setOnLED(led7, colorLight);
                    LEDManager.getInstance().setOnLED(0, color);
                    LEDManager.getInstance().setOnLED(1, colorLight);
                    LEDManager.getInstance().setOffLEDS(led6, led6);

                } else if (j == 1 && !firstTime && !end) {
                    LEDManager.getInstance().setOnLED(0, colorLight);
                    LEDManager.getInstance().setOnLED(1, color);
                    LEDManager.getInstance().setOnLED(2, colorLight);
                    LEDManager.getInstance().setOffLEDS(led7, led7);

                } else if (j == led7 && !firstTime && !end) {
                    LEDManager.getInstance().setOnLED(led6, colorLight);
                    LEDManager.getInstance().setOnLED(led7, color);
                    LEDManager.getInstance().setOnLED(1, colorLight);
                    LEDManager.getInstance().setOffLEDS(led5, led5);

                } else if (!end) {
                    LEDManager.getInstance().setOnLED(j - 1, colorLight);
                    LEDManager.getInstance().setOnLED(j, color);
                    LEDManager.getInstance().setOnLED(j + 1, colorLight);
                    LEDManager.getInstance().setOffLEDS(j - 2, j - 2);
                }

                Utils.sleep(interval);

            }
        }
    }

    /**
     * Terminates the thread.
     */
    public void terminate() {
        this.end = true;
    }
}
