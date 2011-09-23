package eu.funinnumbers.guardian.ui.gestures;

/*
 * CircularBuffer.java
 *
 */

/**
 * The <code> CircularBuffer </code> is consisted of one dimensinal array that stores data
 * and an index that indexes the current position of the <code> CircularBuffer </code>.
 */
public class CircularBuffer {

    /**
     * One dimensinal array that stores data of the <code> CircularBuffer </code>.
     */
    private int[] data;

    /**
     * Index that show the current position of the <code> CircularBuffer </code>.
     */
    private int index;

    /**
     * Constructor for a new instance of <code> CircularBuffer </code>.
     *
     * @param size The size of the <code> CircularBuffer </code>
     */
    public CircularBuffer(final int size) {
        data = new int[size];
        index = 0;
    }

    /**
     * This method stores data in the <code> CircularBuffer </code> in the index-th position of the data array.
     *
     * @param aValue The value of the inserted data in the <code> CircularBuffer </code>
     */
    public void setData(final int aValue) {
        data[index] = aValue;
    }

    /**
     * This method returns the data from the <code> CircularBuffer </code> that
     * is store in the offsetFromCurrentPosition-th position of the data array.
     *
     * @param offset The offsetFromCurrentPosition is the position of the desirable data array
     * @return the data in the indicated position of the data array
     */
    public int getData(final int offset) {
        int newIndex = index + offset;
        if (newIndex > data.length - 1) {
            newIndex = newIndex - data.length;
        }
        if (newIndex < 0) {
            newIndex = newIndex + data.length;
        }
        return data[newIndex];
    }

    /**
     * This method returns increases the current position of the <code> CircularBuffer </code>.
     */
    public void increment() {
        if (++index == data.length) {
            index = 0;
        }
    }

    /**
     * This method returns the length of the data array of the <code> CircularBuffer </code>.
     *
     * @return the length of the data array of the <code> CircularBuffer </code>
     */
    public int length() {
        return data.length;
    }

    /**
     * This method resets the contents of the data array of the <code> CircularBuffer </code>.
     */
    public void reset() {
        for (int i = 0; i < data.length; i++) {
            data[i] = 0;
        }
    }
}
