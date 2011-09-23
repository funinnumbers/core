package eu.funinnumbers.battleEngine;

import eu.funinnumbers.db.managers.BattleEngineManager;
import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.db.util.HibernateUtil;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.hibernate.Transaction;

import java.util.Iterator;
import java.util.List;

/**
 * This is a junit test for the MonkEngine class / table.
 */
public class BattleEngineTester extends TestCase {

    /**
     * Setup the Tester.
     */
    protected void setUp() {
        // no setup is necessary
    }

    /**
     * Testing Function.
     */
    public void testListRecords() {
        // Start new transaction
        final Transaction tranx = HibernateUtil.getInstance().getSession().beginTransaction();

        final List<BattleEngine> list = BattleEngineManager.getInstance().list();
        final Iterator<BattleEngine> iter = list.iterator();
        while (iter.hasNext()) {
            final BattleEngine battleEngine = iter.next();
            /*Logger.getInstance().debug("BattlEngine name :" + battleEngine.getName() + " Battle Engine ID :"
                    + battleEngine.getId());*/
        }

        // Commit Hibernate Transaction
        tranx.commit();
    }

    /**
     * Test Add functionality.
     */
    public void testAddRecord() {
        // Start new transaction
        final Transaction tranx = HibernateUtil.getInstance().getSession().beginTransaction();

        // Construct new Engine
        final BattleEngine battleEngine = new BattleEngine();
        battleEngine.setIpAddr("127.0.0.1");
        battleEngine.setIsAlive(false);

        //Save the record
        BattleEngineManager.getInstance().add(battleEngine);

        // Check if record was added -- primary key should be assigned a value
        Assert.assertTrue(battleEngine.getId() >= 0);

        // Commit Hibernate Transaction
        tranx.commit();
    }

    /**
     * Test Get functionality.
     */
    public void testGetRecord() {
        // Start new transaction
        final Transaction tranx = HibernateUtil.getInstance().getSession().beginTransaction();

        // Construct new MonkEngine
        BattleEngine battleEngine;

        // Try to get
        battleEngine = BattleEngineManager.getInstance().getByID(1);

        // Check if record was retrieved -- primary key should be assigned the proper value
        Assert.assertTrue(battleEngine.getId() == 1);

        // Commit Hibernate Transaction
        tranx.commit();
    }

    /**
     * Test List functionality.
     */
    public void testList() {
        // Start new transaction
        final Transaction tranx = HibernateUtil.getInstance().getSession().beginTransaction();

        // Try to save
        final List<BattleEngine> list = BattleEngineManager.getInstance().list();

        // Check if record was added -- primary key should be assigned a value
        Assert.assertTrue(!list.isEmpty());

        // Commit Hibernate Transaction
        tranx.commit();
    }

    /**
     * Test Delete functionality.
     */
    public void testDeleteRecord() {
        // Start new transaction
        final Transaction tranx = HibernateUtil.getInstance().getSession().beginTransaction();

        // Construct new MonkEngine
        final BattleEngine battleEngine = new BattleEngine();
        battleEngine.setId(1);

        // Delete record ID = 1
        BattleEngineManager.getInstance().delete(battleEngine);


        try {
            // Try to load battleEngine ID = 1
            BattleEngineManager.getInstance().getByID(1);
        } catch (Exception e) {
            if (!(e.getMessage().equals("No row with the given identifier exists: [db.model.BattleEngine#1]"))) {
                Assert.fail("Failed to delete entity");
            }
        }

        // Commit Hibernate Transaction
        tranx.commit();
    }

}

