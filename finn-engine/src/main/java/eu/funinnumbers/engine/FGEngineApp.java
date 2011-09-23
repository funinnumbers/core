package eu.funinnumbers.engine;

import eu.funinnumbers.db.managers.AvatarManager;
import eu.funinnumbers.db.managers.BattleEngineManager;
import eu.funinnumbers.db.managers.GuardianManager;
import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.Station;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.db.util.HibernateUtil;
import eu.funinnumbers.engine.event.Coordinator;
import eu.funinnumbers.engine.event.EventDBWriter;
import eu.funinnumbers.engine.rmi.FGEngineInterface;
import eu.funinnumbers.engine.rmi.FGEngineRMIImpl;
import eu.funinnumbers.engine.util.GenericRMIServer;
import org.hibernate.Transaction;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observer;
import eu.funinnumbers.util.eventconsumer.EventConsumer;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * First Generation Egnine Application.
 *
 * @author ichatz
 */
public abstract class FGEngineApp extends AbstractEngineApp { //NOPMD

    /**
     * Time period for incactive threads to sleep.
     */
    public static final int THREAD_SLEEP = 5000;

    /**
     * Instance of the EventCoordinator.
     */
    private final Coordinator coordinator;

    /**
     * The BattleEngine record of the game.
     */
    private final BattleEngine battleEngine;

    /**
     * The Stations of the Engine index by IP.
     * (needed for future look-ups)
     */
    private final HashMap<String, Station> stationsMap = new HashMap<String, Station>();

    /**
     * The Guardians of the Game.
     */
    private final List<Guardian> guardiansList;

    /**
     * The HashMap containing the association between guardians and avatars.
     */
    private final HashMap guardiansAvatarsMap; //NOPMD

    /**
     * The HashMap containing the association between avatars and teams.
     */
    private final HashMap avatarsTeamsMap;

    /**
     * List with Points Of Interest.
     */
    private final List<Integer> pointsOfInterest;


    /**
     * Queue where RMI adds events which received from Station.
     */
    protected BlockingQueue<Event> stationsEventQueue = new LinkedBlockingQueue<Event>(); //NOPMD


    /**
     * Default constructor.
     *
     * @throws RemoteException if RMI unable to connect
     */
    protected FGEngineApp() throws RemoteException { //NOPMD
        super();

        // Configure DB
        configureHibernate();

        // Start DB transaction
        final Transaction trans = HibernateUtil.getInstance().getSession().beginTransaction();

        // Retrieve BattleEngine from the DB based on IP
        battleEngine = BattleEngineManager.getInstance().getAliveByIp(getMyIP());
        Logger.getInstance().debug("BattleEngine ID=" + battleEngine.getId() + ", Name=" + battleEngine.getName());

        //Change the game status to "Started"
        battleEngine.setStatus("Started");

        //Update the battleEngine record
        BattleEngineManager.getInstance().update(battleEngine);

        // Set the build.xml file to the BattleEngine object
        battleEngine.setBuildXMLpath(getBuildXMLpath());
        Logger.getInstance().debug("BattleEngine build.xml=" + battleEngine.getBuildXMLpath());

        // Construct HashMap for Stations indexed by IP
        for (Station station1 : battleEngine.getStations()) {
            stationsMap.put(station1.getIpAddr(), station1);
            Logger.getInstance().debug("Station ID=" + station1.getStationId()
                    + ", LEDID=" + station1.getLEDId()
                    + ", Name=" + station1.getName());
        }

        // Retrieve list of Guardians
        guardiansList = GuardianManager.getInstance().list(battleEngine);

        // Debug Guardians
        for (final Iterator guardianIterator = guardiansList.iterator(); guardianIterator.hasNext();) {
            final Guardian guardian = (Guardian) guardianIterator.next();
            Logger.getInstance().debug("Guardian ID=" + guardian.getID());
        }

        // Retrieve Guardians-Avatars Map
        guardiansAvatarsMap = AvatarManager.getInstance().getGuardianAvatarMap(battleEngine);

        // Debug Guardians - Avatar associations
        for (final Iterator entryIterator = guardiansAvatarsMap.entrySet().iterator(); entryIterator.hasNext();) {
            final Map.Entry entry = (Map.Entry) entryIterator.next();
            Logger.getInstance().debug("Guardian-Avatar Key=" + entry.getKey() + " Value=" + entry.getValue());
        }

        // Retrieve AvatarsTeams Map
        avatarsTeamsMap = AvatarManager.getInstance().getAvatarTeamMap(battleEngine);

        // Debug Avatar - Team associations
        for (final Iterator entryIterator = avatarsTeamsMap.entrySet().iterator(); entryIterator.hasNext();) {
            final Map.Entry entry = (Map.Entry) entryIterator.next();
            Logger.getInstance().debug("Avatar-Team Key=" + entry.getKey() + " Value=" + entry.getValue());
        }

        //Retrieve pointOfInterests List
        pointsOfInterest = BattleEngineManager.getInstance().getPointsOfInterest(battleEngine.getId());

        // Debug pointsOfInterest
        final Iterator iter = pointsOfInterest.iterator();
        while (iter.hasNext()) {
            Logger.getInstance().debug("Point of interest = " + iter.next());
        }

        //Initialize Engine's RMI implementation
        final FGEngineRMIImpl engine = new FGEngineRMIImpl();
        engine.setEngineApp(this);

        //Register RMI Interface
        GenericRMIServer.getInstance().registerInterface(FGEngineInterface.RMI_NAME, engine);

        // create the Coordinator
        coordinator = new Coordinator(guardiansList);

        //EventConsumer send Events from Station to coordinator.
        EventConsumer.getInstance().addObserver(coordinator);

        // Finalize DB transaction
        trans.commit();

        try {
            // EventDBWriter should observe Coordinator and store received events to database
            final EventDBWriter eObs = new EventDBWriter(getBattleEngine(), getGuardiansAvatarsMap());
            coordinator.addObserver(eObs);

            // start RMI
            //eu.funinnumbers.engine.startRMI();
        } catch (Exception ex) {
            Logger.getInstance().debug("Failed to start Battle Engine", ex);
        }

    }

    /**
     * Starts the Engine application.
     */
    public final void startApp() { //NOPMD
        // nothing further to do
    }

    /**
     * Starts the Stations of this game.
     */
    public final void startStations() {

        // Iterate through the stations
        for (Station station1 : stationsMap.values()) {
            // Execute Station app for this IP
            Logger.getInstance().debug(station1.getIpAddr());
        }
    }

    /**
     * Adds an observer to the set of observers for this object, provided
     * that it is not the same as some observer already in the set.
     * The order in which notifications will be delivered to multiple
     * observers is not specified. See the class comment.
     *
     * @param obj an observer to be added.
     */
    public final void addEventObserver(final Observer obj) {
        coordinator.addObserver(obj);
    }

    /**
     * Access the Event Coordinator.
     *
     * @return the current instance of the Event Coordinator
     */
    public final Coordinator getCoordinator() {
        return coordinator;
    }

    /**
     * Access the Engine Information as stored in DB.
     *
     * @return the current Engine information
     */
    public final BattleEngine getBattleEngine() {
        return battleEngine;
    }

    /**
     * Access the Station Information associated with the Engine.
     *
     * @param stationIp the IP of the Station to look-up
     * @return the Station information
     */
    public final Station getStationByIP(final String stationIp) {
        return stationsMap.get(stationIp);
    }

    /**
     * Access the Collection of Guardians associated with the Engine.
     *
     * @return the Collection of Guardians
     */
    public final Collection<Guardian> getGuardians() {
        return guardiansList;
    }

    /**
     * Access the Map containing the associated guardians and avatars.
     *
     * @return the Map containing the associated guardians and avatars
     */
    public final Map getGuardiansAvatarsMap() {
        return guardiansAvatarsMap;
    }

    /**
     * Access the Map containing the associated avatars and teams.
     *
     * @return the Map containing the associated avatars and teams
     */
    public final Map getAvatarsTeamsMap() {
        return avatarsTeamsMap;
    }

    /**
     * Access the List with the Points Of Interest.
     *
     * @return The List with the Points of Interest
     */
    public List<Integer> getPointsOfInterest() {
        return pointsOfInterest;
    }

    /**
     * Get the path of the build.XML file used to compile and deploy the code for the guardians.
     *
     * @return the path of the build.xml file
     */
    protected abstract String getBuildXMLpath();

    /**
     * Sets the properties of Hibernate that define the database/table connection settings.
     */
    protected abstract void configureHibernate();


}
