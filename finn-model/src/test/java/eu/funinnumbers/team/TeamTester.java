package eu.funinnumbers.team;

import eu.funinnumbers.db.managers.BattleEngineManager;
import eu.funinnumbers.db.managers.TeamManager;
import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.db.model.Team;
import eu.funinnumbers.db.util.HibernateUtil;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.hibernate.Transaction;

import java.util.List;

import eu.funinnumbers.util.Logger;


/**
 * This is a junit test for the Team class / table.
 */
public class TeamTester extends TestCase {

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
        final BattleEngine battleEngine = new BattleEngine();
        //Try to save
        battleEngine.setName("Testing Bat");

        // Construct new Team
        final Team team = new Team();
        team.setName("My Team");
        team.setBattleEngine(battleEngine);

        // Try to save
        BattleEngineManager.getInstance().add(battleEngine);
        TeamManager.getInstance().add(team);

        // Check if record was added -- primary key should be assigned a value

        // Commit Hibernate Transaction
        tranx.commit();

    }

    /**
     * Test Get functionality.
     */
    public void testGetRecord() {
        // Start new transaction
        final Transaction tranx = HibernateUtil.getInstance().getSession().beginTransaction();

        // Construct new Team
        Team team = new Team();

        // Try to get
        team = TeamManager.getInstance().getByID(1);

        Logger.getInstance().debug(team.getBattleEngine().getId());
        // Check if record was retrieved -- primary key should be assigned the proper value
        Assert.assertTrue(team.getTeamId() == 1);

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
        final List<Team> list = TeamManager.getInstance().list();

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

        // Construct new Team
        final Team team = new Team();
        team.setTeamId(1);

        // Delete record ID = 1
        TeamManager.getInstance().delete(team);

        try {
            // Try to load team ID = 1
            TeamManager.getInstance().getByID(1);
        } catch (Exception e) {
            if (!(e.getMessage().equals("No row with the given identifier exists: [eu.funinnumbers.db.model.Team#1]"))) {
                Assert.fail("Failed to delete entity");
            }
        }
        // Commit Hibernate Transaction
        tranx.commit();
    }

}
