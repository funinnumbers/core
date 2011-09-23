package eu.funinnumbers.engine.rmi;


import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.db.model.localization.Point;
import eu.funinnumbers.station.rmi.StationInterface;

import java.rmi.RemoteException;

/**
 * RMI Interface of Second Generation Engine used by the (Second Generation) Stations.
 */
public interface SGEngineInterface extends BaseEngineInterface {

    /**
     * The name of the RMI class.
     */
    String RMI_NAME = "SGEngine";

    /**
     * This is the remote method which is called by the Stations to retrieve their initial information.
     *
     * @param stationIP the IP address of the Station
     * @param stationIF the remote object implementing the StationInterface
     * @throws RemoteException if RMI was unable to invoke method
     */
    void registerStation(String stationIP, StationInterface stationIF) throws RemoteException;

    /**
     * This is the remote method which is called by the Stations to send new events to the Engine.
     *
     * @param event The new Event
     * @throws RemoteException if RMI was unable to invoke method
     */
    void addEvent(Event event) throws RemoteException;

    /**
     * Logs both the Link quality indicator and rssi for the connection between eu.funinnumbers.station and eu.funinnumbers.guardian.
     *
     * @param stationMac  the mac address of the eu.funinnumbers.station
     * @param guardianMac the mac address of the eu.funinnumbers.guardian
     * @param lqi         the link quality indicator
     * @param rssi        rss indicator
     * @throws RemoteException if RMI was unable to invoke method
     */
    void logLocalizationValues(final String stationMac, final String guardianMac, final Integer rssi, final Integer lqi, final int param) throws RemoteException;


    /**
     * Associates the mac address of a eu.funinnumbers.station with coordinates.
     *
     * @param mac    the mac address of the eu.funinnumbers.station.
     * @param point  the corresponding coordinates.
     */
    void setStationCoordinates(final String mac, final Point point) throws RemoteException;


}

