package eu.funinnumbers.guardian.communication.actionprotocol;

import eu.funinnumbers.db.model.StorableEntity;
import eu.funinnumbers.util.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * ActionID represents the last action identifier.
 * This id is unique for each eu.funinnumbers.guardian and action performed.
 * It should be increased upon action performance.
 */
public class ActionID implements StorableEntity {

    /**
     * ID used in StorageService.
     */
    private static int storageID = 1;

    /**
     * The current action identifier value.
     */
    public static int actionID; //NOPMD

    /**
     * Returns the type of the entity.
     *
     * @return the type of the entity
     */
    public int getEntityType() {
        return StorableEntity.ACTIONIDENTITY;
    }

    /**
     * Returns the id of the entity.
     *
     * @return the id of the entity
     */
    public int getID() {
        return storageID;
    }

    /**
     * Sets the id of the entity.
     *
     * @param entityId - the id of the entity
     */
    public void setID(final int entityId) {
        storageID = 1;
    }

    /**
     * @return the action identifier integer.
     */
    public int getActionID() {
        // Return the updated member variable
        return actionID;
    }

    /**
     * Sets the value of the action identifier.
     *
     * @param actID value to set
     */
    public void setActionID(final int actID) {
        // Set value to the member variable
        actionID = actID;
    }

    /**
     * Increase by 1 the action identifier and updates the stored copy.
     *
     * @return the new actionID
     */
    public int increaseActionID() {
        // Increase member variable
        final int increasedID = ++actionID;
        setActionID(increasedID);
        return increasedID;
    }

    /**
     * Converts the entity to byte array.
     * Note that the entity's ID should be the first to be saved in the byte array.
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

            dout.flush();
            dout.close();

            data = bout.toByteArray();

        } catch (Exception e) {
            Logger.getInstance().debug(e);

        }
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

            din.close();
            bin.close();

        } catch (Exception e) {
            Logger.getInstance().debug(e);
        }
    }


}
