package eu.funinnumbers.station;

import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.Station;

import eu.funinnumbers.engine.rmi.FGEngineInterface;
import eu.funinnumbers.station.communication.EventForwarder;
import eu.funinnumbers.station.communication.GuardianInitializer;
import eu.funinnumbers.station.communication.GuardianObserver;
import eu.funinnumbers.station.communication.dtservice.DTSManager;
import eu.funinnumbers.station.communication.dtservice.FGEventProcessor;
import eu.funinnumbers.station.rmi.StationRMIImpl;
import eu.funinnumbers.util.Logger;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 * First Generation Station application.
 * <p/>
 * It is responsible for initializing the following:
 * <p/>
 * 1. <br>RMI
 * 2. <br>Logger
 * 3. <br>EventForwarder
 * 4. <br>EventObserver
 * 5. <br>GuardianObserver
 * 6. <br>GuardianInitializer
 *
 * @author marlog
 */
public class FGStationApp extends AbstractStationApp {

    /**
     * Station Information.
     */
    private final Station station;

    /**
     * Observes state of EchoProtocol.
     */
    private final GuardianObserver echoProtObserver;

    /**
     * Remote Engine Interface.
     */
    private final FGEngineInterface engine;

    /**
     * The Guardians on the BattleEngine.
     */
    private final Collection<Guardian> guardians;

    /**
     * The HashMap containing the association between guardians and avatars.
     */
    private final Map guardiansAvatarsMap; //NOPMD

    /**
     * The HashMap containing the association between avatars and teams.
     */
    private final Map avatarsTeamsMap;

    /**
     * The List containing the pointsOfInterest.
     */
    private final List pointsOfInterest;

    /**
     * Creates a new instance of Station.
     * First register to battle Engine.
     * Then register the battleStation to the local RMI registry.
     * Finaly bind with Coordinator.
     *
     * @throws RemoteException       if unable to lookup RMI interface
     * @throws MalformedURLException if eu.funinnumbers.engine's IP address is not correct
     * @throws NotBoundException     cannot bind to RMI interface
     */
    public FGStationApp()
            throws RemoteException, MalformedURLException, NotBoundException {
        super();

        // Lookup Engine RMI Interface
        Logger.getInstance().debug("Going to bind with Engine @ " + getEngineIP());
        engine = (FGEngineInterface) Naming.lookup("rmi://" + getEngineIP() + "/" + FGEngineInterface.RMI_NAME);

        // Register Station to EngineInterface and retrieve Station Information
        station = engine.registerStation(getMyIP(), new StationRMIImpl());

        Logger.getInstance().debug("Station ID=" + station.getStationId()
                + ", LEDID=" + station.getLEDId() + ", Name=" + station.getName());
        Logger.getInstance().debug("BattleEngine ID=" + station.getBattleEngine().getId()
                + ", Name=" + station.getBattleEngine().getName());

        // Retrieve the collection of Guardians from the EngineInterface
        guardians = engine.retrieveGuardians();

        // Retrieve the guardiansAvatars HashMap from the Engine Interface
        guardiansAvatarsMap = engine.retrieveGuardiansAvatarsMap();

        // Retrieve the guardiansAvatars HashMap from the Engine Interface
        avatarsTeamsMap = engine.retrieveAvatarsTeamsMap();

        //Retrieve the pointsOfInterest List from Engine Interface
        pointsOfInterest = engine.getPointsOfInterest();

        //Debug Points Of Interst List
        final Iterator iter = pointsOfInterest.iterator();
        while (iter.hasNext()) {
            Logger.getInstance().debug("Point Of Interest with id = " + iter.next());
        }

        // Debug Avatar - Team associations
        for (final Iterator entryIterator = avatarsTeamsMap.entrySet().iterator(); entryIterator.hasNext();) {
            final Map.Entry entry = (Map.Entry) entryIterator.next();
            Logger.getInstance().debug("Avatar-Team Key=" + entry.getKey()
                    + " Value=" + entry.getValue());
        }

        // Start the Event Forwarder
        EventForwarder.getInstance().setEngineInterface(engine);

        // Start the FGEventProcessor Thread
        DTSManager.getInstance().addObserver(new FGEventProcessor(guardians));
        Logger.getInstance().debug("FGEventProcessor registered");

        // Start the GuardianObserver
        echoProtObserver = new GuardianObserver(station);
        Logger.getInstance().debug("GuardianObserver thread started successfully");

        // Start the GuardianInitializer
        new GuardianInitializer(station, this);
        Logger.getInstance().debug("The GuardianInitializer started successfully");

        //Logger.getInstance().debug("GuardianIntializer has been bypassed");
        // Add GuardianObserver to GuardianInitializer observers
        //ginit.addObserver(echoProtObserver);
    }


    /**
     * Returns the connected guardians.
     *
     * @return a vector with all the guardians connected to this eu.funinnumbers.station
     */
    public final Vector getConnGuardians() {
        return echoProtObserver.getConnGuardians();

    }

    /**
     * Access the information for this eu.funinnumbers.station.
     *
     * @return the DB object related to this eu.funinnumbers.station
     */
    public final Station getStation() {
        return station;
    }

    /**
     * Access the EngineInterface provided by remote Engine over RMI.
     *
     * @return the RMI Interface of the Engine
     */
    public final FGEngineInterface getEngine() {
        return engine;
    }

    /**
     * Access the Collection of Guardians associated with the Engine.
     *
     * @return the Collection of Guardians
     */
    public final Collection getGuardians() {
        return guardians;
    }

    /**
     * Access the Map containing the association between guardians and avatars.
     *
     * @return the Map containing the association between avatars ans teams
     */
    public final Map getGuardiansAvatarsMap() {
        return guardiansAvatarsMap;
    }

    /**
     * Access the Map containing the association between avatars and teams.
     *
     * @return the Map containing the association between avatars ans teams
     */
    public final Map getAvatarsTeamsMap() {
        return avatarsTeamsMap;
    }

    /**
     * Returnf Points Of Interests.
     *
     * @return a List with the Points Of Interest
     */
    public List getPointsOfInterest() {
        return pointsOfInterest;
    }

    /**
     * Main function for invoking Station as a standalone application.
     *
     * @param args the command line arguments
     * @throws RemoteException       if unable to lookup RMI interface
     * @throws MalformedURLException if eu.funinnumbers.engine's IP address is not correct
     * @throws NotBoundException     cannot bind to RMI interface
     */
    public static void main(final String[] args)
            throws RemoteException, MalformedURLException, NotBoundException {


        // Start FGStationApp
        new FGStationApp();
    }

    /**
     * Specific game application code.
     */
    public void stationApp() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
