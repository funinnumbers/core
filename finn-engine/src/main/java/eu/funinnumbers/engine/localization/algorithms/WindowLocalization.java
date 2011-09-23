package eu.funinnumbers.engine.localization.algorithms;

import eu.funinnumbers.db.model.localization.Point;
import eu.funinnumbers.engine.localization.Localization;
import eu.funinnumbers.engine.localization.LocalizationData;
import eu.funinnumbers.engine.localization.NDPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Compute the Average RSSI values of last N values.
 * <p>This class is used from the others implemented Algorithms.
 */
public class WindowLocalization implements Localization { //NOPMD

    private final int windowSize;


    /**
     * The size of the window.
     *
     * @param size
     */
    public WindowLocalization(final int size) {
        windowSize = size;
    }

    /**
     * Not Implemented.
     *
     * @param macAddress of the device.
     * @return
     */
    public Float getX(final String macAddress) {
        return null;
    }

    /**
     * Not Implemented.
     *
     * @param macAddress of the device.
     * @return
     */
    public Point getCoordinates(final String macAddress) {
        return null;
    }

    /**
     * Not Implemented.
     *
     * @param currentNDPoint of the device.
     * @return
     */
    public Point getCoordinates(final NDPoint currentNDPoint) {
        return null;
    }

    /**
     * Not Implemented.
     *
     * @param currentNDPoint of the device.
     * @param pointIDs       where to search for the currentNDPoint  coordinates.
     * @return
     */
    public Point getCoordinates(final NDPoint currentNDPoint, final Vector<Integer> pointIDs) {
        return null;
    }

    /**
     * Returns the NDPoint constructed from the last N RSSI values.
     *
     * @param macAddress of the device.
     * @return the NDpoint of the device.
     */
    public NDPoint getCurrentNDpoint(final String macAddress) { //NOPMD
        // Get the latest readings for this eu.funinnumbers.guardian
        final HashMap<String, List<Integer>> latestReadings =
                LocalizationData.getInstance().getValues(windowSize, macAddress, LocalizationData.RSSI);


        // We need to find out the corresponding coordinate for each eu.funinnumbers.station

        if (!latestReadings.isEmpty()) {
            // Create a Vector big enough for all the NDPoint coordinates
            final Vector<Float> ndPointCoords = new Vector<Float>(LocalizationData.getInstance().getStationCoordinates().size());

            // Expand Vector's size
            for (int i = 0; i < latestReadings.size(); i++) {
                ndPointCoords.add(LocalizationData.MIN_RSSI);
            }

            int maxSize = 0;
            //Find the actual size of the window.
            for (String station : LocalizationData.getInstance().getStationCoordinates().keySet()) {
                if (latestReadings.get(station).size() > maxSize) {
                    maxSize = latestReadings.get(station).size();
                }
            }

            // Get the rssi values from all stations
            for (String station : LocalizationData.getInstance().getStationCoordinates().keySet()) {

                if (!latestReadings.get(station).isEmpty()) {

                    // Find the X coordinate of this eu.funinnumbers.station
                    final int dimension = LocalizationData.getInstance().getStationCoordinates(station).getId();

                    //find the average RSSI
                    final List<Integer> rssiValues = latestReadings.get(station);

                    Float sumRssi = 0f;
                    for (Integer rssiValue : rssiValues) {
                        sumRssi += rssiValue.floatValue();
                    }
                    if (rssiValues.size() < maxSize) {
                        sumRssi += ((maxSize - rssiValues.size()) * LocalizationData.MIN_RSSI);
                    }
                    final Float avgRssi = sumRssi / maxSize;
                    // put the value to the vector
                    ndPointCoords.set(dimension - 1, avgRssi.floatValue());
                }
            }
            // Create the N-D point according to the previous values
            return new NDPoint(ndPointCoords);
        }
        return new NDPoint(LocalizationData.getInstance().getStationCoordinates().size());
    }

}
