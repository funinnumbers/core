package eu.funinnumbers.db.model.event;

import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.util.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Date;

/**
 * Represents an event generated when a avatar/player/eu.funinnumbers.guardian changes position (i.e. changes eu.funinnumbers.station)
 */    /*Do not remove the comment*/
public class MotionEvent extends Event /*implements java.io.Serializable*/ {

    /**
     * Required by Serializable interface.
     */
    static final long serialVersionUID = 42L; //NOPMD

    /**
     * Set true if a eu.funinnumbers.guardian is leaving from a eu.funinnumbers.station else false.
     */
    private boolean isLeaving;

    /**
     * Constructor with no arguments.
     */
    public MotionEvent() {
        super();
    }

    /**
     * Constructor used by guardians.
     *
     * @param guardianID        Id of the eu.funinnumbers.guardian that created the event
     * @param guardianCounter   Counter of the eu.funinnumbers.guardian that created the event
     * @param guardianAddress   String describing the MAC address of the eu.funinnumbers.guardian
     * @param guardianLastAlive Date of the last beacon received from the eu.funinnumbers.guardian
     */
    public MotionEvent(final int guardianID, final int guardianCounter,
                       final String guardianAddress, final Date guardianLastAlive) {
        super(guardianID, guardianCounter, "MotionEvent",
                "Guardian motion from " + guardianAddress + " @ " + guardianLastAlive);
    }


    /**
     * Returns true if a eu.funinnumbers.guardian is leaving from the staion.
     *
     * @return The boolean variable isLeaving
     */
    public boolean getIsLeaving() { // NOPMD
        return isLeaving;
    }

    /**
     * Set isLeaving true if the eu.funinnumbers.guardian is leaving the eu.funinnumbers.station.
     *
     * @param alive A boolean
     */
    public void setIsLeaving(final boolean alive) {
        isLeaving = alive;
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

            dout.writeBoolean(getIsLeaving());


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

            setIsLeaving(din.readBoolean());


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
        stringbuf.append(super.getDebugInfo());
        stringbuf.append(", isLeaving=");
        stringbuf.append(getIsLeaving());


        return stringbuf.toString();
    }


}
