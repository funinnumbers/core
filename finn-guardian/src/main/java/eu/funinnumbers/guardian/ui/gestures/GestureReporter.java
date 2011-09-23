package eu.funinnumbers.guardian.ui.gestures;

/*
 * GestureReporter.java
 *
 */


import com.sun.spot.sensorboard.peripheral.LEDColor;
import eu.funinnumbers.guardian.ui.misc.LEDManager;
import eu.funinnumbers.util.Logger;

import java.io.IOException;

/**
 * The <code> GestureReporter </code> is triggered when a predefined gesture is completed
 * by the user and blinks some led to interact with the user.
 */
public class GestureReporter implements GestureListener {

    /**
     * Creates a new instance of ShowStatistics.
     *
     * @throws IOException Input Output Exception
     */
    public GestureReporter() throws IOException {
        LEDManager.getInstance().setOnLEDS(0, LEDManager.LAST_LED, LEDColor.WHITE);
    }

    /**
     * Reports a Clockwise Gesture.
     */
    public void doClockwiseGesture() {
        Logger.getInstance().debug("Clockwise Gesture");
        LEDManager.getInstance().setOnLEDS(0, LEDManager.LAST_LED, LEDColor.BLUE);
    }

    /**
     * Reports a Counter-Clockwise Gesture.
     */
    public void doCounterClockwiseGesture() {
        Logger.getInstance().debug("Counterclockwise Gesture");
        LEDManager.getInstance().setOnLEDS(0, LEDManager.LAST_LED, LEDColor.RED);
    }

    /**
     * Reports a Right Violent Movement.
     */
    public void doHorizontalViolentMovement() {
        Logger.getInstance().debug("Horizontal Violent Movement");
        LEDManager.getInstance().setOnLEDS(0, LEDManager.LAST_LED, LEDColor.ORANGE);
    }

    /**
     * Reports a movement.
     */
    public void doAnyMovement() {
        Logger.getInstance().debug("Movement");
        LEDManager.getInstance().setOnLEDS(0, LEDManager.LAST_LED, LEDColor.GREEN);
    }

    /**
     * Reports no movement.
     */
    public void noMovement() {
        Logger.getInstance().debug("No Movement");
        LEDManager.getInstance().setOnLEDS(0, LEDManager.LAST_LED, LEDColor.PUCE);
    }

    /**
     * Report Right Tilt movement.
     */
    public void doForwardTilt() {
        Logger.getInstance().debug("Right Tilt");
        LEDManager.getInstance().setOnLEDS(0, LEDManager.LAST_LED, LEDColor.MAGENTA);
    }

    /**
     * Report Left Tilt movement.
     */
    public void doBackTilt() {
        Logger.getInstance().debug("Left Tilt");
        LEDManager.getInstance().setOnLEDS(0, LEDManager.LAST_LED, LEDColor.TURQUOISE);
    }

    /**
     * Reports Right Switch push.
     */
    public void doRightSwitch() {
        //Do nothing
    }

    /**
     * Reports Left Switch push.
     */
    public void doLeftSwitch() {
        //Do nothing
    }

    public void doVerticalViolentMovement() {
           Logger.getInstance().debug("Vertical");
        LEDManager.getInstance().setOnLEDS(0, LEDManager.LAST_LED, LEDColor.CHARTREUSE);
    }

    public void doForwardViolentMovement() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void doDownMovement() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
