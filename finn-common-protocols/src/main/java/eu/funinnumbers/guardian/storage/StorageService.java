package eu.funinnumbers.guardian.storage;

import eu.funinnumbers.db.model.Avatar;
import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.StorableEntity;
import eu.funinnumbers.db.model.Team;
import eu.funinnumbers.db.model.event.ActionEvent;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.db.model.event.MotionEvent;
import eu.funinnumbers.guardian.communication.actionprotocol.ActionID;
import eu.funinnumbers.util.Logger;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreNotOpenException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Enumeration;
import java.util.Vector;

/**
 * StorageService - This class provides the necessary functions in order to read and write
 * from and to the disk, entities of all types.
 */
public final class StorageService { //NOPMD

    /**
     * Record store for guardians.
     */
    private RecordStore guardiansRS;

    /**
     * Record store for avatars.
     */
    private RecordStore avatarsRS;

    /**
     * Record store for temas.
     */
    private RecordStore teamsRS;

    /**
     * Record store for events.
     */
    private RecordStore eventsRS;

    /**
     * Record store for action events.
     */
    private RecordStore actioneventsRS;

    /**
     * Record store for motion events.
     */
    private RecordStore motioneventsRS;

    /**
     * Record store the id of the last action.
     */
    private RecordStore actionIDRS;

    /**
     * Record store for configentries.
     */
    private RecordStore configentriesRS;

    /**
     * Record store for PointsOfInterest.
     */
    private RecordStore poisRS;


    /**
     * Unique instance of this object.
     */
    private static StorageService storage;

    /**
     * Constructor.
     */
    private StorageService() {

        // Intializations
        init();
    }

    /**
     * Intializes recordstores.
     */
    private void init() {

        // Create the recordStores if needed
        try {
            guardiansRS = RecordStore.openRecordStore(getRecordStoreName(StorableEntity.GUARDIANENTITY), true);
            avatarsRS = RecordStore.openRecordStore(getRecordStoreName(StorableEntity.AVATARENTITY), true);
            teamsRS = RecordStore.openRecordStore(getRecordStoreName(StorableEntity.TEAMENTITY), true);
            eventsRS = RecordStore.openRecordStore(getRecordStoreName(StorableEntity.EVENTENTITY), true);
            actioneventsRS = RecordStore.openRecordStore(getRecordStoreName(StorableEntity.ACTIONEVENTENTITY), true);
            motioneventsRS = RecordStore.openRecordStore(getRecordStoreName(StorableEntity.MOTIONEVENTENTITY), true);
            actionIDRS = RecordStore.openRecordStore(getRecordStoreName(StorableEntity.ACTIONIDENTITY), true);
            configentriesRS = RecordStore.openRecordStore(getRecordStoreName(StorableEntity.CONFIGENTRYENTITY), true);
            poisRS = RecordStore.openRecordStore(getRecordStoreName(StorableEntity.POINTSOFINTEREST), true);

        } catch (Exception e) {
            Logger.getInstance().debug("Exception during creating/opening recordstores", e);
        }

        Logger.getInstance().debug("StorageService initialized.");
    }

    /**
     * Returns the unique instance of StorageService.
     *
     * @return a StorageService instance
     */
    public static StorageService getInstance() {

        synchronized (StorageService.class) {
            // Check if an instance has already been created
            if (storage == null) {
                // Create a new instance if not
                Logger.getInstance().debug("Storage Service Created");
                storage = new StorageService();
            }
        }

        // Return the StorageService instance
        return storage;
    }

    /**
     * Adds a new entity to the Storage.
     *
     * @param entity - the entity to be added
     */
    public void add(final StorableEntity entity) { //NOPMD

        // Get byte array
        final byte[] entityBuffer = entity.toByteArray();

        // The corresponding recordStore
        final int entityType = entity.getEntityType();
        final RecordStore recordStore = getRecordStore(entityType);

        // Store to the corresponding recordstore, based on the entity type
        int storageID = -1;

        switch (entityType) {
            case StorableEntity.ACTIONIDENTITY:
                // Check if we have ever stored the actionid record
                int totRecords = 0;
                try {
                    totRecords = recordStore.getNumRecords();
                } catch (RecordStoreNotOpenException ex) {
                    // This cannot happen!
                    Logger.getInstance().debug("Unable to read ACTIONIDENTITY recordstore", ex);

                    // Clear recordstore to be on the safe side
                    clear(StorableEntity.ACTIONIDENTITY);
                }

                // Check if records already exist
                if (totRecords == 0) {
                    storageID = -1;
                } else {
                    // Only 1 record is stored in this RMS
                    storageID = 1;
                }
                break;


            case StorableEntity.CONFIGENTRYENTITY:
            case StorableEntity.GUARDIANENTITY:
            case StorableEntity.POINTSOFINTEREST:
                storageID = find(entity);
                break;

            default:
                break;
        }

        // Add entity
        try {
            if (storageID == -1) {
                recordStore.addRecord(entityBuffer, 0, entityBuffer.length);
            } else {
                recordStore.setRecord(storageID, entityBuffer, 0, entityBuffer.length);
            }

        } catch (Exception e) {
            Logger.getInstance().debug("Exception during adding a new record", e);
        }
    }

    /**
     * Delete a StorableEntity from the Storage.
     *
     * @param entity - the entity to be delete
     */
    public void delete(final StorableEntity entity) {

        // If the entity does not exist in the corresponding recordStore then return
        final int storageID = find(entity);
        if (storageID == -1) {
            return;
        }

        // The corrsponding recordStore
        final RecordStore recordStore = getRecordStore(entity.getEntityType());

        // Delete entry from the corresponding recordstore
        try {
            recordStore.deleteRecord(storageID);

        } catch (Exception e) {
            Logger.getInstance().debug("Exception during deleting a record", e);
        }

    }

    /**
     * Searches for an entity, based on its id, in the corresponding recordstore.
     *
     * @param entity - the given entity
     * @return the record id of the given entity in the corresponding recordstore or -1 if it is not found
     */
    public int find(final StorableEntity entity) {

        byte[] recordBuffer = null; // NOPMD
        return find(entity.getEntityType(), entity.getID(), recordBuffer);
    }

    /**
     * Searches for an entity, based on its id, in the corresponding recordstore.
     *
     * @param entityType   - the entityType of the entity
     * @param entityID     - the id of the entity
     * @param recordBuffer - the record found
     * @return the record id of the given entity in the corresponding recordstore or -1 if it is not found
     */
    public int find(final int entityType, final int entityID, byte[] recordBuffer) { //NOPMD

        // The corrsponding recordStore
        final RecordStore recordStore = getRecordStore(entityType);

        // Search in the recordstore for the entity
        try {
            // Loop until a mathcing record is found
            ByteArrayInputStream bain;
            DataInputStream distream;
            int recordID;
            final int totRecords = recordStore.getNumRecords();
            for (int i = 0; i <= totRecords; i++) {

                // Get next record byte array
                byte[] buffer;
                try {
                    buffer = recordStore.getRecord(i);

                } catch (Exception e) {
                    // The i record is not valid (e.g., it is deleted).
                    // Just move on!
                    continue;
                }

                // Read the entity's id
                // Note that the entity's ID should be the first to be saved
                bain = new ByteArrayInputStream(buffer); //NOPMD
                distream = new DataInputStream(bain); //NOPMD
                recordID = distream.readInt();

                // Check whether the id read matches the entity's id
                if (recordID == entityID) {
                    recordBuffer = buffer;
                    return i;
                }
            }

        } catch (Exception e) {
            Logger.getInstance().debug("Exception during getting a record from a recordstore", e);
        }

        return -1;
    }

    /**
     * Retrieves an entity, based on its id, from the corresponding recordstore.
     *
     * @param entityType - the entityType of the entity
     * @param entityID   - the id of the entity
     * @return the StorableEntity from the corresponding recordstore or null if it is not found
     */
    public StorableEntity get(final int entityType, final int entityID) { //NOPMD

        // The corrsponding recordStore
        final RecordStore recordStore = getRecordStore(entityType);

        // Search in the recordstore for the entity
        try {
            // Loop until a mathcing record is found
            ByteArrayInputStream bain;
            DataInputStream distream;
            int recordID;
            final int totRecords = recordStore.getNumRecords();
            for (int i = 0; i <= totRecords; i++) {

                // Get next record byte array
                byte[] buffer;
                try {
                    buffer = recordStore.getRecord(i);

                } catch (Exception e) {
                    // The i record is not valid (e.g., it is deleted).
                    // Just move on!
                    continue;
                }

                // Read the entity's id
                // Note that the entity's ID should be the first to be saved
                bain = new ByteArrayInputStream(buffer); //NOPMD
                distream = new DataInputStream(bain); //NOPMD
                recordID = distream.readInt();

                // Check whether the id read matches the entity's id
                if (recordID == entityID) {
                    final StorableEntity entity = getStorableEntity(entityType);
                    entity.fromByteArray(buffer);
                    return entity;
                }
            }

        } catch (Exception e) {
            Logger.getInstance().debug("Exception during getting a record from a recordstore", e);
        }

        return null;
    }

    /**
     * Clears the record store of a specific entityType.
     *
     * @param entityType - the entity type
     */
    public void clear(final int entityType) {

        // The corresponding recordStore
        final RecordStore recordStore = getRecordStore(entityType);

        // Delete the recordStore and then open/create it again
        try {
            final Vector ids = new Vector(); // NOPMD
            final RecordEnumeration recordEnumeration = recordStore.enumerateRecords(null, null, true);
            while (recordEnumeration.hasNextElement()) {
                ids.addElement(new Integer(recordEnumeration.nextRecordId())); //NOPMD
            }
            final int totRecords = ids.size();
            for (int i = 0; i < totRecords; i++) {
                recordStore.deleteRecord(((Integer) ids.elementAt(i)).intValue());
            }

        } catch (Exception e) {
            Logger.getInstance().debug("Exception during clearing a recordstore", e);
        }
    }

    /**
     * Clears all the record stores.
     */
    public void clearAll() {

        // Delete the recordStore and then open/create it again
        try {
            getRecordStore(StorableEntity.GUARDIANENTITY).closeRecordStore();
            RecordStore.deleteRecordStore(getRecordStoreName(StorableEntity.GUARDIANENTITY));

            getRecordStore(StorableEntity.AVATARENTITY).closeRecordStore();
            RecordStore.deleteRecordStore(getRecordStoreName(StorableEntity.AVATARENTITY));

            getRecordStore(StorableEntity.TEAMENTITY).closeRecordStore();
            RecordStore.deleteRecordStore(getRecordStoreName(StorableEntity.TEAMENTITY));

            getRecordStore(StorableEntity.EVENTENTITY).closeRecordStore();
            RecordStore.deleteRecordStore(getRecordStoreName(StorableEntity.EVENTENTITY));

            getRecordStore(StorableEntity.ACTIONEVENTENTITY).closeRecordStore();
            RecordStore.deleteRecordStore(getRecordStoreName(StorableEntity.ACTIONEVENTENTITY));

            getRecordStore(StorableEntity.MOTIONEVENTENTITY).closeRecordStore();
            RecordStore.deleteRecordStore(getRecordStoreName(StorableEntity.MOTIONEVENTENTITY));

            getRecordStore(StorableEntity.ACTIONIDENTITY).closeRecordStore();
            RecordStore.deleteRecordStore(getRecordStoreName(StorableEntity.ACTIONIDENTITY));

            getRecordStore(StorableEntity.CONFIGENTRYENTITY).closeRecordStore();
            RecordStore.deleteRecordStore(getRecordStoreName(StorableEntity.CONFIGENTRYENTITY));

            getRecordStore(StorableEntity.POINTSOFINTEREST).closeRecordStore();
            RecordStore.deleteRecordStore(getRecordStoreName(StorableEntity.POINTSOFINTEREST));

            init();

        } catch (Exception e) {
            Logger.getInstance().debug("Exception during clearing a recordstore", e);
        }
    }

    /**
     * Read all entities of a specific type from the corresponding recordstore and return them.
     *
     * @param entityType - the entity type
     * @return the Vector containing all the entities of a specific type
     */
    public Vector listEntities(final int entityType) {
        final Vector entities = new Vector(); // NOPMD

        // The corresponding recordStore
        final RecordStore recordStore = getRecordStore(entityType);

        // Get all the entities in bytearray format
        final Vector recordsBuffer = listRecords(recordStore);

        // Convert entity from byte array to the corresponding entity
        final Enumeration enumeration = recordsBuffer.elements();
        while (enumeration.hasMoreElements()) {

            // The corresponding entity object
            final StorableEntity entity = getStorableEntity(entityType);

            // Get eu.funinnumbers.guardian entity
            final byte[] entityBuffer = (byte[]) enumeration.nextElement();
            entity.fromByteArray(entityBuffer);

            entities.addElement(entity);
        }

        return entities;
    }

    /**
     * Read all records from a specific recordstore and return them.
     *
     * @param recordStore - the recordstore
     * @return the Vector containing all the recirds from a specific recordStore
     */
    private Vector listRecords(final RecordStore recordStore) {

        final Vector recordsBuffer = new Vector();  // NOPMD

        try {
            final RecordEnumeration recordEnumeration = recordStore.enumerateRecords(null, null, false);

            // Loop while a record exists
            while (recordEnumeration.hasNextElement()) {

                // Get next record byte array
                final byte[] recordBuffer = recordEnumeration.nextRecord();
                recordsBuffer.addElement(recordBuffer);
            }

        } catch (Exception e) {
            Logger.getInstance().debug("Exception during listing records", e);
        }

        return recordsBuffer;
    }

    /**
     * Returns the recordstore in which entities of the given entitytype, are stored.
     *
     * @param entityType - the entitytype
     * @return the recordstore in which entities of the given entitytype, are stored
     */
    private RecordStore getRecordStore(final int entityType) { //NOPMD

        RecordStore recordStore = null;

        // Find the correspondig recordstore
        switch (entityType) {
            case StorableEntity.GUARDIANENTITY:
                recordStore = guardiansRS;
                break;

            case StorableEntity.AVATARENTITY:
                recordStore = avatarsRS;
                break;

            case StorableEntity.TEAMENTITY:
                recordStore = teamsRS;
                break;

            case StorableEntity.EVENTENTITY:
                recordStore = eventsRS;
                break;

            case StorableEntity.ACTIONEVENTENTITY:
                recordStore = actioneventsRS;
                break;

            case StorableEntity.MOTIONEVENTENTITY:
                recordStore = motioneventsRS;
                break;

            case StorableEntity.ACTIONIDENTITY:
                recordStore = actionIDRS;
                break;

            case StorableEntity.CONFIGENTRYENTITY:
                recordStore = configentriesRS;
                break;

            case StorableEntity.POINTSOFINTEREST:
                recordStore = poisRS;
                break;
            default:
                break;
        }

        return recordStore;
    }

    /**
     * Returns the recordstore name in which entities of the given entitytype, are stored.
     *
     * @param entityType - the entitytype
     * @return the recordstore name in which entities of the given entitytype, are stored
     */
    private String getRecordStoreName(final int entityType) { //NOPMD

        String recordStoreName = null;

        // Find the correspondig recordstore
        switch (entityType) {
            case StorableEntity.GUARDIANENTITY:
                recordStoreName = "guardians";
                break;

            case StorableEntity.AVATARENTITY:
                recordStoreName = "avatars";
                break;

            case StorableEntity.TEAMENTITY:
                recordStoreName = "teams";
                break;

            case StorableEntity.EVENTENTITY:
                recordStoreName = "events";
                break;

            case StorableEntity.ACTIONEVENTENTITY:
                recordStoreName = "actionevents";
                break;

            case StorableEntity.MOTIONEVENTENTITY:
                recordStoreName = "motionevents";
                break;

            case StorableEntity.ACTIONIDENTITY:
                recordStoreName = "actionID";
                break;

            case StorableEntity.CONFIGENTRYENTITY:
                recordStoreName = "configentries";
                break;

            case StorableEntity.POINTSOFINTEREST:
                recordStoreName = "pointsofinterest";
                break;

            default:
                break;
        }

        return recordStoreName;
    }

    /**
     * Returns an entity object based on the given entitytype.
     *
     * @param entityType - the entitytype
     * @return an entity object based on the given entitytype
     */
    private StorableEntity getStorableEntity(final int entityType) { //NOPMD

        // Find the correspondig recordstore
        switch (entityType) {
            case StorableEntity.GUARDIANENTITY:
                return new Guardian();

            case StorableEntity.AVATARENTITY:
                return new Avatar();

            case StorableEntity.TEAMENTITY:
                return new Team();

            case StorableEntity.EVENTENTITY:
                return new Event();

            case StorableEntity.ACTIONEVENTENTITY:
                return new ActionEvent();

            case StorableEntity.MOTIONEVENTENTITY:
                return new MotionEvent();

            case StorableEntity.ACTIONIDENTITY:
                return new ActionID();

            case StorableEntity.CONFIGENTRYENTITY:
                return new ConfigEntry();

            case StorableEntity.POINTSOFINTEREST:
                return PointsOfInterest.getInstance();
            default:
                break;
        }

        return null;
    }

}
