package eu.funinnumbers.db.managers;

import eu.funinnumbers.db.model.Station;

import java.util.List;


/**
 * This class is responsible for all the basic CRUD functions in the database regarding eu.funinnumbers.station objects. It inherits the
 * AbstractManager class.
 */
public final class StationManager extends AbstractManager<Station> {
    /**
     * static instance(ourInstance) initialized as null.
     */

    private static StationManager ourInstance = null;

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private StationManager() {
        // Nothing to do
    }

    /**
     * StationManager is loaded on the first execution of StationManager.getInstance()
     * or the first access to StationManager.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static StationManager getInstance() {
        synchronized (StationManager.class) {
            if (ourInstance == null) {
                ourInstance = new StationManager();
            }
        }

        return ourInstance;
    }

    /**
     * get the Station from the database that corresponds to the input id.
     *
     * @param entityID the id of the Station.
     * @return a Station.
     */
    public Station getByID(final int entityID) {
        return super.getByID(new Station(), entityID);
    }

    /**
     * deleting the input Station from the database.
     *
     * @param station the Station that we want to delete
     */
    public void delete(final Station station) {
        super.delete(station, station.getStationId());
    }

    /**
     * listing alla the stations from the database.
     *
     * @return a list of all the Stations that exist inside the Station table in the eu.funinnumbers.db.
     */
    public List<Station> list() {
        return super.list(new Station());
    }


}
