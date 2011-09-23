package eu.funinnumbers.db.model;

/**
 * StorableEntity -- The interface that all entities must imlement.
 */
public interface StorableEntity {

    /**
     * The Identity of Event class.
     */
    int EVENTENTITY = 0;

    /**
     * The Identity of Guardian class.
     */
    int GUARDIANENTITY = 1;

    /**
     * The Identity of Motion Event class.
     */
    int MOTIONEVENTENTITY = 2;

    /**
     * The Identity of Action Event class.
     */
    int ACTIONEVENTENTITY = 3;

    /**
     * The Identity of Avatar class.
     */
    int AVATARENTITY = 4;

    /**
     * The Identity of Team class.
     */
    int TEAMENTITY = 5;

    /**
     * The identity of the Action ID.
     */
    int ACTIONIDENTITY = 6;

    /**
     * The identity of the ConfigEntry.
     */
    int CONFIGENTRYENTITY = 7;

    /**
     * The identity of the PointsOfInterest.
     */
    int POINTSOFINTEREST = 8;

    /**
     * Returns the type of the entity.
     *
     * @return the type of the entity
     */
    int getEntityType();

    /**
     * Returns the id of the entity.
     *
     * @return the id of the entity
     */
    int getID();

    /**
     * Sets the id of the entity.
     *
     * @param entityId - the id of the entity
     */
    void setID(final int entityId);

    /**
     * Converts the entity to byte array.
     * Note that the entity's ID should be the first to be saved in the byte array
     *
     * @return the byte array of the entity
     */
    byte[] toByteArray();

    /**
     * Initializes the entity by reading its byte array.
     *
     * @param entityBuffer - the entity's buffer
     */
    void fromByteArray(final byte[] entityBuffer);

}
