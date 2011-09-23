package eu.funinnumbers.guardian.ui.action;

import com.sun.spot.sensorboard.peripheral.LEDColor;
import eu.funinnumbers.guardian.ui.misc.LEDManager;

/**
 * The <code> AbstractAction </code> that implements the <code> ActionListener </code>
 * and provides the <code> DisplayProgressBar </code> method.
 */
public abstract class AbstractAction implements ActionListener {

    /**
     * Invoked when an action occurs in a Checkable MenuItem ( Knife, Gun e.t.c ).
     */
    public abstract void actionPerformed();

    /**
     * Invoked when an action occurs in a Checkable ( Knife, Gun e.t.c ) and
     * non Checkable MenuItem , primarily a display action regarding user interaction( Health, Ammo e.t.c ).
     */
    public abstract void actionSelected();

    /**
     * Invoked when an action occurs in a Checkable ( Knife, Gun e.t.c ) and
     * non Checkable MenuItem , primarily to stop action displaying in user interface( Health, Ammo e.t.c ).
     */
    public abstract void actionDeselected();

    /**
     * Implementing DisplayProgressBar(int value) : LED[0] is used as a display of the top level menu vectors
     * LED[1] is used as a display of the low level menu items
     * LED[2] - LED[6] is used to display current guardians hit points and ammunition quota
     * <p/>
     * <p/>
     * Note : More weapons in the inventory more colors required . Must know how many weapons
     * and items is the eu.funinnumbers.guardian holding.
     * <p/>
     * For the time being the number of weapons is two.
     *
     * @param val the value of the progress bar in the range of 1..100
     */
    public void displayProgressBar(final int val) {
        int value = val;
        // Turn off the value display leds
        final int lastLed = 6;
        LEDManager.getInstance().setOffLEDS(2, lastLed);

        // Check the value if out of range
        if (value < 0) {
            value = 0;
        }
        final int range = 100;
        // Check the value if out of range
        if (value > range) {
            value = range;
        }

        // Activate leds
        // Compute how many LEDS we want to turn on
        // all of them are colored in BLUE

        // if value is above 10 there is no need to warn the user.
        // Displays the values normally
        final int index = value / 20;
        LEDManager.getInstance().setOnLEDS(2, index + 1, LEDColor.BLUE);

        // compute if the last led will be RED
        if ((value / 10) % 2 == 1) {
            // light up the rightmost LED colored in RED
            LEDManager.getInstance().setOnLEDS(index + 2, index + 2, LEDColor.RED);
        }


    }
}
