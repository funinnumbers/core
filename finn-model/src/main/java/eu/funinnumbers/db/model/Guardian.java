package eu.funinnumbers.db.model;

import com.sun.spot.util.IEEEAddress;
import eu.funinnumbers.util.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Date;


/**
 * Contains information related to the Device (the Guardian) used by the player to
 * play the game.
 */
public class Guardian implements StorableEntity /*, java.io.Serializable*/ {

    /**
     * Required by Serializable interface.
     */
    static final long serialVersionUID = 42L; //NOPMD

    /**
     * Initialization phase: need to update jar.
     */
    public static final int INIT_NOJAR = 0;

    /**
     * Initialization phase: need to update data.
     */
    public static final int INIT_NODATA = 1;

    /**
     * Initialization phase: complete.
     */
    public static final int INIT_COMPLETE = 2;

    /**
     * Initialization phase: lost the game.
     */
    public static final int INIT_LOST = 3;


    /**
     *
     */
    public static final int INIT_LEADER_ELECTION = 4;

    /**
     * Guardian's ID.
     */
    private int ID;  // NOPMD

    /**
     * Guardian's address.
     */
    private Long address;

    /**
     * Indicates the Initialization Phase of the Guardian.
     */
    private int initPhase;

    /**
     * Guardians Liferay id.
     */
    private int liferayID;

    /**
     * Station's name.
     */
    private String station = "nostation";

    /**
     * Stations Long address.
     */
    private Long stationL = new Long(0);

    /**
     * The representation of the Guardian ID on the Guardian LEDS is stored on the DB and defined by user.
     */
    private int ledId;

    /**
     * Guardian's Avatar.
     */
    private java.util.Set avatars /*START_OF_COMMENT*/ = new java.util.HashSet()/*END_OF_COMMENT*/;

    // Not stored in DB

    /**
     * Indicates if the Guardian is alive or not.
     */
    private boolean alive;

    /**
     * The last RSSI value.
     */
    private int lastRssi;

    /**
     * The last time that eu.funinnumbers.guardian was used.
     */
    private Date lastAlive = null;

    /**
     * The current target of the eu.funinnumbers.guardian.
     */
    private Guardian currentTarget = null;

    /**
     * Sets the eu.funinnumbers.station with which the Guardian is associated.
     *
     * @param stationP - the eu.funinnumbers.station
     */
    public void setStation(final String stationP) {
        this.station = stationP;
    }

    /**
     * Returns the eu.funinnumbers.station with which the Guardian is associated.
     *
     * @return the eu.funinnumbers.station
     */
    public String getStation() {
        return this.station;
    }

    /**
     * Get eu.funinnumbers.guardian's liferay id.
     *
     * @return the liferay id
     */
    public int getLiferayID() {
        return liferayID;
    }

    /**
     * Set eu.funinnumbers.guardian's liferay id.
     *
     * @param liferayIDP the liferay id
     */
    public void setLiferayID(final int liferayIDP) {
        this.liferayID = liferayIDP;
    }

    /**
     * Set the avatars of this eu.funinnumbers.guardian.
     *
     * @param avatarsP A set of Avatars.
     */
    public void setAvatars(final java.util.Set avatarsP) {
        this.avatars = avatarsP;
    }

    /**
     * Get a set of avatars of this Guardian.
     *
     * @return A set of avatars
     */
    public java.util.Set getAvatars() {
        return avatars;
    }

    /**
     * Adds an avatar to the existing Set of Avatars.
     *
     * @param avatar The avatar
     */    /*START_OF_COMMENT*/
    public void addAvatar(final Avatar avatar) {
        this.getAvatars().add(avatar);
    }
    /*END_OF_COMMENT*/

    /**
     * Returns the Guardian Id.
     *
     * @return An int with the guardianId
     */
    public int getID() {
        return ID;
    }

    /**
     * Sets guardianId.
     *
     * @param guardianID An int with the guardianId
     */
    public void setID(final int guardianID) {
        this.ID = guardianID;
    }

    /**
     * Returns the address of a eu.funinnumbers.guardian.
     *
     * @return A Long with the address
     */
    public Long getAddress() {
        return address;
    }

    /**
     * Sets the address of a eu.funinnumbers.guardian.
     *
     * @param addressP A Long with the address
     */
    public void setAddress(final Long addressP) {
        this.address = addressP;
    }

    /**
     * Set the Address of a eu.funinnumbers.guardian as a String.
     *
     * @param addressP A String with the mac address
     */
    public void setAddress(final String addressP) {
        this.address = new Long(IEEEAddress.toLong(addressP));
    }

    /**
     * Returns the Mac address od the eu.funinnumbers.guardian as String.
     *
     * @return A String with the mac address
     */
    public String getAddressAsString() {
        return IEEEAddress.toDottedHex(this.address.longValue());
    }

    /**
     * Checks whether the Guardian is alive.
     *
     * @return true if the Guardian is alive or false otherwise
     */
    public final boolean isAlive() {
        return alive;
    }

    /**
     * Sets to true if the Guardian is alive, or false otherwise.
     *
     * @param aliveP - true if the Guardian is alive, or false otherwise
     */
    public final void setAlive(final boolean aliveP) {
        this.alive = aliveP;
    }

    /**
     * Returns the last date when the Guardian was alive.
     *
     * @return the last date when the Guardian was alive
     */
    public final Date getLastAlive() {
        return lastAlive;
    }

    /**
     * Sets the last date when the Guardian was alive.
     *
     * @param lastAliveP - the last date when the Guardian was alive
     */
    public final void setLastAlive(final Date lastAliveP) {
        this.lastAlive = lastAliveP;
    }

    /**
     * Sets the last date when the Guardian was alive, to the current date.
     */
    public final void setLastAlive() {
        this.lastAlive = new Date();
    }

    /**
     * Returns the Guardian's current target.
     *
     * @return the Guardian's current targer
     */
    public final Guardian getCurrentTarget() {
        return currentTarget;
    }

    /**
     * Sets the Guardian's current target.
     *
     * @param target - the Guardian's current target
     */
    public final void setCurrentTarget(final Guardian target) {
        this.currentTarget = target;
    }

    /**
     * Returns the type of the entity.
     *
     * @return the type of the entity
     */
    public final int getEntityType() {
        return StorableEntity.GUARDIANENTITY;
    }

    /**
     * Get stationId.
     *
     * @return An int with the stationId
     */
    public int getLedId() {
        return ledId;
    }

    /**
     * Set stationId.
     *
     * @param ledIdP An int with stationId
     */
    public void setLedId(final int ledIdP) {
        this.ledId = ledIdP;
    }

    /**
     * Return the initialization phase of this eu.funinnumbers.guardian.
     *
     * @return the init phase of this eu.funinnumbers.guardian
     */
    public int getInitPhase() {
        return initPhase;
    }

    /**
     * Sets the initialization phase of this eu.funinnumbers.guardian.
     *
     * @param initPhaseP the initialization phase of this eu.funinnumbers.guardian
     */
    public void setInitPhase(final int initPhaseP) {
        this.initPhase = initPhaseP;
    }

    /**
     * Returns the last RSSI value.
     *
     * @return an int with the RSSI value.
     */
    public int getLastRssi() {
        return lastRssi;
    }

    /**
     * Sets the last RSSI value.
     *
     * @param lastRssi the last RSSI value.s
     */
    public void setLastRssi(int lastRssi) {
        this.lastRssi = lastRssi;
    }

    public Long getStationLongAddr() {
        return stationL;
    }

    public void setStationLong(final Long stationLong) {
        this.stationL = stationLong;
    }

    /**
     * Converts the entity to byte array.
     *
     * @return the byte array of the entity
     */
    public final byte[] toByteArray() {

        byte[] data = null;

        try {
            // Create byte array
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            final DataOutputStream dout = new DataOutputStream(bout);

            dout.writeInt(ID);
            dout.writeInt(ledId);
            dout.writeLong(address.longValue());
            dout.writeUTF(station);
            dout.writeInt(initPhase);

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
    public final void fromByteArray(final byte[] entityBuffer) {

        try {
            // Read entity's fields from byte array
            final ByteArrayInputStream bin = new ByteArrayInputStream(entityBuffer);
            final DataInputStream din = new DataInputStream(bin);

            ID = din.readInt();
            ledId = din.readInt();
            address = new Long(din.readLong());
            station = din.readUTF();
            initPhase = din.readInt();

            din.close();
            bin.close();

        } catch (Exception e) {
            Logger.getInstance().debug(e);
        }
    }

}
