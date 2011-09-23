package eu.funinnumbers.db.managers;


import eu.funinnumbers.db.model.Avatar;
import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.db.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;


/**
 * This class is responsible for all the basic CRUD functions in the database regarding event objects.
 * It inherits the AbstractManager class.
 */
public final class EventManager extends AbstractManager<Event> {

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static EventManager ourInstance = null;

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private EventManager() {
    }

    /**
     * EventManager is loaded on the first execution of EventManager.getInstance()
     * or the first access to EventManager.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static EventManager getInstance() {
        synchronized (EventManager.class) {
            if (ourInstance == null) {
                ourInstance = new EventManager();
            }
        }

        return ourInstance;
    }


    /**
     * get the event from the database that corresponds to the input id.
     *
     * @param entityID the id of the Event.
     * @return an Event.
     */
    public Event getByID(final int entityID) {
        return super.getByID(new Event(), entityID);
    }

    /**
     * deleting the input event from the database.
     *
     * @param event the event that we want to delete
     */
    public void delete(final Event event) {
        super.delete(event, event.getID());
    }

    /**
     * listing all events from the database.
     *
     * @return a list of all the events that exist inside the Event table related in the eu.funinnumbers.db.
     */
    public List<Event> list() {
        return super.list(new Event());
    }

    /**
     * Find the winner in hotPotato game.
     *
     * @param battleEngine the BattleEngine of Hot
     * @return The winner
     */
    public Avatar getWinnerAvatar(final BattleEngine battleEngine) {

        final Session session = HibernateUtil.getInstance().getSession();

        //The sql query that will be excecuted in eu.funinnumbers.db. It returns the AVATAR.AVATAR_ID and the TEAM.TEAM_ID
        final String stringQuery = "SELECT  AVATAR.*  "
                + " FROM AVATAR, EVENT "
                + " WHERE AVATAR.AVATAR_ID = EVENT.AVATAR_ID "
                + " AND EVENT.BATTLE_ID = :battleID  "
                + " AND EVENT.DESCRIPION = :desc "
                + " ORDER BY EVENT.TIMESTAMP DESC";

        // Returns an oblect with the result of the query
        final Query query = session.createSQLQuery(stringQuery)
                .addEntity(Avatar.class)
                .setParameter("battleID", battleEngine.getId())
                .setParameter("desc", "I am out!");

        return (Avatar) query.list().get(0);
    }
}
