package eu.funinnumbers.engine.localization;

import eu.funinnumbers.db.model.localization.Point;
import eu.funinnumbers.util.Logger;

import java.util.*;

/**
 * The singleton class where the Localization Data are saved.
 */
public final class LocalizationData { //NOPMD

    /**
     * Maps the MAC address of a eu.funinnumbers.station to it's coordinate.
     */
    private final HashMap<String, Point> stationCoordinates = new HashMap<String, Point>();

    /**
     * The unique instance of localizer.
     */
    private static LocalizationData thisLocalizer = null;

    /**
     * LQI type identifier.
     */
    public static final int LQI = 0;

    /**
     * RSSI type identifier.
     */
    public static final int RSSI = 1;

    /**
     * Minimun RSSI value.
     */
    public static final float MIN_RSSI = -50f;

    /**
     * Minimun LQI value.
     */
    public static final float MIN_LQI = 0;

    /**
     * HashMap for storing rss indications per eu.funinnumbers.station, per eu.funinnumbers.guardian.
     */
    private final HashMap<String, HashMap<String, ArrayList<Integer>>> rssi;

    /**
     * HashMap for storing link quality indications per eu.funinnumbers.station, per eu.funinnumbers.guardian.
     */
    private final HashMap<String, HashMap<String, ArrayList<Integer>>> lqi;

    /**
     * Array for both lqi and rssi readings.
     */
    private final HashMap<String, HashMap<String, ArrayList<Integer>>> savedValues[];

    private final HashMap<String, ArrayList<NDPoint>> rssiSavedValues;

    private final HashMap<String, ArrayList<NDPoint>> lqiSavedValues;

    /**
     * Public access to the Localizer.
     *
     * @return the unique instance
     */
    public static LocalizationData getInstance() {
        synchronized (LocalizationData.class) {

            if (thisLocalizer == null) {
                thisLocalizer = new LocalizationData();
            }
            return thisLocalizer;
        }
    }

    /**
     * Default constructor.
     */
    @SuppressWarnings("unchecked")
    private LocalizationData() {

        rssi = new HashMap<String, HashMap<String, ArrayList<Integer>>>();
        lqi = new HashMap<String, HashMap<String, ArrayList<Integer>>>();

        savedValues = new HashMap[2];
        savedValues[LQI] = lqi;
        savedValues[RSSI] = rssi;

        rssiSavedValues = new HashMap<String, ArrayList<NDPoint>>();
        lqiSavedValues = new HashMap<String, ArrayList<NDPoint>>();

    }


    /**
     * Logs either LQI or RSSI readings from a specific eu.funinnumbers.station of a specific eu.funinnumbers.guardian.
     *
     * @param station  mac address of eu.funinnumbers.station
     * @param guardian mac address of eu.funinnumbers.guardian
     * @param value    the value to be logged
     * @param type     the type to be logged
     */
    public void logValue(final String station,
                         final String guardian,
                         final Integer value,
                         final int type) {
        synchronized (savedValues) {
            if (!savedValues[type].containsKey(station)) {
                Logger.getInstance().debug("First time eu.funinnumbers.station");
                savedValues[type].put(station, new HashMap<String, ArrayList<Integer>>());
                savedValues[type].get(station).put(guardian, new ArrayList<Integer>());


            }
            if (!savedValues[type].get(station).containsKey(guardian)) {
                Logger.getInstance().debug("First time eu.funinnumbers.guardian");
                savedValues[type].get(station).put(guardian, new ArrayList<Integer>());
                savedValues[type].get(station).get(guardian);

            }

            savedValues[type].get(station).get(guardian).add(value);

        }
    }


    /**
     * Logs both LQI and RSSI readings from a specific eu.funinnumbers.station of a specific eu.funinnumbers.guardian.
     *
     * @param station  mac address of eu.funinnumbers.station
     * @param guardian mac address of eu.funinnumbers.guardian
     * @param rssi     the rssi value to be logged
     * @param lqi      the lqi value to be logged
     */
    public void logValues(final String station,
                          final String guardian,
                          final Integer rssi,
                          final Integer lqi) {

        final Integer[] values = new Integer[2];
        values[LQI] = lqi;
        values[RSSI] = rssi;
        synchronized (savedValues) {
            for (int valType = LQI; valType <= RSSI; valType++) {

                if (!savedValues[valType].containsKey(station)) {
                    Logger.getInstance().debug("First time eu.funinnumbers.station");
                    savedValues[valType].put(station, new HashMap<String, ArrayList<Integer>>()); //NOPMD
                    savedValues[valType].get(station).put(guardian, new ArrayList<Integer>()); //NOPMD


                }
                if (!savedValues[valType].get(station).containsKey(guardian)) {
                    Logger.getInstance().debug("First time eu.funinnumbers.guardian");
                    savedValues[valType].get(station).put(guardian, new ArrayList<Integer>()); //NOPMD
                    savedValues[valType].get(station).get(guardian);

                }

                savedValues[valType].get(station).get(guardian).add(values[valType]);
            }
        }
    }

    /**
     * Returns the latest rssi or lqi readings from all stations for a specific eu.funinnumbers.guardian.
     *
     * @param numOfLatestValues the number of required readings
     * @param guardian          the mac address of the eu.funinnumbers.guardian
     * @param type              LQI or RSSI
     * @return retMap           the HashMap containing the requested readings
     */
    public HashMap<String, List<Integer>> getValues(final int numOfLatestValues,
                                                    final String guardian,
                                                    final int type) {
        synchronized (savedValues) {
            final HashMap<String, List<Integer>> retMap = new HashMap<String, List<Integer>>();
            for (final String station : stationCoordinates.keySet()) {
                try {
                    if (savedValues[type].get(station).get(guardian).size() < numOfLatestValues) {
                        retMap.put(station, savedValues[type].get(station).get(guardian));
                    } else {
                        retMap.put(station, savedValues[type].get(station).get(guardian).subList(savedValues[type].get(station).get(guardian).size() - numOfLatestValues, savedValues[type].get(station).get(guardian).size()));
                    }

                } catch (Exception ex) {
                    //Logger.getInstance().debug("Exception while collecting values from stations");
                    retMap.put(station, new ArrayList()); //NOPMD
                }
            }

            return retMap;
        }
    }

    /**
     * Sets the coordinates of a specific eu.funinnumbers.station.
     *
     * @param mac   the address of the eu.funinnumbers.station.
     * @param point the coordinates of the eu.funinnumbers.station as Point.
     */
    public void setStationCoordinates(final String mac,
                                      final Point point) {
        stationCoordinates.put(mac, point);

    }

    /**
     * Returns the coordinates of a eu.funinnumbers.station according to associations.
     *
     * @param stationMac the mac address as string.
     * @return the X Point of the eu.funinnumbers.station.
     */
    public Point getStationCoordinates(final String stationMac) {
        return stationCoordinates.get(stationMac);
    }

    /**
     * Returns the coordinates of all stations.
     *
     * @return the map containing the coordinates for each eu.funinnumbers.station.
     */
    public HashMap<String, Point> getStationCoordinates() {
        return stationCoordinates;
    }


    /**
     * Returns the number of stations with known coordinates.
     *
     * @return the X Point of the eu.funinnumbers.station.
     */
    public int getNumberOfStations() {
        return stationCoordinates.size();
    }

    /**
     * Clear the saved values of a specific eu.funinnumbers.guardian.
     *
     * @param guardian the eu.funinnumbers.guardian' mac address
     */
    public void clearValues(final String guardian) { //NOPMD
        synchronized (savedValues) {
            for (final String station : stationCoordinates.keySet()) {
                try {
                    if (savedValues[RSSI].get(station).containsKey(guardian)) {
                        savedValues[RSSI].get(station).get(guardian).clear();
                    }

                    if (savedValues[LQI].get(station).containsKey(guardian)) {
                        savedValues[LQI].get(station).get(guardian).clear();
                    }
                } catch (Exception ex) {
                    Logger.getInstance().debug("Exception while clearing values ", ex);
                }
            }
        }
    }

    public void logNDpoint(final String guardian, final NDPoint ndPoint, final int type) {
        if (type == LocalizationData.RSSI) {
            synchronized (rssiSavedValues) {
                if (rssiSavedValues.containsKey(guardian)) {
                    rssiSavedValues.get(guardian).add(ndPoint);

                } else {
                    final ArrayList<NDPoint> ndPoints = new ArrayList<NDPoint>();
                    ndPoints.add(ndPoint);
                    rssiSavedValues.put(guardian, ndPoints);
                }
            }
        } else if (type == LocalizationData.LQI) {
            synchronized (lqiSavedValues) {
                if (lqiSavedValues.containsKey(guardian)) {
                    lqiSavedValues.get(guardian).add(ndPoint);
                } else {
                    final ArrayList<NDPoint> ndPoints = new ArrayList<NDPoint>();
                    ndPoints.add(ndPoint);
                    lqiSavedValues.put(guardian, ndPoints);
                }
            }
        } else {
            Logger.getInstance().debug("Unknown type. Couldn't log NDPoint.");
        }
    }

    public List<NDPoint> getRSSIValues(final int numOfLatestValues, final String guardian) {

        if (rssiSavedValues.containsKey(guardian)) {
            final ArrayList<NDPoint> ndPoints = rssiSavedValues.get(guardian);
            Collections.sort(ndPoints);
            if (ndPoints.size() >= numOfLatestValues) {
                return ndPoints.subList(0, numOfLatestValues - 1);
            } else {
                return ndPoints;
            }
        }
        return new ArrayList<NDPoint>(0);
    }

    public List<NDPoint> getLQIValues(final int numOfLatestValues, final String guardian) {
        if (lqiSavedValues.containsKey(guardian)) {
            final ArrayList<NDPoint> ndPoints = lqiSavedValues.get(guardian);
            Collections.sort(ndPoints);
            if (ndPoints.size() >= numOfLatestValues) {
                return ndPoints.subList(ndPoints.size() - numOfLatestValues - 1, ndPoints.size() - 1);
            } else {
                return ndPoints;
            }
        }
        return new ArrayList<NDPoint>(0);
    }
}