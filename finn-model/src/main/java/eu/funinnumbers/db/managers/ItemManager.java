package eu.funinnumbers.db.managers;

import eu.funinnumbers.db.model.item.Item;

import java.util.List;

/**
 * This class is responsible for all the basic CRUD functions in the database regarding Item objects. It inherits the
 * AbstractManager class.
 */
public final class ItemManager extends AbstractManager<Item> {

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static ItemManager ourInstance = null;

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private ItemManager() {
        // Nothing to do
    }

    /**
     * ItemManager is loaded on the first execution of ItemManager.getInstance()
     * or the first access to ItemManager.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static ItemManager getInstance() {
        synchronized (ItemManager.class) {
            if (ourInstance == null) {
                ourInstance = new ItemManager();
            }
        }

        return ourInstance;
    }

    /**
     * get the item from the database that corresponds to the input id.
     *
     * @param entityID the id of the Item.
     * @return an Item.
     */
    public Item getByID(final int entityID) {
        return super.getByID(new Item(), entityID);
    }

    /**
     * deleting the input Item from the database.
     *
     * @param item the item that we want to delete
     */
    public void delete(final Item item) {
        super.delete(item, item.getItemId());
    }

    /**
     * listing all the items from the database.
     *
     * @return a list of all the Items that exist inside the Item table in the eu.funinnumbers.db.
     */
    public List<Item> list() {
        return super.list(new Item());
    }


}
