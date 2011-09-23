package eu.funinnumbers.guardian.storage;

import eu.funinnumbers.db.model.StorableEntity;
import eu.funinnumbers.util.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Contains game specific configuration entries that are stored in the StorageService.
 */
public class ConfigEntry implements StorableEntity {

    /**
     * Identity of ConfigEntry used by Hot Potato game for storing potatoes.
     */
    public static final int CONFIG_HP_POTATO = 1;

    /**
     * Identity of ConfigEntry used by Hot Potato game for storing penalties.
     */
    public static final int CONFIG_HP_PENALTY = 2;

    /**
     * The ID of the configuration entry.
     */
    private int entryID;

    /**
     * The value of the configuration entry.
     */
    private String value;

    /**
     * Returns the type of the entity.
     *
     * @return the type of the entity
     */
    public int getEntityType() {
        return CONFIGENTRYENTITY;
    }

    /**
     * Returns the id of the entity.
     *
     * @return the id of the entity
     */
    public int getID() {
        return entryID;
    }

    /**
     * Get the value of this configuration entry.
     *
     * @return the value of the entry.
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value of this configuration entry.
     *
     * @param valueP the new value of the entry.
     */
    public void setValue(final String valueP) {
        this.value = valueP;
    }

    /**
     * Sets the id of the entity.
     *
     * @param entityId - the id of the entity
     */
    public void setID(final int entityId) {
        entryID = entityId;
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

            dout.writeInt(this.entryID);
            dout.writeUTF(this.value);

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
            setValue(din.readUTF());

            din.close();
            bin.close();

        } catch (Exception e) {
            Logger.getInstance().debug(e);
        }
    }
}
