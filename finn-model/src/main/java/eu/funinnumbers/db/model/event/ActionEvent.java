package eu.funinnumbers.db.model.event;

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
 * Represents an event related to a player action.
 */ /*Do not remove the comment*/
public class ActionEvent extends Event /*implements java.io.Serializable*/ {

    /**
     * Required by Serializable interface.
     */
    static final long serialVersionUID = 42L; //NOPMD

    /**
     * If an ActionEvent is successful, set isSuccess = true else false.
     */
    private boolean isSuccess;

    /**
     * Constructor with no arguments.
     */
    public ActionEvent() {
        super();
        // Nothing to do
    }

    /**
     * Copy constructor from an Event object.
     *
     * @param parent the base object
     */
    public ActionEvent(final Event parent) {
        super(parent.getGuardianID(),
                parent.getGuardianCounter(),
                parent.getID(),
                parent.getTimeStamp(),
                parent.getType(),
                parent.getDescription(),
                parent.getBattleEngine());
    }

    /**
     * Get the success of an action event.
     *
     * @return A boolean: True Success False Failure
     */
    public boolean getIsSuccess() {  // NOPMD
        return isSuccess;
    }

    /**
     * Set the success of an action event.
     *
     * @param success A boolean with the success
     */
    public void setIsSuccess(final boolean success) {
        isSuccess = success;
    }

    /**
     * Returns the type of the entity.
     *
     * @return the type of the entity
     */
    public int getEntityType() {
        return StorableEntity.ACTIONEVENTENTITY;
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

            dout.writeBoolean(getIsSuccess());
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

            setIsSuccess(din.readBoolean());
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
        final StringBuffer stringbuf = new StringBuffer();
        stringbuf.append(super.getDebugInfo());
        stringbuf.append(", isSuccess=");
        stringbuf.append(getIsSuccess());
        return stringbuf.toString();
    }

}
