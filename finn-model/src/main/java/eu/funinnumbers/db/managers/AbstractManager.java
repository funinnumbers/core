package eu.funinnumbers.db.managers;

import eu.funinnumbers.db.util.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.List;

/**
 * This class implements an abstract manager which is responsible for all the basic CRUD(insert,select,update,delete)
 * functions in the database. Other sub-classes inherit from this one. This means that every sub-class can use all
 * the methods that are implemented here and consequently there is no need of defining the CRUD functions in every
 * class. Templates are used here as a generic to all the class types that are defined inside the logic package.
 *
 * @param <E> Generic type of AbstractManager
 */
public abstract class AbstractManager<E> { // NOPMD

    /**
     * adding a new entry into the database, according to the input object it receives.
     *
     * @param entity an Entity object that may be of every type of entity.
     */
    public void add(final E entity) {
        final Session session = HibernateUtil.getInstance().getSession();
        session.save(entity);
    }

    /**
     * updating an entry into the database, according to the input object it receives.
     *
     * @param entity an Entity object that may be of every type of entity.
     */
    public void update(final E entity) {
        final Session session = HibernateUtil.getInstance().getSession();
        session.merge(entity);
    }

    /**
     * deleting an entry into the database, according to the input object it receives.
     *
     * @param entity   an Entity object that may be of every type of entity.
     * @param entityID the id of the Entity object.
     */
    public void delete(final E entity, final int entityID) {
        final Session session = HibernateUtil.getInstance().getSession();
        final Object entity2 = session.load(entity.getClass(), entityID);
        session.delete(entity2);
    }

    /**
     * listing all the entries from the database related to the input object it receives.
     *
     * @param entity an Entity object that may be of every type of entity.
     * @return a list of all the records(objects) that exist inside the table related to the input Entity object.
     */
    @SuppressWarnings("unchecked")
    protected List<E> list(final E entity) {
        final Session session = HibernateUtil.getInstance().getSession();
        final Criteria criteria = session.createCriteria(entity.getClass());
        return (List<E>) criteria.list();

    }

    /**
     * get the entry from the database that corresponds to the input id.
     *
     * @param entity   an Entity object that may be of every type of entity.
     * @param entityID the id of the Entity object.
     * @return an Entity object.
     */
    @SuppressWarnings("unchecked")
    protected E getByID(final E entity, final int entityID) {
        final Session session = HibernateUtil.getInstance().getSession();
        return (E) session.get(entity.getClass(), entityID);
    }

}
