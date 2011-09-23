package eu.funinnumbers.avatar;

import eu.funinnumbers.db.managers.AvatarManager;
import eu.funinnumbers.db.managers.BattleEngineManager;
import eu.funinnumbers.db.managers.GuardianManager;
import eu.funinnumbers.db.managers.TeamManager;
import eu.funinnumbers.db.model.Avatar;
import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.Team;
import eu.funinnumbers.db.util.HibernateUtil;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.hibernate.Transaction;

import java.util.List;


/**
 * This is a junit test for the Avatar class / table.
 */
public class AvatarTester extends TestCase {

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

        // Construct new BattleEngine
        final BattleEngine battleEngine = new BattleEngine();
        battleEngine.setId(1);
        battleEngine.setIsAlive(true);
        BattleEngineManager.getInstance().add(battleEngine);

        //Construct new Team
        final Team team = new Team();

        // Construct new Avatars
        final Avatar avatar = new Avatar();
        avatar.setID(1);
        avatar.setName("My Avatar 1");

        //Construct new Guardian
        final Guardian guardian = new Guardian();
        guardian.setID(1);

        GuardianManager.getInstance().add(guardian);

        final Avatar avatar2 = new Avatar();
        avatar2.setID(2);

        avatar2.setName("My Avatar 2");


        team.setBattleEngine(battleEngine);

        // Try to save

        AvatarManager.getInstance().add(avatar);
        AvatarManager.getInstance().add(avatar2);
        TeamManager.getInstance().add(team);

        // Check if record was added -- primary key should be assigned a value
        Assert.assertTrue(avatar.getID() >= 0);

        // Commit Hibernate Transaction
        tranx.commit();

    }

    /**
     * Test Get functionality.
     */
    public void testGetRecord() {
        // Start new transaction
        final Transaction tranx = HibernateUtil.getInstance().getSession().beginTransaction();

        // Construct new Avatar
        Avatar avatar = new Avatar();

        // Try to get
        avatar = AvatarManager.getInstance().getByID(1);

        // Check if record was retrieved -- primary key should be assigned the proper value
        Assert.assertTrue(avatar.getID() == 1);

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
        final List<Avatar> list = AvatarManager.getInstance().list();

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

        // Construct new Avatar
        final Avatar avatar = new Avatar();
        avatar.setID(1);

        // Delete record ID = 1
        AvatarManager.getInstance().delete(avatar);


        try {
            // Try to load avatar ID = 1
            AvatarManager.getInstance().getByID(1);
        } catch (Exception e) {
            if (!(e.getMessage().equals("No row with the given identifier exists: [eu.funinnumbers.db.model.Avatar#1]"))) {
                Assert.fail("Failed to delete entity");
            }
        }

        // Commit Hibernate Transaction
        tranx.commit();
    }

}
