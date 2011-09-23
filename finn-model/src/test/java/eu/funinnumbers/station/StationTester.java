package eu.funinnumbers.station;


import eu.funinnumbers.db.managers.BattleEngineManager;
import eu.funinnumbers.db.managers.ItemManager;
import eu.funinnumbers.db.managers.StationManager;
import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.db.model.Station;
import eu.funinnumbers.db.util.HibernateUtil;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.hibernate.Transaction;

import java.util.List;

/**
 * This is a junit test for the Station class / table.
 */
public class StationTester extends TestCase {

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
        battleEngine.setId(1);

        //Try to save MonkEngine
        BattleEngineManager.getInstance().add(battleEngine);

        //Construct new stationApp
        final Station station = new Station();
        station.setStationId(1);
        station.setName("Station 1");
        station.setBattleEngine(battleEngine);

        final Station sstation = new Station();
        sstation.setStationId(2);
        sstation.setName("Station 2");
        sstation.setBattleEngine(battleEngine);

        //Try to save
        StationManager.getInstance().add(station);
        StationManager.getInstance().add(sstation);

        // Check if record was added -- primary key should be assigned a value
        Assert.assertTrue(sstation.getStationId() > 0);
        Assert.assertTrue(station.getStationId() > 0);

        // Commit Hibernate Transaction
        tranx.commit();

    }

    /**
     * Test Get functionality.
     */
    public void testGetRecord() {
        // Start new transaction
        final Transaction tranx = HibernateUtil.getInstance().getSession().beginTransaction();

        //Construct new BattleEngine
        BattleEngine battleEngine = new BattleEngine();
        battleEngine = BattleEngineManager.getInstance().getByID(1);

        // Construct new stationApp
        Station station = new Station();

        //Get all the stations of a battle eu.funinnumbers.engine kai print the id and the name
        /*
        for (int i = 0; i < battleEngine.getStations().size(); i++) {
            eu.funinnumbers.station = (Station) battleEngine.getStations().elementAt(i);
            Logger.getInstance().debug(eu.funinnumbers.station.getStationId() + eu.funinnumbers.station.getName());
        }
        */

        // Try to get
        station = StationManager.getInstance().getByID(1);

        // Check if record was retrieved -- primary key should be assigned the proper value
        Assert.assertTrue(station.getStationId() == 1);

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
        final List<Station> list = StationManager.getInstance().list();

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

        // Get Items
        final List<Station> list = StationManager.getInstance().list();

        for (int i = 0; i < list.size(); i++) {
            // Delete all records
            StationManager.getInstance().delete(list.get(i));
        }
        try {
            // Try to load items ID = 1
            ItemManager.getInstance().getByID(1);
        } catch (Exception e) {
            if (!(e.getMessage().equals("org.hibernate.ObjectNotFoundException: No row with the given identifier exists: [db.model.Station#1]"))) {
                Assert.fail("Failed to delete entity");
            }
        }
        // Commit Hibernate Transaction
        tranx.commit();
    }

}
