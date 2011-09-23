package eu.funinnumbers.engine.event;

import eu.funinnumbers.db.managers.EventManager;
import eu.funinnumbers.db.managers.GuardianManager;
import eu.funinnumbers.db.model.Avatar;
import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.StorableEntity;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.db.util.HibernateUtil;
import org.hibernate.Transaction;
import eu.funinnumbers.util.Observable;
import eu.funinnumbers.util.Observer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Observes the Engine for incomming/outgoing events.
 */
public final class EventDBWriter implements Observer {

    /**
     * The Battle Engine.
     */
    private final BattleEngine engine;

    /**
     * This Map is obtained by the BattleEngine and contains avatarID's with the guardianID as keys.
     */
    private final Map guardiansAvatarsMap; //NOPMD

    /**
     * This Map contains all the avatars of the game, with tha avatarIDs as keys.
     */
    private final Map avatars;

    /**
     * Default Constructor.
     *
     * @param batEng           the BattleEngine object
     * @param guardiansAvatars the map associating guardians (MAC address) with avatars
     */
    public EventDBWriter(final BattleEngine batEng, final Map guardiansAvatars) {
        engine = batEng;

        // Obtain the guardiansAvatarsMap HashMap from the stationApp
        this.guardiansAvatarsMap = guardiansAvatars;

        // obtain the avatars HashMap from the BattleEngine
        avatars = convertSetToHashMap(batEng.getAvatars());
    }

    /**
     * Converts a Collection of StorableEntity objects to a HashMap.
     * The key of each entry is provided by the StorableEntity interface.
     *
     * @param inputSet the Collection of StorableEntity objects
     * @return the HashMap with the same entities indexed by entity ID
     */
    @SuppressWarnings("unchecked")
    private HashMap convertSetToHashMap(final Collection inputSet) {
        final HashMap outputMap = new HashMap(inputSet.size());

        // Associates every object based on its ID
        final Iterator iter = inputSet.iterator();
        while (iter.hasNext()) {
            final StorableEntity entity = (StorableEntity) iter.next();
            outputMap.put(new Integer(entity.getID()), entity); //NOPMD
        }

        return outputMap;
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param obj the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code> method.
     */
    public void update(final Observable obj, final Object arg) {

        // Make sure this is an Event
        if (!(arg instanceof Event)) {
            return;
        }

        // Cast parameter to object type
        final Event event = (Event) arg;

        // Remove Event ID
        event.setID(0);

        // Start new transaction
        final Transaction transaction = HibernateUtil.getInstance().getSession().beginTransaction();

        // Lookup eu.funinnumbers.guardian record
        final Guardian guardian = GuardianManager.getInstance().getByID(event.getGuardianID());

        // Obtain avatarId from the guardiansAvatarsMap
        final Integer avatarID = (Integer) guardiansAvatarsMap.get(guardian.getAddress());
        final Avatar avatar = (Avatar) avatars.get(avatarID);

        // Make Event Associations
        event.setAvatar(avatar);
        event.setBattleEngine(engine);

        // Try to save
        EventManager.getInstance().add(event);

        // Commit Hibernate Transaction
        transaction.commit();
    }
}


