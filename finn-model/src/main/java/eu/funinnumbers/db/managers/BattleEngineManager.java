package eu.funinnumbers.db.managers;

import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.db.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;


/**
 * This class is responsible for all the basic CRUD functions in the database regarding BattleEngine objects. It
 * inherits the AbstractManager class and inside also exist an other function:
 * getByIp(String) Returns a battleEngine accorind to the input ip address.
 */
public final class BattleEngineManager extends AbstractManager<BattleEngine> {
    /**
     * static instance(ourInstance) initialized as null.
     */
    private static BattleEngineManager ourInstance = null;

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private BattleEngineManager() {
        // Does nothing
    }

    /**
     * BattleEngineManager is loaded on the first execution of BattleEngineManager.getInstance()
     * or the first access to BattleEngineManager.ourInstance, not before.
     *
     * @return ourInstance
     */

    public static BattleEngineManager getInstance() {
        synchronized (BattleEngineManager.class) {
            if (ourInstance == null) {
                ourInstance = new BattleEngineManager();
            }
        }

        return ourInstance;
    }


    /**
     * get the battleEngine from the database that corresponds to the input id.
     *
     * @param entityID the id of the Entity object.
     * @return a Battle Engine object.
     */

    public BattleEngine getByID(final int entityID) {
        return super.getByID(new BattleEngine(), entityID);
    }


    /**
     * deleting the input battleEngine from the database.
     *
     * @param battleEngine the object that we want to delete
     */

    public void delete(final BattleEngine battleEngine) {
        super.delete(battleEngine, battleEngine.getId());
    }

    /**
     * listing all the battleEngines from the database.
     *
     * @return a list of all the records(objects) that exist inside the table related to the input Entity object.
     */
    public List<BattleEngine> list() {
        return super.list(new BattleEngine());
    }

    /**
     * get the battleEngine from the database according to the ipnut ip address.
     *
     * @param battleIp the ip of the battlEngine
     * @return a Battle Engine object.
     */
    public BattleEngine getByIp(final String battleIp) {
        final Session session = HibernateUtil.getInstance().getSession();

        final String stringQuery = "from BattleEngine battleEngine where battleEngine.ipAddr = :battle";
        final Query query = session.createQuery(stringQuery)
                .setParameter("battle", battleIp);

        return (BattleEngine) query.list().get(0);
    }

    /**
     * get the battleEngine from the database according to the ipnut ip address.
     *
     * @param battleIp the ip of the battlEngine
     * @return a Battle Engine object.
     */
    public BattleEngine getAliveByIp(final String battleIp) {
        final Session session = HibernateUtil.getInstance().getSession();

        final String stringQuery = "from BattleEngine battleEngine where battleEngine.isAlive = 1 "
                + " AND battleEngine.ipAddr = :battle";
        final Query query = session.createQuery(stringQuery)
                .setParameter("battle", battleIp);

        return (BattleEngine) query.list().get(0);
    }

    /**
     * Change the status to Ready and set isAlive true to the battleEngine it receives.
     *
     * @param battleEngine The BattleEngine object
     */
    public void startGame(final BattleEngine battleEngine) {
        battleEngine.setStatus("Ready");
        battleEngine.setIsAlive(true);
        BattleEngineManager.getInstance().update(battleEngine);


    }

    /**
     * Change the status and set isAlive false according to the battleEngine it receives and call
     * the releaseUnusedGuardians from GuardianManager.
     *
     * @param battleEngine the battleEngine
     */
    public void endGame(final BattleEngine battleEngine) {
        battleEngine.setIsAlive(false);
        battleEngine.setStatus("Finished");
        update(battleEngine);
        //svGuardianManager.getInstance().releaseUnusedGuardians(battleEngine);
    }

    /**
     * Get the ID of the points of interest of the battle eu.funinnumbers.engine.
     *
     * @param battleID An int with the battle eu.funinnumbers.engine ID
     * @return a list with the points of interest
     */
    @SuppressWarnings("unchecked")
    public List<Integer> getPointsOfInterest(final int battleID) {
        final Session session = HibernateUtil.getInstance().getSession();

        final String stringQuery = "SELECT STATION_ID FROM STATION "
                + " WHERE BATTLE_ID = :battleID  ";

        final Query query = session.createSQLQuery(stringQuery)
                .setParameter("battleID", battleID);

        return query.list();
    }

}

