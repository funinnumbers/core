package eu.funinnumbers.db.model.localization;

import java.io.Serializable;

public class Point implements Serializable {

    /**
     * Required by Serializable interface.
     */
    static final long serialVersionUID = 4342L; //NOPMD

    /**
     * The X coordinate.
     */
    private Float coordX;

    /**
     * The Y coordinate.
     */
    private Float coordY;


    /**
     * The Z coordinate.
     */
    private Float coordZ;


    /**
     * The id of this point.
     */
    private Integer id;

    /**
     * Returns the X coordinate for this point.
     *
     * @return X coordinate as Float.
     */
    public Float getX() {
        return coordX;
    }

    /**
     * Returns the Y coordinate for this point.
     *
     * @return Y coordinate as Float.
     */
    public Float getY() {
        return coordY;
    }


    public Integer getId() {
        return id;
    }


    /**
     * Returns the Z coordinate for this point.
     *
     * @return Z coordinate as Float.
     */
    public Float getZ() {
        return coordZ;
    }


    public Point() {
        coordX = (float) -50;
        coordY = (float) -50;
        coordZ = (float) -50;
    }

    /**
     * Creates a 3-D point.
     *
     * @param coordXParam value of X
     * @param coordYParam value of Y
     * @param coordZParam value of Z
     */
    public Point(final Float coordXParam, final Float coordYParam, final Float coordZParam, final Integer id) {
        coordX = coordXParam;
        coordY = coordYParam;
        coordZ = coordZParam;
        this.id = id;
    }

    /**
     * Creates a 2-D point.
     *
     * @param coordXParam value of X.
     * @param coordYParam value of Y.
     */
    public Point(final Float coordXParam, final Float coordYParam, final Integer id) {
        coordX = coordXParam;
        coordY = coordYParam;
        this.id = id;
    }

    /**
     * Creates a 1-D point.
     *
     * @param coordXParam value of X.
     */
    public Point(final Float coordXParam, final Integer id) {
        coordX = coordXParam;
        this.id = id;
    }


    /**
     * Creates a 3-D point.
     *
     * @param coordXParam value of X
     * @param coordYParam value of Y
     * @param coordZParam value of Z
     */
    public Point(final Float coordXParam, final Float coordYParam, final Float coordZParam) {
        coordX = coordXParam;
        coordY = coordYParam;
        coordZ = coordZParam;
    }

    /**
     * Creates a 2-D point.
     *
     * @param coordXParam value of X.
     * @param coordYParam value of Y.
     */
    public Point(final Float coordXParam, final Float coordYParam) {
        coordX = coordXParam;
        coordY = coordYParam;
    }

    /**
     * Creates a 1-D point.
     *
     * @param coordXParam value of X.
     */
    public Point(final Float coordXParam) {
        coordX = coordXParam;
    }


    /**
     * Sets the value of X-Point.
     *
     * @param coordXParam value of X.
     */
    public void setX(final Float coordXParam) {
        coordX = coordXParam;

    }

    /**
     * Sets the value of Y-Point.
     *
     * @param coordYParam value of Y.
     */
    public void setY(final Float coordYParam) {
        coordY = coordYParam;

    }

    /**
     * Sets the value of X-Point.
     *
     * @param coordZParam value of X.
     */
    public void setZ(final Float coordZParam) {
        coordZ = coordZParam;

    }

    /**
     * Sets the id of this point.
     *
     * @param id
     */
    public void setId(final Integer id) {
        this.id = id;
    }

    public boolean isNull() {
        return getX().equals(getY()) &&  (getY().equals(getZ())) && getZ().equals(-50f);
    }
}

