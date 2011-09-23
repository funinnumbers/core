package eu.funinnumbers.guardian.ui.gestures;

/*
 * GestureListener.java
 *
 */

/**
 * The <code> GestureListener </code> interface.
 */
public interface GestureListener {

    /**
     * Invoked when a Clockwise Gesture completed.
     */
    void doClockwiseGesture();

    /**
     * Invoked when a CounterClockwise Gesture completed.
     */
    void doCounterClockwiseGesture();

    /**
     * Invoked when a Right Violent Movement completed.
     */
    void doHorizontalViolentMovement();

    /**
     * Invoked when a movement is detected.
     */
    void doAnyMovement();

    /**
     * Invoked when no movement is detected.
     */
    void noMovement();

    /**
     * Invoked when right tilt is detected.
     */
    void doForwardTilt();

    /**
     * Invoked when left tilt is detected.
     */
    void doBackTilt();

    /**
     * Invoked when right switch is pressed.
     */
    void doRightSwitch();

    /**
     * Invoked when left switch is pressed.
     */
    void doLeftSwitch();

    /**
     * Invoked when vertical violent movement is completed.
     */
    void doVerticalViolentMovement();

    /**
     * Invoked when vertical violent movement is completed.
     */
    void doForwardViolentMovement();
}

