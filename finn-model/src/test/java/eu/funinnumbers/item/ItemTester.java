package eu.funinnumbers.item;

import eu.funinnumbers.db.managers.AvatarManager;
import eu.funinnumbers.db.managers.ItemManager;
import eu.funinnumbers.db.model.Avatar;
import eu.funinnumbers.db.model.item.ArtifactItem;
import eu.funinnumbers.db.model.item.AttackItem;
import eu.funinnumbers.db.model.item.DefenseItem;
import eu.funinnumbers.db.model.item.Item;
import eu.funinnumbers.db.util.HibernateUtil;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.hibernate.Transaction;

import java.util.List;


/**
 * This is a junit test for the Item class / table.
 */
public class ItemTester extends TestCase {

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

        //Construct New Avatar
        final Avatar avatar = new Avatar();
        avatar.setID(1);

        // Save Avatar
        AvatarManager.getInstance().add(avatar);

        // Construct new ArtifactItem
        final ArtifactItem artifact = new ArtifactItem();
        artifact.setVictoryPoints(333);
        artifact.setAvatar(avatar);

        // Construct new AttackItem
        final AttackItem attack = new AttackItem();
        attack.setAccuracy(4);
        attack.setAvatar(avatar);

        // Construct new DefenseItem
        final DefenseItem defense = new DefenseItem();
        defense.setPowerDefense(33);
        defense.setAvatar(avatar);

        // Try to save
        ItemManager.getInstance().add(artifact);
        ItemManager.getInstance().add(attack);
        ItemManager.getInstance().add(defense);

        // Check if record was added -- primary key should be assigned a value
        Assert.assertTrue(artifact.getItemId() > 0);
        Assert.assertTrue(attack.getItemId() > 0);
        Assert.assertTrue(defense.getItemId() > 0);

        // Commit Hibernate Transaction
        tranx.commit();
    }

    /**
     * Test Get functionality.
     */
    public void testGetRecord() {
        // Start new transaction
        final Transaction tranx = HibernateUtil.getInstance().getSession().beginTransaction();

        // Construct new Item
        final Item item = ItemManager.getInstance().getByID(1);

        // Check if record was retrieved -- primary key should be assigned the proper value
        Assert.assertTrue(item.getItemId() == 1);

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
        final List<Item> list = ItemManager.getInstance().list();

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
        //final List<Item> list = ItemManager.getInstance().list();

        final Avatar avat = new Avatar();
        avat.setID(1);
        AvatarManager.getInstance().delete(avat);
        /* for (int i = 0; i < list.size(); i++) {
            // Delete all records
            ItemManager.getInstance().delete(list.get(i));
        }*/
        try {
            // Try to load items ID = 1
            ItemManager.getInstance().getByID(1);
        } catch (Exception e) {
            if (!(e.getMessage().equals("org.hibernate.ObjectNotFoundException: No row with the given identifier exists: [eu.funinnumbers.db.model.item.Item#1]"))) {
                Assert.fail("Failed to delete entity");
            }
        }

        // Commit Hibernate Transaction
        tranx.commit();
    }

}
