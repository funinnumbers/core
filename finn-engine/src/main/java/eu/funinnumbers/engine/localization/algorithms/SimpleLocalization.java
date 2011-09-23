package eu.funinnumbers.engine.localization.algorithms;

import eu.funinnumbers.db.model.localization.Point;
import eu.funinnumbers.engine.localization.Localization;
import eu.funinnumbers.engine.localization.LocalizationData;
import eu.funinnumbers.engine.localization.NDPoint;
import eu.funinnumbers.util.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class SimpleLocalization implements Localization { //NOPMD

    /**
     * Sample length.
     */
    private static final int SAMP_LENGTH = 5;

    /**
     * Returns the X coordinate of a eu.funinnumbers.guardian.
     * X coordinate corresponds to the pre-mapped coordinate of the eu.funinnumbers.station with the best reception.
     *
     * @param macAddress the eu.funinnumbers.guardian's mac address to be localized
     * @return the pre-mapped X coordinate of the eu.funinnumbers.station
     */
    public Float getX(final String macAddress) {
        return getCoordinates(macAddress).getX();
    }

    /**
     * Returns the current NDPoint of a device.
     *
     * @param macAddress of the device.
     * @return The NDPoint.
     */
    public NDPoint getCurrentNDpoint(final String macAddress) { //NOPMD
        // Get the latest readings for this eu.funinnumbers.guardian
        final HashMap<String, List<Integer>> latestReadings =
                LocalizationData.getInstance().getValues(SAMP_LENGTH, macAddress, LocalizationData.RSSI);

        // We need to find out the corresponding coordinate for each eu.funinnumbers.station
        /************************************************************************/
        // Assume that the value X of the eu.funinnumbers.station's coordinate corresponds to the
        // X-th coordinate of the NDPoint
        /************************************************************************/

        if (!latestReadings.isEmpty()) {
            // Create a Vector big enough for all the NDPoint coordinates
            final Vector<Float> ndPointCoords = new Vector<Float>(LocalizationData.getInstance().getStationCoordinates().size());

            // Expand Vector's size
            for (int i = 0; i < latestReadings.size(); i++) {
                ndPointCoords.add(LocalizationData.MIN_RSSI);
            }

            // Get the rssi values from all stations
            for (String station : LocalizationData.getInstance().getStationCoordinates().keySet()) {

                if (!latestReadings.get(station).isEmpty()) {

                    // Find the X coordinate of this eu.funinnumbers.station
                    final int dimension = LocalizationData.getInstance().getStationCoordinates(station).getId();

                    Integer avgRssi = 0;
                    for (Integer rssiValue : latestReadings.get(station)) {
                        avgRssi += rssiValue;
                    }
                    // put the value to the vector
                    ndPointCoords.set(dimension - 1, avgRssi.floatValue() / latestReadings.get(station).size());
                }
            }
            // Create the N-D point according to the previous values
            return new NDPoint(ndPointCoords);
        }
        return new NDPoint(LocalizationData.getInstance().getStationCoordinates().size());
    }

    /**
     * Returns the coordinates of a device.
     *
     * @param macAddress of the device.
     * @return The coordinates of the device.
     */
    public Point getCoordinates(final String macAddress) {

        final NDPoint currentNDPoint = getCurrentNDpoint(macAddress);
        Logger.getInstance().debug(currentNDPoint.getCoordinates());
        //Check if NDPoint is null. (All coordinates are -50).
        boolean pointIsNull = true;
        for (Float rssi : currentNDPoint.getCoordinates()) {
            if (!rssi.equals(LocalizationData.MIN_RSSI)) {
                pointIsNull = false;
                break;
            }
        }
        //If NDPoint isNull return an empty Point.
        if (pointIsNull) {
            return new Point();
        }

        return findClosestPoint(currentNDPoint);

    }

    /**
     * Returns the coordinates of a NDPoint.
     *
     * @param currentNDPoint of the device.
     * @return The coordinates of the device.
     */
    public Point getCoordinates(final NDPoint currentNDPoint) {
        return findClosestPoint(currentNDPoint);
    }

    /**
     * Returns the Closest Point of a eu.funinnumbers.guardian with the best reception.
     *
     * @param currentNDPoint
     * @return the Station's Point with the best reception
     */
    private Point findClosestPoint(final NDPoint currentNDPoint) {

        final Float maxRssi = Collections.max(currentNDPoint.getCoordinates());

        final Integer xCoord = currentNDPoint.getCoordinates().indexOf(maxRssi);

        return new Point(Float.valueOf(xCoord) + 1, xCoord + 1);
    }

    /**
     * Not Implemented in this Algorithm.
     *
     * @param currentNDPoint of the device.
     * @param pointIDs       where to search for the currentNDPoint  coordinates.
     * @return
     */
    @Deprecated
    public Point getCoordinates(final NDPoint currentNDPoint, final Vector<Integer> pointIDs) {
        return new Point();
    }


}