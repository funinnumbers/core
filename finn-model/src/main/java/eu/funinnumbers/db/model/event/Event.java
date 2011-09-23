package eu.funinnumbers.db.model.event;

import eu.funinnumbers.db.model.Avatar;
import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.db.model.Station;
import eu.funinnumbers.db.model.StorableEntity;
import eu.funinnumbers.util.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Date;

/**
 * This is the event sent to the Engine.
 */ /*Do not remove ne comment "Serializable*/
public class Event implements StorableEntity /*,java.io.Serializable*/ {

    /**
     * Required by Serializable interface.
     */
    static final long serialVersionUID = 42L; //NOPMD

    /**
     * Guardian's ID.
     */
    private int guardianID;

    /**
     * Indicates eu.funinnumbers.guardian's Counter.
     */
    private int guardianCounter;

    /**
     * Event's ID.
     */
    private int ID;  // NOPMD

    /**
     * The time stamp of the event.
     */
    private Date timeStamp;

    /**
     * The type of the event.
     */
    private String type;

    /**
     * The description of the event.
     */
    private String description;

    /**
     * The creator of the events.
     */
    private Avatar avatar;

    /**
     * The battle Engine of the event.
     */
    private BattleEngine battleEngine;

    /**
     * The battle eu.funinnumbers.station of the event.
     */
    private Station station;

    /**
     * The source mac Address.
     */
    private String sourceAddress;

    /**
     * Empty Constructor.
     */
    public Event() {
        super();
    }

    /**
     * Constructor with all arguments.
     *
     * @param guardianIDP      Id of the eu.funinnumbers.guardian that created the event
     * @param guardianCounterP Counter of the eu.funinnumbers.guardian that created the event
     * @param eventIDP         An int with a unique id for each event
     * @param timeStampP       Timestamp of the event
     * @param typeP            String describing the type of the event
     * @param descriptionP     String describing the event
     * @param battleEngineP    The Engine associated with the event
     */
    public Event(final int guardianIDP, final int guardianCounterP, final int eventIDP,
                 final Date timeStampP, final String typeP, final String descriptionP,
                 final BattleEngine battleEngineP) {
        this.guardianID = guardianIDP;
        this.guardianCounter = guardianCounterP;
        this.ID = eventIDP;
        this.timeStamp = timeStampP;
        this.type = typeP;
        this.description = descriptionP;
        this.battleEngine = battleEngineP;
    }

    /**
     * Constructor used by guardians.
     *
     * @param guardianIDP      Id of the eu.funinnumbers.guardian that created the event
     * @param guardianCounterP Counter of the eu.funinnumbers.guardian that created the event
     * @param typeP            String describing the type of the event
     * @param descriptionP     String describing the event
     */
    public Event(final int guardianIDP, final int guardianCounterP, final String typeP, final String descriptionP) {
        this.guardianID = guardianIDP;
        this.guardianCounter = guardianCounterP;
        this.ID = -1;
        this.timeStamp = new Date(System.currentTimeMillis());
        this.type = typeP;
        this.description = descriptionP;
        this.battleEngine = null; // NOPMD
    }

    /**
     * Get the eventID.
     *
     * @return An int with the eventID
     */
    public int getID() {
        return ID;
    }

    /**
     * Set the eventID.
     *
     * @param eventID An int with the new eventID
     */
    public void setID(final int eventID) {
        this.ID = eventID;
    }

    /**
     * Get the event's timestamp.
     *
     * @return The timestamp in Date form
     */
    public Date getTimeStamp() {
        return timeStamp;
    }

    /**
     * Set the event's timestamp.
     *
     * @param timeStampP The new timestamp
     */
    public void setTimeStamp(final Date timeStampP) {
        this.timeStamp = timeStampP;
    }

    /**
     * Get the type of an event.
     *
     * @return A string with the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Set the type of the event.
     *
     * @param typeP A string with the type
     */
    public void setType(final String typeP) {
        this.type = typeP;
    }

    /**
     * Get the description of an event.
     *
     * @return A string with the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the description of the event.
     *
     * @param desc A string with the description
     */
    public void setDescription(final String desc) {
        this.description = desc;
    }

    /**
     * Get the avatar of the event.
     *
     * @return An Avatar object
     */
    public Avatar getAvatar() {
        return avatar;
    }

    /**
     * Set the Avatar of the event.
     *
     * @param avatarP A avatar object
     */
    public void setAvatar(final Avatar avatarP) {
        this.avatar = avatarP;
    }

    /**
     * Get the guardianID.
     *
     * @return An int with the eu.funinnumbers.guardian id
     */
    public int getGuardianID() {
        return guardianID;
    }

    /**
     * Set the guardianID.
     *
     * @param guardianIDP An int with the new guardianID
     */
    public void setGuardianID(final int guardianIDP) {
        this.guardianID = guardianIDP;
    }

    /**
     * Get the guardianCounter.
     *
     * @return An int with the eu.funinnumbers.guardian counter
     */
    public int getGuardianCounter() {
        return guardianCounter;
    }

    /**
     * Set the guardianCounter.
     *
     * @param guardianCounterP An int with the new guardianCounter
     */
    public void setGuardianCounter(final int guardianCounterP) {
        this.guardianCounter = guardianCounterP;
    }

    /**
     * Get the Battle Engine associated with the event.
     *
     * @return The Engine
     */
    public BattleEngine getBattleEngine() {
        return battleEngine;
    }

    /**
     * Set the Battle Engine associated with the event.
     *
     * @param battleEngineP The Engine
     */
    public void setBattleEngine(final BattleEngine battleEngineP) {
        this.battleEngine = battleEngineP;
    }

    /**
     * Get the Station where the event took place.
     *
     * @return The eu.funinnumbers.station
     */
    public Station getStation() {
        return station;
    }

    /**
     * Set the Staion where the event took place.
     *
     * @param stationP The Station
     */
    public void setStation(final Station stationP) {
        this.station = stationP;
    }

    /**
     * Tests for equality between the specified event and this event.
     * Two events are considered equal if they have the same guardianID and guardianCounter.
     *
     * @param event the event to be tested for equality with this event
     * @return true if the events are considered equal, false otherwise
     */
    public boolean equals(final Object event) {
        if (!(event instanceof Event)) {
            return false; //NOPMD
        }

        // Type conversion
        final Event eventB = (Event) event;

        return (this.getGuardianID() == eventB.getGuardianID()
                && this.getGuardianCounter() == eventB.getGuardianCounter());
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hashtables such as those provided by
     * <code>java.eu.funinnumbers.util.Hashtable</code>.
     * <p/>
     * The general contract of <code>hashCode</code> is:
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     * an execution of a Java application, the <tt>hashCode</tt> method
     * must consistently return the same integer, provided no information
     * used in <tt>equals</tt> comparisons on the object is modified.
     * This integer need not remain consistent from one execution of an
     * application to another execution of the same application.
     * <li>If two objects are equal according to the <tt>equals(Object)</tt>
     * method, then calling the <code>hashCode</code> method on each of
     * the two objects must produce the same integer result.
     * <li>It is <em>not</em> required that if two objects are unequal
     * according to the {@link java.lang.Object#equals(java.lang.Object)}
     * method, then calling the <tt>hashCode</tt> method on each of the
     * two objects must produce distinct integer results.  However, the
     * programmer should be aware that producing distinct integer results
     * for unequal objects may improve the performance of hashtables.
     * </ul>
     * <p/>
     * As much as is reasonably practical, the hashCode method defined by
     * class <tt>Object</tt> does return distinct integers for distinct
     * objects. (This is typically implemented by converting the internal
     * address of the object into an integer, but this implementation
     * technique is not required by the
     * Java<font size="-2"><sup>TM</sup></font> programming language.)
     *
     * @return a hash code value for this object.
     * @see java.lang.Object#equals(java.lang.Object)
     * @see java.util.Hashtable
     */
    public int hashCode() { // NOPMD
        return super.hashCode();
    }

    /**
     * Returns information regarding this event in a string format.
     *
     * @return A string with the containts of the Event
     */
    public String toString() {
        return getTimeStamp().getTime() + "/" + getGuardianID() + "/"
                + getGuardianCounter() + "/" + getType() + "/" + getDescription();
    }

    /**
     * Takes an event which is in string format and converts it to an Event.
     *
     * @param event The event in string format
     * @return A new Event
     */
    public static Event valueOf(final String event) {
        int lastIndex;

        // TimeStamp
        final Date id1 = new Date(Long.parseLong(event.substring(0, event.indexOf('/'))));
        lastIndex = event.indexOf('/');

        // Guardian ID
        final int id2 = Integer.parseInt(event.substring(lastIndex + 1, event.indexOf('/', lastIndex + 1)));
        lastIndex = event.indexOf('/', lastIndex + 1);

        // Guardian Counter
        final int id3 = Integer.parseInt(event.substring(lastIndex + 1, event.indexOf('/', lastIndex + 1)));
        lastIndex = event.indexOf('/', lastIndex + 1);

        // Type
        final String id4 = event.substring(lastIndex + 1, event.indexOf('/', lastIndex + 1));
        lastIndex = event.indexOf('/', lastIndex + 1);

        // Description
        final String id5 = event.substring(lastIndex + 1);

        final Event newEvent = new Event(id2, id3, id4, id5);
        newEvent.setTimeStamp(id1);

        return newEvent;
    }

    /**
     * Returns the type of the entity.
     *
     * @return the type of the entity
     */
    public int getEntityType() {
        return StorableEntity.EVENTENTITY;
    }

    /**
     * Returns Sunspot source mac Address.
     *
     * @return String the source mac address.
     */
    public String getSourceAddress() {
        return sourceAddress;
    }

    /**
     * Set the source mac Address.
     *
     * @param sourceAddress a String with the mac address.
     */
    public void setSourceAddress(final String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    /**
     * Converts the entity to byte array.
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
            dout.writeInt(getGuardianID());
            dout.writeInt(getGuardianCounter());
            dout.writeUTF(getType());
            dout.writeUTF(getDescription());
            if (getBattleEngine() == null) {
                dout.writeInt(-1);
            } else {
                dout.writeInt(getBattleEngine().getId());
            }
            dout.writeLong(getTimeStamp().getTime());
            if (getStation() == null) {
                dout.writeInt(-1);
            } else {
                dout.writeInt(getStation().getStationId());
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

            setID(din.readInt());
            setGuardianID(din.readInt());
            setGuardianCounter(din.readInt());
            setType(din.readUTF());
            setDescription(din.readUTF());
            setBattleEngine(new BattleEngine());
            getBattleEngine().setId(din.readInt());
            setTimeStamp(new Date(din.readLong()));
            setStation(new Station());
            getStation().setStationId(din.readInt());


            din.close();
            bin.close();

        } catch (Exception e) {
            Logger.getInstance().debug(e);
        }
    }

    /**
     * Generates string based debug message.
     *
     * @return a string containing the info of the event
     */
    public String getDebugInfo() {
        final StringBuffer stringbuf = new StringBuffer(120);
        stringbuf.append(this.getClass().getName());
        stringbuf.append(": ID=");
        stringbuf.append(getID());
        stringbuf.append(", guardianID=");
        stringbuf.append(getGuardianID());
        stringbuf.append(", guardianCounter=");
        stringbuf.append(getGuardianCounter());
        stringbuf.append(", type=");
        stringbuf.append(getType());
        stringbuf.append(", description=");
        stringbuf.append(getDescription());
        stringbuf.append(", timestamp=");
        stringbuf.append(getTimeStamp());
        stringbuf.append(", Station ID=");
        stringbuf.append(getStation().getStationId());
        return stringbuf.toString();
    }
}
