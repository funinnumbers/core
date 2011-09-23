package eu.funinnumbers.event;


import eu.funinnumbers.db.managers.BattleEngineManager;
import eu.funinnumbers.db.managers.EventManager;
import eu.funinnumbers.db.managers.StationManager;
import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.db.model.Station;
import eu.funinnumbers.db.model.event.ActionEvent;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.db.model.event.MotionEvent;
import eu.funinnumbers.db.util.HibernateUtil;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.hibernate.Transaction;

import java.util.List;


/**
 * This is a junit test for the Event class / table.
 */
public class EventTester extends TestCase {

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

        //Try to save BattleEngine
        BattleEngineManager.getInstance().add(battleEngine);

        // Construct new Station
        final Station station = new Station();

        // Try to save Station
        StationManager.getInstance().add(station);

        // Construct new ActionEvent
        final ActionEvent actionE = new ActionEvent();
        actionE.setType("action");
        actionE.setIsSuccess(true);
        actionE.setBattleEngine(battleEngine);

        // Construct new MotionEvent
        final MotionEvent motionE = new MotionEvent();
        motionE.setType("Motion");
        motionE.setIsLeaving(true);
        motionE.setStation(station);
        motionE.setBattleEngine(battleEngine);

        // Try to save
        EventManager.getInstance().add(actionE);
        EventManager.getInstance().add(motionE);

        // Check if record was added -- primary key should be assigned a value
        Assert.assertTrue(actionE.getID() > 0);
        Assert.assertTrue(motionE.getID() > 0);

        // Commit Hibernate Transaction
        tranx.commit();

    }

    /**
     * Test Get functionality.
     */
    public void testGetRecord() {
        // Start new transaction
        final Transaction tranx = HibernateUtil.getInstance().getSession().beginTransaction();

        // Construct new Event
        Event event;

        // Try to get
        event = EventManager.getInstance().getByID(1);

        // Check if record was retrieved -- primary key should be assigned the proper value
        Assert.assertTrue(event.getID() == 1);

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
        final List<Event> list = EventManager.getInstance().list();

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

        // Get Events
        final List<Event> list = EventManager.getInstance().list();

        for (int i = 0; i < list.size(); i++) {
            // Delete all records
            EventManager.getInstance().delete(list.get(i));
        }

        try {
            // Try to load events ID = 1
            EventManager.getInstance().getByID(1);
        } catch (Exception e) {
            if (!(e.getMessage().equals("org.hibernate.ObjectNotFoundException: No row with the given identifier exists: [eu.funinnumbers.db.model.event.Event#1]"))) {
                Assert.fail("Failed to delete entity");
            }

        }

        // Commit Hibernate Transaction
        tranx.commit();
    }


}
