package eu.funinnumbers.guardian.ui.gestures;

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import com.sun.spot.sensorboard.peripheral.LIS3L02AQAccelerometer;
import com.sun.spot.util.Utils;
import eu.funinnumbers.util.Logger;

import java.io.IOException;

/**
 * The <code> GestureRecogniser </code> process the samples of the accelerometer ( Acceleration and Raw )
 * and decides which Gesture has happened.
 */
public class GestureRecogniser implements Runnable { //NOPMD

    /**
     * Default sample sized.
     */
    private static final int DEF_SAMPLESIZE = 150;

    /**
     * Threashold value for internal mechanisms.
     */
    private static final int POWER_THRESHOLD = 100000;

    /**
     * Buffers for the raws in X-Axis, Y-Axis, Z-Axis.
     */
    private final CircularBuffer xBuf, yBuf, zBuf;

    /**
     * Buffers for the acceleration in X-Axis, Y-Axis, Z-Axis.
     */
    private double[] xAccBuf = {0, 0, 0, 0}, yAccBuf = {0, 0, 0, 0}, zAccBuf = {0, 0, 0, 0}; //NOPMD

    /**
     * The <code> gestures </code> that can be recognized.
     */
    private final Gesture clockwise, counterclockwise;

    /**
     * Sampling with rate 1000 / 50 ( samples / second ).
     */
    private static final int MILLIS_PER_SAMPLE = 1000 / 50;

    /**
     * Sine operator for the convolution computation.
     */
    private final int[] sineOperator = {0, 12, 26, 38, 51, 62, 72, 80, 87, 93, 96, 98,
            98, 97, 94, 88, 81, 73, 63, 52, 41, 28, 14, 1, -11, -24, -36, -49, -60, -70, -79, -86, -92, -96,
            -98, -98, -97, -94, -89, -82, -74, -64, -54, -42, -29, -16, -3, 9, 22, 35};

    /**
     * Cosine operator for the convolution computation.
     */
    private final int[] cosineOperator = {100, 99, 96, 91, 85, 77, 68, 57, 45, 33, 20, 7, -5, -18, -32, -44, -56,
            -66, -76, -83, -90, -95, -98, -98, -98, -96, -91, -85, -78, -69, -58, -47, -35, -22, -9, 4, 16, 29,
            42, 53, 64, 74, 82, 89, 94, 97, 98, 98, 96, 92};

    /**
     * <code> GestureListener </code> that listens for any completed gesture.
     */
    private final GestureListener gestureListener;

    /**
     * Accelerometer instance.
     */
    private final LIS3L02AQAccelerometer acc;

    /**
     * Sunspot's ISwitch 1.
     */
    private final ISwitch switch1 = EDemoBoard.getInstance().getSwitches()[EDemoBoard.SW1];

    /**
     * Sunspot's ISwitch 2.
     */
    private final ISwitch switch2 = EDemoBoard.getInstance().getSwitches()[EDemoBoard.SW2];

    private boolean isEnabled = true;

    /**
     * Constructor for a new instance of <code> GestureRecogniser </code>.
     *
     * @param sampleSize    The numbers of samples that can be buffered
     * @param gestureListen The GestureListener that listens for gesture completion
     * @throws IOException if unable to access accelerometer
     */
    public GestureRecogniser(final int sampleSize, final GestureListener gestureListen) throws IOException {
        this.gestureListener = gestureListen;
        xBuf = new CircularBuffer(sampleSize);
        yBuf = new CircularBuffer(sampleSize);
        zBuf = new CircularBuffer(sampleSize);

        /**
         * The Counterclockwise gesture is a combination of two <code> Convolutions </code>.
         * The first <code> Convolution </code> is a combination of Raw in Z-Axis and sineOperator,
         * and the second <code> Convolution </code> is a combination of Raw in X-Axis and cosineOperator.
         */
        counterclockwise = new Gesture(new Convolution(zBuf, sineOperator), new Convolution(xBuf, cosineOperator));

        /**
         * The Clockwise gesture is a combination of two <code> Convolutions </code>.
         * The first <code> Convolution </code> is a combination of Raw in Z-Axis and cosineOperator,
         * and the second <code> Convolution </code> is a combination of Raw in X-Axis and sineOperator.
         */
        clockwise = new Gesture(new Convolution(zBuf, cosineOperator), new Convolution(xBuf, sineOperator));

        /** Notice the relation between Clockwise and Counterclockwise gestures
         * Remember that -sine is an differentiated cosine
         * and cosine is an differentiated sine
         */
        acc = (LIS3L02AQAccelerometer) EDemoBoard.getInstance().getAccelerometer();

        // Sets the desirable scale of the accelerometer
        acc.setScale(LIS3L02AQAccelerometer.SCALE_6G);


    }

    /**
     * Constructor for a new instance of <code> GestureRecogniser </code>.
     * The sample size is set to the DEFAULT_SAMPLESIZE value.
     *
     * @param gestureListen The GestureListener that listens for gesture completion
     * @throws IOException if unable to access accelerometer
     */
    public GestureRecogniser(final GestureListener gestureListen) throws IOException {
        this(DEF_SAMPLESIZE, gestureListen);
    }

    /**
     * Collects the samples and checks if a recognizable gesture have been completed.
     */
    public void run() { //NOPMD
        // Current time in millis
        long last = System.currentTimeMillis();
        int index = 0;
        long lastTimestamp = 0;
        boolean moved = true;
        double gAcc;
        boolean tiltDone = false;
        final int gravityOffset = 62;
        try {
            while (isEnabled) {
                // Sampling per MILLIS_PER_SAMPLE
                final long now = System.currentTimeMillis();
                // If the sampling rate time is not passed
                if (now < (last + MILLIS_PER_SAMPLE)) {
                    Utils.sleep(last + MILLIS_PER_SAMPLE - now); // Wait for some millis
                }
                // Refresh the current time
                last = now;

                // Get Raw Samples for all Axis
                xBuf.setData(acc.getRawX());
                yBuf.setData(acc.getRawY());
                zBuf.setData(acc.getRawZ() + gravityOffset); // Adjustment for gravity zero offset for 6g is 62


                xAccBuf[index % 4] = acc.getAccelX();
                yAccBuf[index % 4] = acc.getAccelY();
                zAccBuf[index % 4] = acc.getAccelZ();
                index++;

                if (Math.abs(xAccBuf[0]) > 5.0 && Math.abs(xAccBuf[1]) > 5.0
                        && Math.abs(xAccBuf[2]) > 5.0 && Math.abs(xAccBuf[3]) > 5.0
                        ) {


                    // Report that a Right Violent Movement has been completed
                    gestureListener.doHorizontalViolentMovement();
                    //tiltDone = false;

                    // Clear the buffers for new computations
                    xAccBuf[0] = 0;
                    xAccBuf[1] = 0;
                    xAccBuf[2] = 0;
                    xAccBuf[3] = 0;

                    resetBuffers();

                } else if (Math.abs(zAccBuf[0]) > 5.0 && Math.abs(zAccBuf[1]) > 5.0
                        && Math.abs(zAccBuf[2]) > 5.0 && Math.abs(zAccBuf[3]) > 5.0) {
                    gestureListener.doVerticalViolentMovement();
                    zAccBuf[0] = 0;
                    zAccBuf[1] = 0;
                    zAccBuf[2] = 0;
                    zAccBuf[3] = 0;

                    //tiltDone = false;
                    resetBuffers();


                } else if (Math.abs(yAccBuf[0]) > 5.0 && Math.abs(yAccBuf[1]) > 5.0
                        && Math.abs(yAccBuf[2]) > 5.0 && Math.abs(yAccBuf[3]) > 5.0) {

                    gestureListener.doForwardViolentMovement();
                    yAccBuf[0] = 0;
                    yAccBuf[1] = 0;
                    yAccBuf[2] = 0;
                    yAccBuf[3] = 0;

                    //tiltDone = false;
                    resetBuffers();

                } else if (now - lastTimestamp > 180000) {

                    // Report that no movement has occured
                    gestureListener.noMovement();
                    lastTimestamp = now;
                }

                // Check if it moved
                if (Math.abs(acc.getAccelX()) > 1.2 || Math.abs(acc.getAccelY()) > 1.2 || Math.abs(acc.getAccelZ()) > 1.2) {
                    moved = true;
                    // Report that a movement has occured
                    gestureListener.doAnyMovement();
                    lastTimestamp = now;
                }

                resetBuffers();

            }
            // Increase the size of buffers
            xBuf.increment();
            yBuf.increment();
            zBuf.increment();

        }

        catch (Exception ex) {
            Logger.getInstance().debug(ex.toString());
            //ex.printStackTrace();
        }

    }

    /**
     * Resets the contents of the buffers in X,Y,Z axis
     * and the contents of Clockwise and Counterclockwise gesture.
     */
    private void resetBuffers() {
        xBuf.reset();
        yBuf.reset();
        zBuf.reset();
        clockwise.reset();
        counterclockwise.reset();
    }

    /**
     * Returns the current power of the <code> Clockwise </code> <code> Gesture </code>.
     *
     * @return the current power of the <code> Clockwise </code> <code> Gesture </code>
     */
    public double getClockwisePower() {
        return clockwise.getPower();
    }

    /**
     * Returns the current power of the <code> Counterclockwise </code> <code> Gesture </code>.
     *
     * @return the current power of the <code> Counterclockwise </code> <code> Gesture </code>
     */
    public double getCounterclockwisePower() {
        return counterclockwise.getPower();
    }

    public void killRecognizer() {
        isEnabled = false;
    }
}
