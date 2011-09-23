package eu.funinnumbers;

import eu.funinnumbers.db.managers.AvatarManager;
import eu.funinnumbers.db.managers.BattleEngineManager;
import eu.funinnumbers.db.model.Avatar;
import eu.funinnumbers.db.util.HibernateUtil;
import junit.framework.TestCase;
import org.hibernate.Transaction;

import java.util.Iterator;
import java.util.List;

import eu.funinnumbers.util.Logger;


/**
 * This is a junit test for the Team class / table.
 */
public class TestDB extends TestCase {

    /**
     * Setup the Tester.
     */
    protected void setUp() {
        // no setup is necessary
        HibernateUtil.setConfiguration("hibernateHP.cfg.xml");
    }

    /** public void testSerialization() {
     try {
     FileOutputStream fos = new FileOutputStream("testSerialization.obj");
     ObjectOutputStream oos = new ObjectOutputStream(fos);

     // Start new transaction
     final Transaction trans = HibernateUtil.getInstance().getSession().beginTransaction();

     // Retrieve BattleEngine from the DB based on IP
     BattleEngine battleEngine = BattleEngineManager.getInstance().getByID(1);
     Logger.getInstance().debug("BattleEngine ID=" + battleEngine.getId() + ", Name=" + battleEngine.getName());

     // Retrieve List of stations
     Set<Station> stationsList = battleEngine.getStations();
     Logger.getInstance().debug("Tot Stations: " + stationsList.size());

     // Commit Hibernate Transaction
     trans.commit();

     oos.writeObject(stationsList);

     oos.close();
     fos.close();

     } catch (Exception e) {
     e.printStackTrace();
     }
     } **/

    /**
     * Test Add functionality.
     */
    public void testAddRecord() {

        /*// Start new transaction
        final Transaction trans = HibernateUtil.getInstance().getSession().beginTransaction();

        // Construct new Avatar
        final Avatar avatar = AvatarManager.getInstance().getByID(1);


        Logger.getInstance().debug(avatar.getEvents().size());

        // Commit Hibernate Transaction
        trans.commit();*/
    }

    /**
     * Test Get functionality.
     */
    public void testGetRecord() {
        /*HibernateUtil.setConfiguration("hibernateHP.cfg.xml");
        int battleID = -1;
        final int teamID = -1;
        final int guardianID = -1;
        final int liferayID = -1;

        //Battle Engine ID
        battleID = 7;
        final Transaction trans = HibernateUtil.getInstance().getSession().beginTransaction();
        final List<Integer> list = BattleEngineManager.getInstance().getPointsOfInterest(battleID);

        final Iterator<Integer> iter = list.iterator();
        while (iter.hasNext()) {
            final int i = iter.next();
            Logger.getInstance().debug(i);


        }

        trans.commit();
*/
    }
}