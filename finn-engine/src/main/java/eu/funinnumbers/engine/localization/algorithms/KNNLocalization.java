package eu.funinnumbers.engine.localization.algorithms;

import eu.funinnumbers.db.model.localization.Point;
import eu.funinnumbers.engine.localization.Localization;
import eu.funinnumbers.engine.localization.LocalizationData;
import eu.funinnumbers.engine.localization.NDPoint;
import eu.funinnumbers.util.Logger;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;

public class KNNLocalization implements Localization { //NOPMD

    private Vector<PointPair> databasePointsVector = new Vector<PointPair>(); //NOPMD

    private int K = 10;

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
     * Returns the coordinates of a device.
     *
     * @param macAddress of the device.
     * @return The coordinates of the device.
     */
    public Point getCoordinates(final String macAddress) { //NOPMD

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

        final Map<Point, Integer> neigbOccurMap = new HashMap<Point, Integer>();

        // Find the K nearest points
        final List<PointDistancePair> kNearestPoints = findClosestPoints(currentNdPoint, databasePointsVector).subList(0, K);

        for (PointDistancePair pair : kNearestPoints) {
            if (!neigbOccurMap.containsKey(pair.getGridPoint())) {
                neigbOccurMap.put(pair.getGridPoint(), 0);
            }
            neigbOccurMap.put(pair.getGridPoint(), neigbOccurMap.get(pair.getGridPoint()) + 1);

        }

        return (Point) sortByValueDesc(neigbOccurMap).get(0);
    }

    /**
     * Returns the coordinates of a NDPoint.
     *
     * @param currentNDPoint of the device.
     * @return The coordinates of the device.
     */
    public Point getCoordinates(final NDPoint currentNDPoint) {
        final Map<Point, Integer> neigbOccurMap = new HashMap<Point, Integer>();

        // Find the K nearest points
        final List<PointDistancePair> kNearestPoints = findClosestPoints(currentNDPoint, databasePointsVector).subList(0, K);

        for (PointDistancePair pair : kNearestPoints) {
            if (!neigbOccurMap.containsKey(pair.getGridPoint())) {
                neigbOccurMap.put(pair.getGridPoint(), 0);
            }
            neigbOccurMap.put(pair.getGridPoint(), neigbOccurMap.get(pair.getGridPoint()) + 1);

        }
        return (Point) sortByValueDesc(neigbOccurMap).get(0);
    }

    /**
     * Returns the coordinates of a NDPoint which is closest to specific Points.
     *
     * @param currentNDPoint of the device.
     * @param pointIDs       where to search for the currentNDPoint  coordinates.
     * @return The coordinates of the device.
     */
    public Point getCoordinates(final NDPoint currentNDPoint, final Vector<Integer> pointIDs) { //NOPMD

        final Vector<PointPair> pointPairs = new Vector<PointPair>();
        for (PointPair pointPair : databasePointsVector) {
            if (pointIDs.contains(pointPair.getNdPoint().getId())) {
                pointPairs.add(pointPair);
            }
        }

        final Map<Point, Integer> neigbOccurMap = new HashMap<Point, Integer>();

        // Find the K nearest points
        final List<PointDistancePair> kNearestPoints = findClosestPoints(currentNDPoint, pointPairs).subList(0, K);

        for (PointDistancePair pair : kNearestPoints) {
            if (!neigbOccurMap.containsKey(pair.getGridPoint())) {
                neigbOccurMap.put(pair.getGridPoint(), 0);
            }
            neigbOccurMap.put(pair.getGridPoint(), neigbOccurMap.get(pair.getGridPoint()) + 1);
        }
        return (Point) sortByValueDesc(neigbOccurMap).get(0);
    }


    /**
     * Finds the ND point that is closest to current ND point.
     *
     * @param currentNdPoint the ND point of the device.
     * @param pointPairs     A Vector with PointPairs to find the closest NDPoint
     * @return String mac address of the closest eu.funinnumbers.station.
     */
    private List<PointDistancePair> findClosestPoints(final NDPoint currentNdPoint, final Vector<PointPair> pointPairs) {

        final HashMap<PointDistancePair, Double> distances = new HashMap<PointDistancePair, Double>();

        // Iterate points from database
        for (PointPair pointPair : pointPairs) {

            // Calculate euclidean distance between currentNdPoint and that from database
            final Double distance = currentNdPoint.euclidianDistance(pointPair.getNdPoint());
            distances.put(new PointDistancePair(pointPair.getGridPoint(), distance), distance); //NOPMD
        }

        return sortByValueAsc(distances);
    }

    /**
     * Load Data from file.
     *
     * @param filename
     */
    public void loadTrainedValues(final String filename) { //NOPMD
        // Load Data from file
        HashMap<Point, Vector<NDPoint>> loadedPoints;

        try {
            final FileInputStream in = new FileInputStream(filename);

            final ObjectInputStream s = new ObjectInputStream(in);
            loadedPoints = (HashMap<Point, Vector<NDPoint>>) s.readObject();
            s.close();
            in.close();

            Logger.getInstance().debug(loadedPoints.size() + " trained points loaded succefully");

            // Make a Vector with ALL Points and corresponding NDPoints
            for (Point point : loadedPoints.keySet()) {
                final Vector<NDPoint> ndPointsVec = loadedPoints.get(point);
                for (NDPoint ndPoint : ndPointsVec) {
                    databasePointsVector.add(new PointPair(point, ndPoint)); //NOPMD
                }

            }
            Logger.getInstance().debug("DatabaseVector contains " + databasePointsVector.size() + " pairs");

            for (PointPair pointPair : databasePointsVector) {
                Logger.getInstance().debug(pointPair.getGridPoint().getId() + ": (" + pointPair.getGridPoint().getX() + ","
                        + pointPair.getGridPoint().getY() + ")" + pointPair.getNdPoint().getCoordinates());
            }

            K = databasePointsVector.size() / 10;
        } catch (Exception e) {
            Logger.getInstance().debug(e);

        }
    }

    /**
     * Sorts a map descending according to its values.
     *
     * @param unsortedMap the map to be sorted
     * @return the sorted list.
     */
    private List sortByValueAsc(final Map unsortedMap) {
        final List keys = new ArrayList();
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

    /**
     * Sorts a map descending according to its values.
     *
     * @param unsortedMap the map to be sorted
     * @return the sorted list.
     */
    private List sortByValueDesc(final Map unsortedMap) {
        final List keys = new ArrayList();
        keys.addAll(unsortedMap.keySet());
        Collections.sort(keys, new Comparator() {
            public int compare(final Object o2, final Object o1) {
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

class PointPair {
    private Point gridPoint; //NOPMD
    private NDPoint ndPoint; //NOPMD

    PointPair(final Point gPoint, final NDPoint ndPoint) {
        this.gridPoint = gPoint;
        this.ndPoint = ndPoint;
    }

    public Point getGridPoint() {
        return gridPoint;
    }

    public NDPoint getNdPoint() {
        return ndPoint;
    }
}

class PointDistancePair {
    private Point gridPoint; //NOPMD
    private Double distance; //NOPMD

    PointDistancePair(final Point gPoint, final Double dist) {
        this.gridPoint = gPoint;
        this.distance = dist;

    }

    public Double getDistance() {
        return distance;
    }

    public Point getGridPoint() {
        return gridPoint;
    }
}
