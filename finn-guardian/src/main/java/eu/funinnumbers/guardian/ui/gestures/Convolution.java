package eu.funinnumbers.guardian.ui.gestures;

/*
 * Convolution.java
 *
 */

/**
 * The <code> Convolution </code> is consisted of two <code> CircularBuffers </code>
 * and an one dimensional array.
 */
public class Convolution {

    /**
     * A <code> CircularBuffer </code> for the input signal.
     */
    private final CircularBuffer signal;

    /**
     * An one dimensional array  for the operator of the system (Like Impulse Response).
     */
    private final int[] operator;

    /**
     * A <code> CircularBuffer </code> for the convolution (Output signal).
     */
    private final CircularBuffer buffer;

    /**
     * Constructor for a new instance of <code> Convolution </code>.
     *
     * @param thisSignal   An <code> CircularBuffer </code> for the input signal
     * @param thisOperator The operator of the system
     */
    public Convolution(final CircularBuffer thisSignal, final int[] thisOperator) { // NOPMD
        signal = thisSignal;
        operator = thisOperator;
        buffer = new CircularBuffer(thisSignal.length() - thisOperator.length);
    }

    /**
     * This method returns increases the current position of the convolution <code> CircularBuffer </code>.
     */
    public void increment() {
        buffer.increment();
    }

    /**
     * This method returns the data from the convolution <code> CircularBuffer </code> that
     * is store in the 0-th position of the data array.
     *
     * @return the data in the 0-th position of the data array
     */
    public int getCurrentValue() {
        return buffer.getData(0);
    }

    /**
     * Recalculates the values of the convolution regarding the
     * new samples that arrived.
     */
    public void recalc() {
        int result = 0;
        for (int i = 0; i < operator.length; i++) {
            result += (signal.getData(0 - i) * operator[i]) / 100;
        }
        buffer.setData(result / operator.length);
    }

    /**
     * This method resets the contents of the convolution.
     */
    public void reset() {
        buffer.reset();
    }
}
