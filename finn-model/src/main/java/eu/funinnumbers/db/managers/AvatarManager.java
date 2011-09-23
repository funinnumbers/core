package eu.funinnumbers.db.managers;

import eu.funinnumbers.db.model.Avatar;
import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.db.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * This class is responsible for all the basic CRUD(insert,select,update,delete) functions in the database regarding
 * avatar objects. It inherits the AbstractManager class and inside also exist two other functions:
 * getAvatarTeamMap(BattleEngine) , getGuardianAvatarMap(BattleEngine)
 */
public final class AvatarManager extends AbstractManager<Avatar> {

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static AvatarManager ourInstance = null;

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private AvatarManager() {
        // Does nothing
    }

    /**
     * AvatarManager is loaded on the first execution of AvatarManager.getInstance()
     * or the first access to AvatarManager.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static AvatarManager getInstance() {
        synchronized (AvatarManager.class) {
            if (ourInstance == null) {
                ourInstance = new AvatarManager();
            }
        }

        return ourInstance;
    }


    /**
     * get the Avatar from the database that corresponds to the input id.
     *
     * @param entityID the id of the Entity object.
     * @return an Entity object.
     */
    public Avatar getByID(final int entityID) {
        return super.getByID(new Avatar(), entityID);
    }

    /**
     * Delete the input Avatar from the database.
     *
     * @param avatar the avatar tha we want to delete
     */
    public void delete(final Avatar avatar) {
        super.delete(avatar, avatar.getID());
    }

    /**
     * Listing all the Avatars from the database.
     *
     * @return a list of all the Avatars that exist inside the table Avatar.
     */
    public List<Avatar> list() {
        return super.list(new Avatar());
    }

    /**
     * Get a hashMap(eu.funinnumbers.guardian.Address , avatar.avatarID) that associates
     * a eu.funinnumbers.guardian and an avatar according to the battleEngine that this method receives.
     *
     * @param battleEngine The battleEngine
     * @return a HashMap(String, Integer)
     */
    public HashMap<String, Integer> getGuardianAvatarMap(final BattleEngine battleEngine) {
        final HashMap<String, Integer> guardianAvatarMap = new HashMap<String, Integer>();
        final Session session = HibernateUtil.getInstance().getSession();

        //The sql query that will be excecuted in eu.funinnumbers.db. It returns the GUARDIAN.ADDRESS and the AVATAR.AVATAR_ID
        final String stringQuery = "select GUARDIAN.ADDRESS , AVATAR.AVATAR_ID "
                + "from GUARDIAN , AVATAR, AVATAR_GUARDIAN, BATTLE_AVATAR "
                + "where GUARDIAN.GUARDIAN_ID = AVATAR_GUARDIAN.GUARDIAN_ID"
                + " AND AVATAR_GUARDIAN.AVATAR_ID = AVATAR.AVATAR_ID "
                + " AND AVATAR.AVATAR_ID = BATTLE_AVATAR.AVATAR_ID "
                + " AND BATTLE_AVATAR.BATTLE_ID = :battleID";

        // Returns an oblect with the result of the query
        final Query query = session.createSQLQuery(stringQuery)
                .setParameter("battleID", battleEngine.getId());

        //Convert the results from object to array and put them in the HashMap
        final List arList = query.list();
        final Iterator ire = arList.iterator();
        while (ire.hasNext()) {
            Object[] testAr;
            testAr = (Object[]) ire.next();
            guardianAvatarMap.put((String) testAr[0], (Integer) testAr[1]);
        }

        return guardianAvatarMap;
    }


    /**
     * Get a hashMap(avatar.avatarID , team.teamID) that associates
     * an avatar and a team according to the battleEngine that this method receives.
     *
     * @param battleEngine The battleEngine
     * @return a HashMap(Integer, Integer)
     */
    public HashMap getAvatarTeamMap(final BattleEngine battleEngine) {
        final HashMap<Integer, Integer> avatarTeamMap = new HashMap<Integer, Integer>();
        final Session session = HibernateUtil.getInstance().getSession();

        //The sql query that will be excecuted in eu.funinnumbers.db. It returns the AVATAR.AVATAR_ID and the TEAM.TEAM_ID
        final String stringQuery = "SELECT  AVATAR.AVATAR_ID, TEAM.TEAM_ID "
                + " FROM TEAM, AVATAR, AVATAR_TEAM, BATTLE_AVATAR "
                + " WHERE TEAM.TEAM_ID = AVATAR_TEAM.TEAM_ID "
                + " AND AVATAR_TEAM.AVATAR_ID = AVATAR.AVATAR_ID "
                + " AND AVATAR.AVATAR_ID = BATTLE_AVATAR.AVATAR_ID "
                + " AND BATTLE_AVATAR.BATTLE_ID = :battleID "
                + " AND TEAM.BATTLE_ID = :battleID ";

        // Returns an oblect with the result of the query
        final Query query = session.createSQLQuery(stringQuery)
                .setParameter("battleID", battleEngine.getId());

        //Convert the results from object to array and put them in the HashMap
        final List atList = query.list();
        final Iterator ire = atList.iterator();
        while (ire.hasNext()) {
            Object[] testAr;
            testAr = (Object[]) ire.next();
            avatarTeamMap.put((Integer) testAr[0], (Integer) testAr[1]);
        }

        return avatarTeamMap;

    }

    /**
     * Locates the avatar object based on the name of the avatar.
     *
     * @param name the name of the avatar to lookup
     * @return the Avatar object
     */
    @SuppressWarnings("unchecked")
    public Avatar getByName(final String name) {
        final Session session = HibernateUtil.getInstance().getSession();

        final List<Avatar> avatars = session.createCriteria(Avatar.class)
                .add(Restrictions.eq("name", name))
                .list();

        return avatars.get(0);

    }

    /**
     * Locates the avatar object based on the liferay id of the avatar.
     *
     * @param liferayId the Liferay ID of an avatar
     * @return the Avatar object
     */
    @SuppressWarnings("unchecked")
    public List<Avatar> getByLiferayID(final int liferayId) {
        final Session session = HibernateUtil.getInstance().getSession();

        final List<Avatar> avatars = session.createCriteria(Avatar.class)
                .add(Restrictions.eq("liferayId", liferayId))
                .list();

        return avatars;

    }
}

