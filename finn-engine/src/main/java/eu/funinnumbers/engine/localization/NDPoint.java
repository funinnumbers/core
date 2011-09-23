package eu.funinnumbers.engine.localization;

import java.io.Serializable;
import java.util.Vector;

public class NDPoint implements Serializable, Comparable<NDPoint> {
    public static final float MIN_VALUE = -50;
    /**
     * The array containing the coordinates for this Point.
     */
    private Vector<Float> coordinates; //NOPMD

    /**
     * The id of this NDPoint.
     */
    private Integer id;

    /**
     * A SequenceNumber.
     */
    private int sequenceNumber;

    /**
     * Constructs a n-d point with the given coordinates.
     *
     * @param coords as Vector.
     */
    public NDPoint(final Vector<Float> coords) {
        coordinates = new Vector<Float>(coords);
        sequenceNumber = -1;
    }


    /**
     * Constructs a n-d point with the given coordinates and the giver id.
     *
     * @param coords as Vector.
     * @param id     as Integer.
     */
    public NDPoint(final Vector<Float> coords, final Integer id) {
        coordinates = new Vector<Float>(coords);
        this.id = id;
        sequenceNumber = -1;
    }


    /**
     * Default constructor.
     */
    public NDPoint() {
        coordinates = new Vector<Float>();
        sequenceNumber = -1;
    }

    /**
     * Constructor with size parameter.
     */
    public NDPoint(final int size) {
        sequenceNumber = -1;
        coordinates = new Vector<Float>();
        for (int i = 0; i < size; i++) {
            coordinates.add(MIN_VALUE);
        }
    }

    /**
     * Returns the coordinates of the points.
     *
     * @return the coordinates as array of integers.
     */
    public Vector<Float> getCoordinates() {
        return coordinates;
    }

    /**
     * Calulates the distance of this point to another point.
     *
     * @param pointB the other point.
     * @return the distance of Float.
     */
    public Double euclidianDistance(final NDPoint pointB) {
        // Check if dimensions of the two points match.
        if (this.coordinates.size() == pointB.coordinates.size()) {
            int sum = 0;
            for (int index = 0; index < coordinates.size(); index++) {
                sum += Math.pow(this.coordinates.get(index) - pointB.coordinates.get(index), 2);
            }
            // Calculate distance.
            return Math.sqrt(sum);
        }
        // Dimensions do not match
        return Double.MIN_VALUE;

    }

    /**
     * Set's the value of a specific coordinate.
     *
     * @param dimension the dimension
     * @param value     the value to be set.
     */
    public void setCoordinate(final int dimension, final Integer value) {
        this.coordinates.set(dimension, value.floatValue());
    }

    /**
     * Sets the id of this NDPoint.
     *
     * @param id as Integer
     */
    public void setId(final Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(final int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     */
    public int compareTo(NDPoint o) {
        return this.getSequenceNumber() - o.getSequenceNumber();
    }

    /**
     * Determines whether or not two points are equal. Two instances of
     * <code>Point</code> are equal if the values of their
     * <code>x</code> and <code>y</code> member fields, representing
     * their position in the coordinate space, are the same.
     *
     * @param obj an object to be compared with this <code>Point2D</code>
     * @return <code>true</code> if the object to be compared is
     *         an instance of <code>Point2D</code> and has
     *         the same values; <code>false</code> otherwise.
     */
    public boolean equals(final Object obj) {
        if (obj instanceof NDPoint) {
            final NDPoint pt = (NDPoint) obj;
            return (id.equals(pt.id)) && (sequenceNumber == pt.sequenceNumber);
        }
        return super.equals(obj);
    }


}

