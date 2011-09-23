package eu.funinnumbers.engine.rmi;

import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.db.model.localization.Point;
import eu.funinnumbers.engine.AbstractEngineApp;
import eu.funinnumbers.engine.localization.LocalizationData;
import eu.funinnumbers.engine.playertracking.PlayersTracker;
import eu.funinnumbers.station.rmi.StationInterface;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.eventconsumer.EventConsumer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implementation of First Generation Engine RMI Interface.
 */
public class SGEngineRMIImpl extends UnicastRemoteObject implements SGEngineInterface {

    /**
     * Version for Serialization.
     */
    static final long serialVersionUID = 42L;

    /**
     * Instance of the Engine application.
     */
    private AbstractEngineApp engineApp;

    /**
     * Default Constructor.
     *
     * @throws RemoteException if RMI unable to connect
     */
    public SGEngineRMIImpl() throws RemoteException {
        super();
    }

    /**
     * Set a reference to the eu.funinnumbers.engine application object.
     *
     * @param engineAp the eu.funinnumbers.engine application object
     */
    public void setEngineApp(final AbstractEngineApp engineAp) {
        this.engineApp = engineAp;
    }

    /**
     * Get a reference from the eu.funinnumbers.engine application object.
     *
     * @return Engine application object.
     */
    public final AbstractEngineApp getEngineApp() {
        return engineApp;
    }

    /**
     * This is the remote method which is called by the Stations to retrieve their initial information.
     *
     * @param stationIP the IP address of the Station
     * @param stationIF the remote object implementing the StationInterface
     * @throws RemoteException if RMI was unable to invoke method
     */
    public void registerStation(final String stationIP, final StationInterface stationIF) throws RemoteException {
        stationIF.completeRegistration();
        engineApp.registerStationInterface(stationIP, stationIF);
    }

    /**
     * This is the remote method which is called by the Stations to send new events to the Engine.
     *
     * @param event The new Event
     * @throws RemoteException if RMI was unable to invoke method
     */
    public void addEvent(final Event event) throws RemoteException {
        Logger.getInstance().debug("New Event received from Station: " + event.getDescription());
        EventConsumer.getInstance().addEvent(event);

        PlayersTracker.getInstance().trackMac(event.getSourceAddress());
        
        // Forward the event to Hyper Engine, if exists.
        if (engineApp.getHyperEngineInterface() != null) {
            try {
                Logger.getInstance().debug("\t\tSend to Hyper " + event.getType() + " : " + event.getDescription());
                engineApp.getHyperEngineInterface().addEvent(event);
            } catch (Exception ex) {
                Logger.getInstance().debug("No more eu.funinnumbers.hyperengine! " + ex);
                //engineApp.setHyperEngineInterface(null);
            }

        }


    }


    /**
     * Logs both the Link quality indicator and rssi for the connection between eu.funinnumbers.station and eu.funinnumbers.guardian.
     *
     * @param stationMac  the mac address of the eu.funinnumbers.station
     * @param guardianMac the mac address of the eu.funinnumbers.guardian
     * @param lqi         the link quality indicator
     * @param rssi        rss indicator
     * @throws java.io.IOException when something goes wrong
     */
    public void logLocalizationValues(final String stationMac, final String guardianMac, final Integer rssi, final Integer lqi, final int param) throws RemoteException {
        LocalizationData.getInstance().logValues(stationMac, guardianMac, rssi, lqi);
    }


    /**
     * Associates the mac address of a eu.funinnumbers.station with a X coordinate.
     *
     * @param mac   the mac address of the eu.funinnumbers.station.
     * @param point the corresponding X coordinate.
     */
    public void setStationCoordinates(final String mac, final Point point) throws RemoteException {
        // Station doesn't know it's coordinates
        if (point == null) {
            if (engineApp.getStationCoordinates().containsKey(mac)) {
                Logger.getInstance().debug(" Coordinates found for " + mac);
                LocalizationData.getInstance().setStationCoordinates(
                        mac, engineApp.getStationCoordinates().get(mac));

                // Unknown eu.funinnumbers.station coordinates
            } else {
                Logger.getInstance().debug(" Cannot find coordinates for " + mac);
                LocalizationData.getInstance().setStationCoordinates(mac, new Point());
            }

            // Station knows it's coordinates
        } else {
            LocalizationData.getInstance().setStationCoordinates(mac, point);
        }
    }

    

}
