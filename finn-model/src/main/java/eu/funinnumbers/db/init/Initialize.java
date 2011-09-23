package eu.funinnumbers.db.init; //NOPMD


import eu.funinnumbers.db.managers.AvatarManager;
import eu.funinnumbers.db.managers.BattleEngineManager;
import eu.funinnumbers.db.managers.GuardianManager;
import eu.funinnumbers.db.managers.StationManager;
import eu.funinnumbers.db.managers.TeamManager;
import eu.funinnumbers.db.model.Avatar;
import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.Station;
import eu.funinnumbers.db.model.Team;
import eu.funinnumbers.db.util.HibernateUtil;
import org.hibernate.Transaction;
import eu.funinnumbers.util.Logger;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Initialize the database.
 */
public final class Initialize {

    /**
     * Create a ne battleEngine.
     *
     * @param location Location of the BattleEngine
     * @param counter  BattleEngine' counter
     * @return the BattleEngine
     */
    public static BattleEngine saveEngine(final String location, final int counter) {
        final BattleEngine batteEngine = new BattleEngine();
        batteEngine.setName(location + " Engine " + counter);
        batteEngine.setStartTime(new Date());
        batteEngine.setIpAddr("127.0.0.1"); //NOPMD
        batteEngine.setIsAlive(false);
        batteEngine.setCoordinates("0,0");
        batteEngine.setOwnerId(0);
        batteEngine.setStatus("Waiting for players");

        batteEngine.setStations(setStations(location, batteEngine));

        final Team team = createTeam(location, batteEngine, counter);

        if (counter == 0) {
            batteEngine.setAvatars(setAvatars(location, batteEngine, team));
        } else if (counter > 0) {
            updateAvatars(location, batteEngine, team);
        }

        //batteEngine.addTeam(team);

        return batteEngine;

    }

    /**
     * Update Avatars of a BattlEngine.
     *
     * @param location    Location of the BAttleEngine
     * @param batteEngine the battleengine
     * @param team        the team
     */
    private static void updateAvatars(final String location, final BattleEngine batteEngine, final Team team) {
        final List<Avatar> avList = AvatarManager.getInstance().list();
        final Iterator<Avatar> avIterator = avList.iterator();
        while (avIterator.hasNext()) {
            final Avatar avatar = avIterator.next();
            if (avatar.getName().contains(location)) {
                avatar.addTeam(team);
                avatar.addBattleEngine(batteEngine);
                //avSet.add(avatar);
                AvatarManager.getInstance().update(avatar);

            }
        }

    }

    /**
     * Create a new team.
     *
     * @param location    The location of the BAttleEngine
     * @param batteEngine the BattleEngine
     * @param counter     BattleEngine' counter
     * @return the Team
     */
    private static Team createTeam(final String location, final BattleEngine batteEngine, final int counter) {
        final Team team = new Team();

        team.setName(location + " Team " + counter);
        team.setMaxPlayers(GuardianManager.getInstance().list().size());
        team.setBattleEngine(batteEngine);
        TeamManager.getInstance().add(team);

        return team;
    }

    /**
     * Set Avatars to the BAttleEngine.
     *
     * @param location    Location Of the BattleEngine
     * @param batteEngine the BattlrEngine
     * @param team        the Team
     * @return null
     */
    private static Set setAvatars(final String location, final BattleEngine batteEngine, final Team team) {
        final List<Guardian> guardians = GuardianManager.getInstance().list();
        final Set<Avatar> avatars = new HashSet<Avatar>();
        for (int i = 0; i < guardians.size(); i++) {
            final Avatar avatar = new Avatar(); //NOPMD
            avatar.setName(location + " Avatar " + i);
            avatar.setLiferayId(0);
            avatar.setGamesLost(0);
            avatar.setGamesWon(0);
            avatar.addBattleEngine(batteEngine);
            avatar.addGuardian(guardians.get(i));
            avatar.addTeam(team);

            avatars.add(avatar);
            AvatarManager.getInstance().add(avatar);

        }


        return null;
    }

    /**
     * Set Stations.
     *
     * @param location location of the BattlEngine.
     * @param battle   the BattleEngine
     * @return a Set Of stations
     */
    private static Set setStations(final String location, final BattleEngine battle) {
        final Set<Station> stations = new HashSet<Station>();

        final Station station = new Station();
        station.setName(location + " Station");
        station.setCoordinates("0,0");
        station.setIpAddr("127.0.0.1"); //NOPMD
        final int ledID = 12345;
        station.setLEDId(ledID);
        station.setLocation(location);
        station.setBattleEngine(battle);

        stations.add(station);
        StationManager.getInstance().add(station);

        return stations;
    }

    /**
     * Main function.
     *
     * @param args String Argument
     */
    public static void main(final String[] args) {
        HibernateUtil.setConfiguration("eu/funinnumbers/hibernateHP.cfg.xml");

        final String location = "Hot Potato";
        final int numberOfBattles = 1;


        if (location.isEmpty()) {
            Logger.getInstance().debug("ERROR: Location variable is empty");

        } else {
            final Transaction tranx = HibernateUtil.getInstance().getSession().beginTransaction();
            BattleEngine battleEngine;

            for (int i = 0; i < numberOfBattles; i++) {
                battleEngine = saveEngine(location, i);
                BattleEngineManager.getInstance().add(battleEngine);
            }

            tranx.commit();
        }
    }
}


