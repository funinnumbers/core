package eu.funinnumbers.db.managers;


import eu.funinnumbers.db.model.Team;

import java.util.List;


/**
 * This class is responsible for all the basic CRUD functions in the database regarding Team objects. It inherits the
 * AbstractManager class.
 */
public final class TeamManager extends AbstractManager<Team> {

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static TeamManager ourInstance = null;

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private TeamManager() {
        // Nothing to do
    }

    /**
     * TeamManager is loaded on the first execution of TeamManager.getInstance()
     * or the first access to TeamManager.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static TeamManager getInstance() {
        synchronized (TeamManager.class) {
            if (ourInstance == null) {
                ourInstance = new TeamManager();
            }
        }

        return ourInstance;
    }


    /**
     * get the Team from the database that corresponds to the input id.
     *
     * @param entityID the id of the Team.
     * @return a Team.
     */
    public Team getByID(final int entityID) {
        return super.getByID(new Team(), entityID);
    }

    /**
     * deleting the input Team from the database.
     *
     * @param team the Team that we want to delete
     */
    public void delete(final Team team) {
        super.delete(team, team.getTeamId());
    }

    /**
     * listing all teams from the database.
     *
     * @return a list of all the teams that exist inside the Team table in the eu.funinnumbers.db.
     */
    public List<Team> list() {
        return super.list(new Team());
    }


}
