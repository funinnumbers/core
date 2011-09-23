package eu.funinnumbers.engine.rmi;

import eu.funinnumbers.db.managers.GuardianManager;
import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.Station;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.db.util.HibernateUtil;
import eu.funinnumbers.engine.AbstractEngineApp;
import eu.funinnumbers.engine.FGEngineApp;
import org.hibernate.Transaction;
import eu.funinnumbers.station.rmi.StationInterface;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.eventconsumer.EventConsumer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Implementation of First Generation Engine RMI Interface.
 */
public class FGEngineRMIImpl extends UnicastRemoteObject implements FGEngineInterface {

    /**
     * Version for Serialization.
     */
    static final long serialVersionUID = 42L;

    /**
     * Instance of the Engine application.
     */
    private FGEngineApp engineApp;


    /**
     * Default Constructor.
     *
     * @throws RemoteException if RMI unable to connect
     */
    public FGEngineRMIImpl() throws RemoteException {
        super();
    }

    /**
     * Set a reference to the eu.funinnumbers.engine application object.
     *
     * @param engineAp the eu.funinnumbers.engine application object
     */
    public void setEngineApp(final AbstractEngineApp engineAp) {
        this.engineApp = (FGEngineApp) engineAp;
    }

    /**
     * This is the remote method which is called by the Stations to retrieve their initial information.
     *
     * @param stationIP the IP address of the Station
     * @param stationIF the remote object implementing the StationInterface
     * @return the Station information
     * @throws RemoteException if RMI was unable to invoke method
     */
    @SuppressWarnings("unchecked")
    public final Station registerStation(final String stationIP,
                                         final StationInterface stationIF) throws RemoteException {
        final Station station = engineApp.getStationByIP(stationIP);
        Logger.getInstance().debug("Sending info for Station IP=" + stationIP + ", ID=" + station.getStationId());

        // Lookup Station RMI Interface
        engineApp.registerStationInterface(stationIP, stationIF);

        return station;
    }

    /**
     * This is the remote method which is called by the Stations to retrieve the list of guardians
     * participating in the game.
     *
     * @return the List of guardians
     * @throws RemoteException if RMI was unable to invoke method
     */
    public Collection<Guardian> retrieveGuardians() throws RemoteException {
        return engineApp.getGuardians();
    }

    /**
     * This is the remote method which is called by the Stations to retrieve the HashMap containing
     * the association between guardians and avatars.
     *
     * @return the HashMap containing the association between guardians and avatars
     * @throws RemoteException if RMI was unable to invoke method
     */
    public Map retrieveGuardiansAvatarsMap() throws RemoteException {
        return engineApp.getGuardiansAvatarsMap();
    }

    /**
     * This is the remote method which is called by the Stations to retrieve the HashMap containing
     * the association between avatars and teams.
     *
     * @return the HashMap containing the association between avatars ans teams
     * @throws RemoteException if RMI was unable to invoke method
     */
    public Map retrieveAvatarsTeamsMap() throws RemoteException {
        return engineApp.getAvatarsTeamsMap();
    }

    /**
     * This is the remote method called by the Stations to modify the InitPhase property of the Guardian objects
     * so that the Engine can monitor the progress of the Initialization process.
     *
     * @param guardian the object of the Guardian to update
     * @throws RemoteException if RMI was unable to invoke method
     */
    public void updateGuardianInfo(final Guardian guardian) throws RemoteException {
        // Start new transaction
        final Transaction transaction = HibernateUtil.getInstance().getSession().beginTransaction();

        // Try to save
        GuardianManager.getInstance().update(guardian);

        // Commit Hibernate Transaction
        transaction.commit();
    }

    /**
     * This is the remote method called by the Stations to retrieve all the points of interest from the Engine.
     *
     * @return a list with points of interests related to this eu.funinnumbers.station
     * @throws RemoteException if RMI was unable to invoke method
     */
    public List getPointsOfInterest() throws RemoteException {
        return engineApp.getPointsOfInterest();
    }


    /**
     * This is the remote method which is called by the BattleStations receive new events.
     *
     * @param event The new Event
     * @throws RemoteException in case of an RMI error
     */
    public final void addEvent(final Event event) throws RemoteException {
        EventConsumer.getInstance().addEvent(event);
        Logger.getInstance().debug("New event : " + event.getDebugInfo());
    }


}
