package eu.funinnumbers.engine.rmi;

import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.Station;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.station.rmi.StationInterface;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * RMI Interface of First Generation Engine used by the (First Generation) Stations.
 */
public interface FGEngineInterface extends BaseEngineInterface {

    /**
     * The name of the RMI class.
     */
    String RMI_NAME = "FGEngine";

    /**
     * This is the remote method which is called by the Stations to retrieve the Collection containing.
     * all guardians
     *
     * @return the Collection containing all Guardians
     * @throws RemoteException if RMI was unable to invoke method
     */
    Collection<Guardian> retrieveGuardians() throws RemoteException;

    /**
     * This is the remote method which is called by the Stations to retrieve the Map containing.
     * the association between guardians and avatars
     *
     * @return the Map containing the association between guardians and avatars
     * @throws RemoteException if RMI was unable to invoke method
     */
    Map retrieveGuardiansAvatarsMap() throws RemoteException;

    /**
     * This is the remote method which is called by the Stations to retrieve the Map containing.
     * the association between avatars and teams
     *
     * @return the Map containing the association between avatars ans teams
     * @throws RemoteException if RMI was unable to invoke method
     */
    Map retrieveAvatarsTeamsMap() throws RemoteException;

    /**
     * This is the remote method called by the Stations to modify the InitPhase property of the Guardian objects
     * so that the Engine can monitor the progress of the Initialization process.
     *
     * @param guardian the object of the Guardian to update
     * @throws RemoteException if RMI was unable to invoke method
     */
    void updateGuardianInfo(Guardian guardian) throws RemoteException;

    /**
     * This is the remote method called by the Stations to retrieve all the points of interest from the Engine.
     *
     * @return A list with the Points of Interst
     * @throws RemoteException if RMI was unable to invoke method
     */
    List getPointsOfInterest() throws RemoteException;

    /**
     * This is the remote method which is called by the Stations to retrieve their initial information.
     *
     * @param stationIP the IP address of the Station
     * @param stationIF the remote object implementing the StationInterface
     * @return the Station information
     * @throws RemoteException if RMI was unable to invoke method
     */
    Station registerStation(String stationIP, StationInterface stationIF) throws RemoteException;


    /**
     * This is the remote method which is called by the Stations to send new events to the Engine.
     *
     * @param event The new Event
     * @throws RemoteException if RMI was unable to invoke method
     */
    void addEvent(Event event) throws RemoteException;

}
