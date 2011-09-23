package eu.funinnumbers.db.managers;


import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * This class implements the manager for all the functions related to the capabilities. It inherits the
 * AbstractManager class and inside may also exist other functions related to general managing of capabilities
 */
public final class GuardianManager extends AbstractManager<Guardian> {

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static GuardianManager ourInstance = null;

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private GuardianManager() {
        // Does nothing
    }

    /**
     * GuardianManager is loaded on the first execution of GurdianManager.getInstance()
     * or the first access to GurdianManager.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static GuardianManager getInstance() {
        synchronized (GuardianManager.class) {
            if (ourInstance == null) {
                ourInstance = new GuardianManager();
            }
        }

        return ourInstance;
    }

    /**
     * get the entry from the database that corresponds to the input id.
     *
     * @param entityID the id of the Entity object.
     * @return an Entity object.
     */
    public Guardian getByID(final int entityID) {
        return super.getByID(new Guardian(), entityID);
    }

    /**
     * deleting an entry into the database, according to the input object it receives.
     *
     * @param guardian the object that we want to delete
     */
    public void delete(final Guardian guardian) {
        super.delete(guardian, guardian.getID());
    }

    /**
     * listing all the entries from the database related to the input object it receives.
     *
     * @return a list of all the records(objects) that exist inside the table related to the input Entity object.
     */
    public List<Guardian> list() {
        return super.list(new Guardian());
    }

    /**
     * Loads from the database a list of guardians which are associated with a battle eu.funinnumbers.engine.
     *
     * @param battle A battle eu.funinnumbers.engine
     * @return A list with guardians
     */
    @SuppressWarnings("unchecked")
    public List<Guardian> list(final BattleEngine battle) {
        final Session session = HibernateUtil.getInstance().getSession();
        final String stringQuery = "select GUARDIAN.* from GUARDIAN , AVATAR_GUARDIAN, BATTLE_AVATAR "
                + "where GUARDIAN.GUARDIAN_ID = AVATAR_GUARDIAN.GUARDIAN_ID"
                + " AND AVATAR_GUARDIAN.AVATAR_ID = BATTLE_AVATAR.AVATAR_ID"
                + " AND BATTLE_AVATAR.BATTLE_ID = :battleID";

        final Query query = session.createSQLQuery(stringQuery)
                .addEntity(Guardian.class)
                .setParameter("battleID", battle.getId());

        return (List<Guardian>) query.list();
    }

    /**
     * Get a eu.funinnumbers.guardian entry from the database, according to the input mac address it receives.
     *
     * @param macAddress A string with the mac address of the eu.funinnumbers.guardian
     * @return the eu.funinnumbers.guardian with the mac address
     */
    @SuppressWarnings("unchecked")
    public Guardian getByMac(final String macAddress) {
        final Session session = HibernateUtil.getInstance().getSession();

        final List<Guardian> guardians = session.createCriteria(Guardian.class)
                .add(Restrictions.eq("address", macAddress))
                .list();

        return guardians.get(0);
    }

    /**
     * Get a list of the guardians that haven't been set to an Avatar.
     *
     * @param liferayId user's liferay id
     * @return A List with unset Guardians
     */
    @SuppressWarnings("unchecked")
    public List<Guardian> getUnusedGuardians(final int liferayId) {
        final Session session = HibernateUtil.getInstance().getSession();

        final String stringQuery = "SELECT GUARDIAN.* "
                + " FROM GUARDIAN "
                + " LEFT OUTER JOIN AVATAR_GUARDIAN ON GUARDIAN.GUARDIAN_ID = AVATAR_GUARDIAN.GUARDIAN_ID "
                + " WHERE AVATAR_GUARDIAN.GUARDIAN_ID is NULL "
                + " AND (GUARDIAN.LIFERAY_ID = -1 OR GUARDIAN.LIFERAY_ID = :lifeID  )";


        final Query query = session.createSQLQuery(stringQuery)
                .addEntity(Guardian.class)
                .setParameter("lifeID", liferayId);

        return query.list();

    }

    /**
     * Delete the Avatar_Guardian association, according to the battleEngine it receives.
     *
     * @param battleEngine the battleEngine
     */
    public void releaseUnusedGuardians(final BattleEngine battleEngine) {
        final Session session = HibernateUtil.getInstance().getSession();

        final String stringQuery = "DELETE AVATAR_GUARDIAN FROM AVATAR_GUARDIAN, AVATAR, BATTLE_AVATAR "
                + " WHERE AVATAR_GUARDIAN.AVATAR_ID = AVATAR.AVATAR_ID "
                + " AND AVATAR.AVATAR_ID = BATTLE_AVATAR.AVATAR_ID "
                + " AND BATTLE_AVATAR.BATTLE_ID = :battleID";


        final Query query = session.createSQLQuery(stringQuery)
                .setParameter("battleID", battleEngine.getId());

        query.executeUpdate();

    }
}
