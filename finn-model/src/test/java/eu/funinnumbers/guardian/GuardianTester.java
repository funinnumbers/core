package eu.funinnumbers.guardian;

import eu.funinnumbers.db.managers.BattleEngineManager;
import eu.funinnumbers.db.managers.GuardianManager;
import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.util.HibernateUtil;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.hibernate.Transaction;

import java.util.List;


/**
 * This is a junit test for the Guardian class / table.
 */
public class GuardianTester extends TestCase {

    /**
     * Setup the Tester.
     */
    protected void setUp() {
        // no setup is necessary
    }

    /**
     * Test Add functionality.
     */
    public void testAddRecord() {
        // Start new transaction
        final Transaction tranx = HibernateUtil.getInstance().getSession().beginTransaction();

        //Construct new MonkEngine
        final BattleEngine battle = new BattleEngine();

        //Try to save
        BattleEngineManager.getInstance().add(battle);

        // Construct new Guardian
        final Guardian guard = new Guardian();

        // Try to save
        GuardianManager.getInstance().add(guard);

        // Check if record was added -- primary key should be assigned a value
        Assert.assertTrue(guard.getID() > 0);

        // Commit Hibernate Transaction
        tranx.commit();
    }

    /**
     * Test Get functionality.
     */
    public void testGetRecord() {
        // Start new transaction
        final Transaction tranx = HibernateUtil.getInstance().getSession().beginTransaction();

        // Construct new Guardian
        Guardian guard = new Guardian();

        // Try to get
        guard = GuardianManager.getInstance().getByID(1);

        // Check if record was retrieved -- primary key should be assigned the proper value
        Assert.assertTrue(guard.getID() == 1);

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
        final List<Guardian> list = GuardianManager.getInstance().list();

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

        // Construct new Guardians
        final Guardian guard = new Guardian();
        final Guardian guard2 = new Guardian();

        guard.setID(1);
        guard2.setID(1);

        // Delete record ID = 1
        GuardianManager.getInstance().delete(guard2);

        try {
            // Try to load items ID = 1
            GuardianManager.getInstance().getByID(1);
        } catch (Exception e) {
            if (!(e.getMessage().equals("No row with the given identifier exists: [eu.funinnumbers.db.model.Guardian#1]"))) {
                Assert.fail("Failed to delete entity");
            }
        }

        // Commit Hibernate Transaction
        tranx.commit();
    }

}
