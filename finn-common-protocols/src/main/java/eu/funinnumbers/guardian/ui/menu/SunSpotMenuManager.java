package eu.funinnumbers.guardian.ui.menu;

/*
 * SunSpotMenuManager.java
 *
 */

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import com.sun.spot.sensorboard.peripheral.ISwitchListener;
import eu.funinnumbers.guardian.ui.menu.util.MenuHelperThread;
import eu.funinnumbers.guardian.ui.misc.LEDManager;
import eu.funinnumbers.util.Logger;

import java.util.Date;

/**
 * The <code> SunSpotMenuManager </code> that initialiaze the <code> SunSpotMenu </code>,
 * its <code> SunSpotMenuVectors </code> and the <code> SunSpotMenuItems </code> that
 * its <code> SunSpotMenuVector </code> will contain.
 * Also it implements the <code> ISwitchListener </code> which determines the switch UI
 */
public class SunSpotMenuManager implements ISwitchListener { //NOPMD

    /**
     * SUNSpot ISwitch 1 and 2.
     */
    private final ISwitch switch1, switch2;

    /**
     * Variable for time computation.
     */
    private long startTime = 0;

    /**
     * Variable for time computation.
     */
    private long endTime = 0;

    /**
     * Variable for time computation.
     */
    private long periodPressSw1 = 0;

    /**
     * Only one event of button pressed/released is permitted.
     */
    private boolean buttonLock = false;

    /**
     * Left SW threshold.
     */
    private static final int LEFT_SW_THRESHOLD = 800;


    /**
     * The SPOTMenu.
     */
    private final SunSpotMenu sunspotmenu;


    /**
     * Temp menu vectors and items (actions invoking).
     */
    private SunSpotMenuVector tempVector;

    /**
     * Temp SunSpot menu item.
     */
    private SunSpotMenuItem tempItem;

    /**
     * FunnyLED instance.
     */
    private MenuHelperThread funnyLED;

    /**
     * Constructor for a new instance of SunSpotMenuManager
     * Initialization of the pre-determined menu of our game.
     *
     * @param menu the top level Menu
     */
    public SunSpotMenuManager(final SunSpotMenu menu) {

        //Setting Up : Switches
        switch1 = EDemoBoard.getInstance().getSwitches()[EDemoBoard.SW1];
        switch2 = EDemoBoard.getInstance().getSwitches()[EDemoBoard.SW2];

        // enable automatic notification of switches
        switch1.addISwitchListener(this);
        switch2.addISwitchListener(this);

        // Get the menu
        sunspotmenu = menu;

        // Store the current SunSpotMenuVector of the SunSpotMenu
        tempVector = sunspotmenu.getCurrentVectorOfMenu();

        // Store the current SunSpotMenuItem of the current SunSpotMenuVector
        tempItem = tempVector.getCurrentItemOfVector();

        // Display LEDS
        LEDManager.getInstance().displayMenuLEDS(tempVector.getColor(), tempItem.getColor());

        // Select MenuVector
        tempVector.getAction().actionSelected();

        // Select MenuItem
        if (tempItem != null) {
            tempItem.getAction().actionSelected();
        }

        // Initialising MenuHelperThread instance (just for security reasons.
        // Nobody wants an early nullpointer exception :)
        funnyLED = new MenuHelperThread();
    }

    /**
     * Callback method for when the switch state changes from released to pressed
     * They are run in a new thread.
     *
     * @param swButton the switch that was pressed.
     */
    public void switchPressed(final ISwitch swButton) { //NOPMD 
        synchronized (this) {
            // If one button is already pressed exit the handler
            if (!buttonLock) {

                if (switch1.isClosed() && !(switch2.isClosed())) {
                    // Deselect currently selected item
                    if (tempItem != null) {
                        tempItem.getAction().actionDeselected();
                    }

                    // Lock of switch press listener
                    buttonLock = true;

                    // Start of measure
                    startTime = (new Date()).getTime();

                    // Instantiating funnyLED object
                    //A MenuHelperThread instance (sub class of thread)
                    funnyLED = new MenuHelperThread(switch1, startTime);

                    //This thread is instantiated and runs when switch1 is pressed
                    funnyLED.start();

                } else if (!(switch1.isClosed()) && switch2.isClosed()) {

                    // Lock of switch press listener
                    buttonLock = true;

                    // Store the current SunSpotMenuVector of the SunSpotMenu
                    tempVector = sunspotmenu.getCurrentVectorOfMenu();

                    // Store the current SunSpotMenuItem of the current SunSpotMenuVector
                    tempItem = tempVector.getCurrentItemOfVector();

                    // The actions about the selected item have been triggered
                    if (tempItem != null) { //NOPMD
                        tempItem.getAction().actionPerformed();
                    }
                }
            }
        }
    }

    /**
     * Callback method for when the switch state changes from pressed to released
     * They are run in a new thread.
     *
     * @param swButton the switch that was released.
     */
    public void switchReleased(final ISwitch swButton) { //NOPMD

        if (swButton.equals(switch1) && switch2.isOpen()) {
            try {
                if (funnyLED.isAlive()) {
                    // Stoping funnyLED thread
                    funnyLED.interrupt();

                    // waiting funnyLED to terminate
                    funnyLED.join();
                }
            } catch (InterruptedException ie) {
                // If interrupt is caught in the main thread
                Logger.getInstance().debug(ie.toString());
            }

            // End of measure
            endTime = (new Date()).getTime();

            // Compute the period of pressure of switch2
            periodPressSw1 = endTime - startTime;

            // Left switch was pressed for over the LEFT_SW_THRESHOLD
            // ( High-Level menu Selection -- SunSpotMenuVector Selection )
            if (periodPressSw1 > LEFT_SW_THRESHOLD) {

                // Go to the next SunSpotMenuVector of the SunSpotMenu
                sunspotmenu.goToNextVectorOfMenu();

                // Store the current SunSpotMenuVector of the SunSpotMenu
                tempVector.getAction().actionDeselected();

                tempVector = sunspotmenu.getCurrentVectorOfMenu();

                // Select the first item in the tempVector
                tempVector.goToFirstItemOfVector();
                tempItem = tempVector.getCurrentItemOfVector();

                // Check if this is an empty vector
                if (tempItem == null) {
                    // Display LEDS
                    LEDManager.getInstance().displayMenuLEDS(tempVector.getColor());

                    // Select MenuVector
                    tempVector.getAction().actionSelected();

                } else {
                    // This is not an empty vector
                    // Display LEDS
                    LEDManager.getInstance().displayMenuLEDS(tempVector.getColor(), tempItem.getColor());

                    // Select MenuVector
                    tempVector.getAction().actionSelected();

                    // Select MenuItem
                    tempItem.getAction().actionSelected();
                }

                // Release the lock of the switch press listener
                buttonLock = false; // Release the lock

            } else {
                // Left switch switch was pressed for less than the LEFT_SW_THRESHOLD
                // ( Low-Level menu Selection -- SunSpotMenuItem Selection )

                // Store the current SunSpotMenuVector of the SunSpotMenu
                tempVector = sunspotmenu.getCurrentVectorOfMenu();

                // Go to the next SunSpotMenuItem of the current SunSpotMenuVector of the SunSpotMenu
                if (tempVector != null) {
                    tempVector.goToNextItemOfVector();
                }

                // Store the current SunSpotMenuItem of the SunSpotMenuVector
                tempItem = tempVector.getCurrentItemOfVector();

                // Check if this is an empty vector
                if (tempItem == null) {
                    // Display LEDS
                    LEDManager.getInstance().displayMenuLEDS(tempVector.getColor());

                } else {
                    // This is not an empty vector
                    // Display LEDS
                    LEDManager.getInstance().displayMenuLEDS(tempVector.getColor(), tempItem.getColor());

                    // Select MenuItem
                    tempItem.getAction().actionSelected();
                }
            }

            // Zero the time counter
            periodPressSw1 = 0;

            // Release the lock of the switch press listener
            buttonLock = false;
        }

        if (swButton.equals(switch2)) {

            // Release the lock of the switch press listener
            buttonLock = false;
        }
    }
}

