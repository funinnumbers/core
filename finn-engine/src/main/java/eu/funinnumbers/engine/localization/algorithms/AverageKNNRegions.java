package eu.funinnumbers.engine.localization.algorithms;

import eu.funinnumbers.db.model.localization.Point;
import eu.funinnumbers.engine.localization.Localization;
import eu.funinnumbers.engine.localization.NDPoint;
import eu.funinnumbers.engine.localization.LocalizationData;

import java.util.HashMap;
import java.util.Vector;

/**
 * Localization Algorithm using KNN and DatabaseLocalization for regions and KNNLocalization for points.
 */
public class AverageKNNRegions implements Localization {

    /**
     * DatabaseLocalization Algorithm for regions.
     */
    private final DatabaseLocalization dbRegion = new DatabaseLocalization();

    /**
     * KNNLocalization Algorithm for regions.
     */
    private final KNNLocalization knnRegion = new KNNLocalization();

    /**
     * KNNLocalization Algorithm for points in a specific region.
     */
    private final KNNLocalization knnPoints = new KNNLocalization();

    /**
     * <Region_ID, Vector<Point_ID>>.
     */
    private final HashMap<Integer, Vector<Integer>> regionPointsMap = new HashMap<Integer, Vector<Integer>>();

    /**
     * Returns the coordinate X of a device.
     *
     * @param macAddress of the device.
     * @return The coordinate of the device.
     */
    public Float getX(final String macAddress) {
        return getCoordinates(macAddress).getX();
    }

    /**
     * Returns the coordinates of a device.
     *
     * @param macAddress of the device.
     * @return The coordinates of the device.
     */
    public Point getCoordinates(final String macAddress) {

        final NDPoint currentNDPoint = dbRegion.getCurrentNDpoint(macAddress);

        boolean isNull = true;
        for (Float rssi : currentNDPoint.getCoordinates()) {
            if (!rssi.equals(LocalizationData.MIN_RSSI)) {
                isNull = false;
                break;
            }
        }
        if (isNull)
            return new Point();

        final Point currentDBRegion = dbRegion.getCoordinates(currentNDPoint);

        final Point currentKNNRegion = knnRegion.getCoordinates(currentNDPoint);

        final int averageRegionID = (currentDBRegion.getId() + currentKNNRegion.getId()) / 2;

        return knnPoints.getCoordinates(currentNDPoint, regionPointsMap.get(averageRegionID));
    }

    /**
     * Load Trained Values.
     *
     * @param regionsFile the region trained values.
     * @param pointsFile  the points trained values.
     */
    public void loadTrainedValues(final String regionsFile, final String pointsFile) {
        dbRegion.loadTrainedValues(regionsFile);
        knnRegion.loadTrainedValues(regionsFile);
        knnPoints.loadTrainedValues(pointsFile);
    }

    /**
     * Associate a Point with a specific Region.
     *
     * @param pointId  the point ID.
     * @param regionId the region ID.
     */
    public final void associatePointToRegion(final Integer pointId, final Integer regionId) {
        if (regionPointsMap.containsKey(regionId)) {
            regionPointsMap.get(regionId).add(pointId);
        } else {
            final Vector<Integer> points = new Vector<Integer>();
            points.add(pointId);
            regionPointsMap.put(regionId, points);
        }
    }

    /**
     * Not Implemented.
     *
     * @param currentNDPoint of the device.
     * @return null.
     */
    public Point getCoordinates(final NDPoint currentNDPoint) {
        return null;
    }

    /**
     * Not Implemented.
     *
     * @param currentNDPoint of the device.
     * @param pointIDs       where to search for the currentNDPoint  coordinates.
     * @return null.
     */
    public Point getCoordinates(final NDPoint currentNDPoint, final Vector<Integer> pointIDs) {
        return null;
    }


    /**
     * Not Implemented.
     *
     * @param macAddress of the device.
     * @return null.
     */
    public NDPoint getCurrentNDpoint(final String macAddress) {
        return null;
    }
}

