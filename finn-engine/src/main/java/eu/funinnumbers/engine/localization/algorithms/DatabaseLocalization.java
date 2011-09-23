package eu.funinnumbers.engine.localization.algorithms;

import eu.funinnumbers.db.model.localization.Point;
import eu.funinnumbers.engine.localization.Localization;
import eu.funinnumbers.engine.localization.LocalizationData;
import eu.funinnumbers.engine.localization.NDPoint;
import eu.funinnumbers.util.Logger;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;

/**
 *
 */
public class DatabaseLocalization implements Localization { //NOPMD

    /**
     * Each Point corresponds to some NDPoint after training.
     */
    private final HashMap<Point, NDPoint> DatabasePoints;

    /**
     * Default Constructor.
     */
    public DatabaseLocalization() {
        DatabasePoints = new HashMap<Point, NDPoint>();
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
                LocalizationData.getInstance().getValues(1, macAddress, LocalizationData.RSSI);


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

                    // put the value to the vector
                    ndPointCoords.set(dimension - 1, latestReadings.get(station).get(0).floatValue());
                }
            }
            // Create the N-D point according to the previous values
            return new NDPoint(ndPointCoords);
        }
        return new NDPoint(LocalizationData.getInstance().getStationCoordinates().size());
    }

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

        final NDPoint currentNdPoint = getCurrentNDpoint(macAddress);

        //Check if NDPoint is null. (All coordinates are -50).
        boolean pointIsNull = true;
        for (Float rssi : currentNdPoint.getCoordinates()) {
            if (!rssi.equals(LocalizationData.MIN_RSSI)) {
                pointIsNull = false;
                break;
            }
        }
        //If NDPoint isNull return an empty Point.
        if (pointIsNull) {
            return new Point();
        }

        // Find the distances between this N-D point and all the saved ones
        return findClosestPoints(currentNdPoint, DatabasePoints).get(0);
    }

    /**
     * Returns the coordinates of a NDPoint.
     *
     * @param currentNDPoint of the device.
     * @return The coordinates of the device.
     */
    public Point getCoordinates(final NDPoint currentNDPoint) {
        return findClosestPoints(currentNDPoint, DatabasePoints).get(0);
    }

    /**
     * Returns the coordinates of a NDPoint which is closest to specific Points.
     *
     * @param currentNDPoint of the device.
     * @param pointIDs       where to search for the currentNDPoint  coordinates.
     * @return The coordinates of the device.
     */
    public Point getCoordinates(final NDPoint currentNDPoint, final Vector<Integer> pointIDs) {

        final HashMap<Point, NDPoint> points = new HashMap<Point, NDPoint>();
        for (Point point : DatabasePoints.keySet()) {
            if (pointIDs.contains(point.getId())) {
                points.put(point, DatabasePoints.get(point));
            }
        }
        // Find the closest point
        return findClosestPoints(currentNDPoint, points).get(0);

    }

    /**
     * Finds the ND point that is closest to current ND point.
     *
     * @param ndPoint the ND point of the device.
     * @param points  points where to search the ND point.
     * @return String mac address of the closest eu.funinnumbers.station.
     */
    private List<Point> findClosestPoints(final NDPoint ndPoint, final HashMap<Point, NDPoint> points) {
        final HashMap<Point, Double> distances = new HashMap<Point, Double>();

        // Iterate points from database
        for (Point point : points.keySet()) {
            // Calculate euclidean distance between currentNdPoint and that from database
            distances.put(point, ndPoint.euclidianDistance(points.get(point)));
        }
        return sortByValueAsc(distances);
    }

    /**
     * @param filename
     */
    public void loadTrainedValues(final String filename) { //NOPMD

        HashMap<Point, Vector<NDPoint>> extractedPoints = new HashMap<Point, Vector<NDPoint>>();

        try {
            final FileInputStream in = new FileInputStream(filename);

            final ObjectInputStream s = new ObjectInputStream(in);
            extractedPoints = (HashMap<Point, Vector<NDPoint>>) s.readObject();
            s.close();
            in.close();


            Logger.getInstance().debug(extractedPoints.size() + " trained points loaded succefully.");


            int ndPointDimensions = -1;
            // Extract NDPoints for each gridPoint
            for (Point point : extractedPoints.keySet()) {


                final Vector<NDPoint> ndPointsVec = extractedPoints.get(point);

                // Define the dimensions of this NDPoint (i.e. number of Anchors used)
                if (ndPointDimensions == -1) {
                    ndPointDimensions = ndPointsVec.get(0).getCoordinates().size();
                    Logger.getInstance().debug("Dimensions of NDPoints are " + ndPointDimensions);
                }

                // Initialize the average coordinates Vector
                final Vector<Float> avgCoordinates = new Vector<Float>(); //NOPMD

                final Integer[][] coordArray = new Integer[ndPointsVec.size()][ndPointDimensions]; //NOPMD
                for (int i = 0; i < ndPointsVec.size(); i++) {
                    for (int j = 0; j < ndPointDimensions; j++) {
                        coordArray[i][j] = 0;
                    }
                }


                int row = 0;
                for (NDPoint ndPoint : ndPointsVec) {
                    Logger.getInstance().debug("ndPoint has " + ndPoint.getCoordinates());
                    for (int col = 0; col < ndPoint.getCoordinates().size(); col++) {
                        coordArray[row][col] = ndPoint.getCoordinates().get(col).intValue();
                    }
                    row++;
                }


                // Calculate the actual average
                for (int j = 0; j < ndPointDimensions; j++) {
                    int subSum = 0;
                    for (int i = 0; i < ndPointsVec.size(); i++) {
                        subSum += coordArray[i][j];
                    }
                    final float value = (float) subSum / (float) ndPointsVec.size();
                    avgCoordinates.add(value);
                }


                DatabasePoints.put(point, new NDPoint(avgCoordinates)); //NOPMD

                // DEBUG
                Logger.getInstance().debug("Average for " + point.getId() + " ( " + point.getX() + ", " + point.getY() + ")");
                System.out.print("\t");
                final Vector<Float> debugCoordinates = DatabasePoints.get(point).getCoordinates();
                for (Float debugCoordinate : debugCoordinates) {
                    System.out.print(debugCoordinate + "\t");
                }
                System.out.println();

            }
            Logger.getInstance().debug(DatabasePoints.size() + " grid points available");

        } catch (final Exception e) {
            Logger.getInstance().debug(e.getMessage());
        }

    }

    /**
     * Sorts a map descending according to its values.
     *
     * @param unsortedMap the map to be sorted
     * @return the sorted list.
     */
    private List sortByValueAsc(final Map unsortedMap) {
        final List<String> keys = new ArrayList<String>();
        keys.addAll(unsortedMap.keySet());
        Collections.sort(keys, new Comparator() {
            public int compare(final Object o1, final Object o2) {
                final Object v1 = unsortedMap.get(o1);
                final Object v2 = unsortedMap.get(o2);
                if (v1 == null) {
                    return (v2 == null) ? 0 : 1;

                } else {
                    return ((Comparable) v1).compareTo(v2);

                }
            }
        });
        return keys;
    }
}
