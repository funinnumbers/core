package eu.funinnumbers.guardian.ui.gestures;

/*
 * Gesture.java
 *
 */

/**
 * The <code> Gesture </code> is consisted of two <code> Convolutions </code>
 * and the power of the <code> Gesture </code>.
 */
public class Gesture {

    /**
     * Convolution A.
     */
    private final Convolution convolutionA;

    /**
     * Convolution B.
     */
    private final Convolution convolutionB;

    /**
     * Power of gesture.
     */
    private double power = 0;

    /**
     * Constructor for a new instance of <code> Gesture </code>.
     *
     * @param convA The first <code> Convolution </code> that is needed for a gesture
     * @param convB The second <code> Convolution </code> that is needed for a gesture
     */
    public Gesture(final Convolution convA, final Convolution convB) {
        this.convolutionA = convA;
        this.convolutionB = convB;
    }

    /**
     * Recalculates the values of the <code> Convolutions </code> regarding the
     * new samples that arrived.
     */
    public void recalc() {
        convolutionA.increment();
        final double oldConvA = convolutionA.getCurrentValue();
        convolutionA.recalc();
        final double newConvA = convolutionA.getCurrentValue();

        convolutionB.increment();
        final double oldConvB = convolutionB.getCurrentValue();
        convolutionB.recalc();
        final double newConvB = convolutionB.getCurrentValue();

        final double oldProd = oldConvA * oldConvB;
        final double newProd = newConvA * newConvB;
        power = power - (oldProd < 0 ? 0 : oldProd) + (newProd < 0 ? 0 : newProd);
    }

    /**
     * Get the power of the current <code> Gesture </code>.
     *
     * @return the power of the current <code> Gesture </code>
     */
    public double getPower() {
        return power;
    }

    /**
     * Reset the contents of the current <code> Gesture </code>.
     */
    public void reset() {
        convolutionA.reset();
        convolutionB.reset();
        power = 0;
    }
}
