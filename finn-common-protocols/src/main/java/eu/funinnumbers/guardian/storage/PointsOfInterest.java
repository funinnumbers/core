package eu.funinnumbers.guardian.storage;

import eu.funinnumbers.db.model.StorableEntity;
import eu.funinnumbers.guardian.util.HashMap;
import eu.funinnumbers.util.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Enumeration;

/**
 * Points of Interst Class.
 */
public final class PointsOfInterest implements StorableEntity {

    /**
     * A HashMap containing the points of interest (poi) and true or false depending on whether the corresponding
     * poi has been visited and "marked" (a pray has been perfromed) by the player.
     */
    private final HashMap pois;

    /**
     * The one and only PointsOfInterest instance.
     */
    private static PointsOfInterest thisPOIs;

    /**
     * The number of visited POIs.
     */
    private static int visitedPOIs;

    /**
     * Total number of POIs.
     */
    private static int totalPOIs;

    /**
     * Total number of remaining POIs.
     */
    private static int remainingPOIs;


    /**
     * DS Station.
     */
    private static String dSStation;

    /**
     * Default constructor.
     */
    private PointsOfInterest() {
        pois = new HashMap();

    }

    /**
     * Is invoked each time access to the PointsOfInterest instance is needed.
     *
     * @return the instantiated existing PointsOfInterest
     */
    public static PointsOfInterest getInstance() {
        synchronized (PointsOfInterest.class) {
            if (thisPOIs == null) {
                thisPOIs = new PointsOfInterest();
            }
            return thisPOIs;
        }
    }

    /**
     * Add a new point of interest.
     *
     * @param pointId - the id (eu.funinnumbers.station or guardina id) of the point of interest to be added
     */
    public void addPointOfInterest(final int pointId) {
        pois.put(new Integer(pointId), Boolean.FALSE);
    }


    /**
     * Removes a point of interest.
     *
     * @param pointId - the id (eu.funinnumbers.station or guardina id) of the point of interest to be removed
     */
    public void removePointOfInterest(final int pointId) {
        pois.remove(new Integer(pointId));
    }

    /**
     * Set a POI visited.
     *
     * @param pointId an int with the Poind ID
     */
    public void visitPointOfInterest(final int pointId) {

        if (!((Boolean) pois.get(new Integer(pointId))).booleanValue()) {
            pois.put(new Integer(pointId), Boolean.TRUE);

            if (visitedPOIs != totalPOIs) {
                visitedPOIs++;
            }

            if (remainingPOIs != 0) {
                remainingPOIs--;
            }

            StorageService.getInstance().add(thisPOIs);
            thisPOIs.showme();
        }
    }

    /**
     * Returns the number of univisited points of interest.
     *
     * @return the number of univisited points of interest
     */
    public int pointsOfInterestRemaining() {

        /** int leftcounter = 0;
         Enumeration pointsEnum = POIs.getEntries();
         while (pointsEnum.hasMoreElements()) {

         //if (POIs.containsKey(new Integer(Integer.parseInt((String) pointsEnum.nextElement())))) {
         leftcounter++;
         //}
         }
         return leftcounter; */
        return remainingPOIs;
    }

    /**
     * Returns the number of visited points of interest.
     *
     * @return the number of visited points of interest
     */
    public int pointsOfInterestVisited() {
        return visitedPOIs;
    }

    /**
     * Sets the number of already visited points of interest.
     *
     * @param visitedPOIsP the number as integer
     */
    public void setVisitedPOIs(final int visitedPOIsP) {
        PointsOfInterest.visitedPOIs = visitedPOIsP;
    }

    /**
     * Sets the overall number of existing POIs.
     *
     * @param totalPOIsP the number of POIs as integer
     */
    public void setTotalPOIs(final int totalPOIsP) {
        PointsOfInterest.totalPOIs = totalPOIsP;
        PointsOfInterest.remainingPOIs = totalPOIsP;
    }

    /**
     * Returns the number of POIs.
     *
     * @return the number of POIs as integer
     */
    public int getTotalPOIs() {
        return totalPOIs;
    }

    /**
     * TODO what's this?
     * Returns the DSStation
     *
     * @return the DSStation
     */
    public String getDSStation() {
        return dSStation;
    }

    /**
     * Sets the DSStation.
     *
     * @param dSStationP as String
     */
    public void setDSStation(final String dSStationP) {
        PointsOfInterest.dSStation = dSStationP;
    }

    /**
     * Check whether this eu.funinnumbers.station has already been found by the player.
     *
     * @param nodeId - the (eu.funinnumbers.guardian or eu.funinnumbers.station) id
     * @return true if the id is a point of interest and false otherwise
     */
    public Boolean isPointOfInterset(final Integer nodeId) {
        return (Boolean) pois.get(nodeId);
    }

    /**
     * Returns the type of the entity.
     *
     * @return the type of the entity
     */
    public int getEntityType() {
        return POINTSOFINTEREST;
    }

    /**
     * Returns the id of the entity.
     *
     * @return the id of the entity
     */
    public int getID() {
        return 0;
    }

    /**
     * Sets the id of the entity.
     *
     * @param entityId - the id of the entity
     */
    public void setID(final int entityId) {
        // do nothing
    }

    /**
     * Converts the entity to byte array.
     * Note that the entity's ID should be the first to be saved in the byte array
     *
     * @return the byte array of the entity
     */
    public byte[] toByteArray() {
        byte[] data = null;

        try {
            // Create byte array
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            final DataOutputStream dout = new DataOutputStream(bout);

            dout.writeInt(getID());
            dout.writeInt(totalPOIs);
            dout.writeInt(visitedPOIs);
            dout.writeInt(remainingPOIs);
            dout.writeUTF(dSStation);

            final Enumeration pointsEnum = pois.getKeys();
            while (pointsEnum.hasMoreElements()) {
                final Integer temp = (Integer) pointsEnum.nextElement();
                dout.writeInt(temp.intValue());
                dout.writeBoolean(((Boolean) pois.get(temp)).booleanValue());
            }
            dout.flush();
            dout.close();

            data = bout.toByteArray();

        } catch (Exception e) {
            Logger.getInstance().debug(e);
        }

        // Return byte array
        return data;
    }

    /**
     * Initializes the entity by reading its byte array.
     *
     * @param entityBuffer - the entity's buffer
     */
    public void fromByteArray(final byte[] entityBuffer) {
        try {
            // Read entity's fields from byte array
            final ByteArrayInputStream bin = new ByteArrayInputStream(entityBuffer);
            final DataInputStream din = new DataInputStream(bin);

            din.readInt();
            totalPOIs = din.readInt();
            visitedPOIs = din.readInt();
            remainingPOIs = din.readInt();
            dSStation = din.readUTF();

            for (int i = 0; i < totalPOIs; i++) {
                final int tmpStationID = din.readInt();
                final boolean isAlive = din.readBoolean();
                pois.put(new Integer(tmpStationID), new Boolean(isAlive)); //NOPMD
            }

            din.close();
            bin.close();

        } catch (Exception e) {
            Logger.getInstance().debug(e);
        }


    }

    /**
     * Debug function.
     */
    public void showme() {
        Logger.getInstance().debug("POI DSSstaion " + dSStation);
        Logger.getInstance().debug("Total Pois " + totalPOIs);
        Logger.getInstance().debug("Remaining Pois " + remainingPOIs);
        final Enumeration pointsEnum = thisPOIs.pois.getKeys();
        while (pointsEnum.hasMoreElements()) {
            final Integer temp = (Integer) pointsEnum.nextElement();
            Logger.getInstance().debug("pois Station ID " + temp.intValue()
                    + " visit " + ((Boolean) thisPOIs.pois.get(temp)).booleanValue());

        }

    }
}
