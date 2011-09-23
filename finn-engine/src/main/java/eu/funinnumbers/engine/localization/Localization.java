package eu.funinnumbers.engine.localization;

import eu.funinnumbers.db.model.localization.Point;

import java.util.Vector;

/**
 * Localization interface.
 */
public interface Localization {

    /**
     * Returns the coordinate X of a device.
     *
     * @param macAddress of the device.
     * @return The coordinate of the device.
     */
    public Float getX(final String macAddress);

    /**
     * Returns the coordinates of a device.
     *
     * @param macAddress of the device.
     * @return The coordinates of the device.
     */
    public Point getCoordinates(final String macAddress);

    /**
     * Returns the coordinates of a NDPoint.
     *
     * @param currentNDPoint of the device.
     * @return The coordinates of the device.
     */
    public Point getCoordinates(final NDPoint currentNDPoint);

    /**
     * Returns the coordinates of a NDPoint which is closest to specific Points.
     *
     * @param currentNDPoint of the device.
     * @param pointIDs       where to search for the currentNDPoint  coordinates.
     * @return The coordinates of the device.
     */
    public Point getCoordinates(final NDPoint currentNDPoint, final Vector<Integer> pointIDs);

    /**
     * Returns the current NDPoint of a device.
     *
     * @param macAddress of the device.
     * @return The NDPoint.
     */
    public NDPoint getCurrentNDpoint(final String macAddress);

}
