package eu.funinnumbers.engine;

import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.db.model.localization.Point;
import eu.funinnumbers.engine.util.GenericRMIServer;
import eu.funinnumbers.hyperengine.rmi.HyperEngineInterface;
import eu.funinnumbers.station.rmi.StationInterface;
import eu.funinnumbers.util.Logger;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract Engine Application.
 */
public abstract class AbstractEngineApp {

    /**
     * The IP address of the Engine.
     */
    private String myIP;

    /**
     * StationInterfaces that are connected to this Engine.
     */
    protected final Map<String, StationInterface> stationInterfaces;

    /**
     * Mac addresses of staions associated with coordinates.
     */
    protected Map<String, Point> stationCoordinates = new HashMap<String, Point>();

    protected HyperEngineInterface hyperEngine = null;

    /**
     * Default constructor.
     */
    public AbstractEngineApp() {
        // Retrieve Engine IP
        setMyIP(System.getProperty("java.rmi.server.hostname"));
        Logger.getInstance().debug("Engine Address: " + getMyIP());

        // Initialize arraylist
        stationInterfaces = new HashMap<String, StationInterface>();

        // Invoke GenericRMIServer
        GenericRMIServer.getInstance();
        try {
            startApp();
        } catch (RemoteException e) {
            Logger.getInstance().debug("Exception starting eu.funinnumbers.engine application", e);
        }

        Logger.getInstance().debug("Engine application started");
    }

    /**
     * Starts the Engine application.
     *
     * @throws java.rmi.RemoteException if an RMI error occured
     */
    public abstract void startApp() throws RemoteException;

    /**
     * Get The IP address of the Engine.
     *
     * @return the IP address as string
     */
    public final String getMyIP() {
        return myIP;
    }

    /**
     * Set The IP address of the Engine.
     *
     * @param myIp the address as String
     */
    public final void setMyIP(final String myIp) {
        this.myIP = myIp;
    }

    /**
     * Register a new StationInterface by IP address.
     *
     * @param stationIP the address as string
     * @param stationIF the remote object offering the eu.funinnumbers.station implementation
     * @throws java.rmi.RemoteException if an RMI error occured
     */
    public void registerStationInterface(final String stationIP,
                                         final StationInterface stationIF) throws RemoteException {
        stationInterfaces.put(stationIP, stationIF);
        Logger.getInstance().debug("New Station registered : " + stationIP);
    }

    /**
     * Access the StationInterfaces of the Stations registered to this eu.funinnumbers.engine.
     *
     * @param stationIP the address as string
     * @return the StationInterface matching this IP otherwise null
     */
    public StationInterface getStationInterface(final String stationIP) {
        return stationInterfaces.get(stationIP);
    }


    /**
     * Access the StationInterfaces of all registered Stations.
     *
     * @return the StationInterfaces matching
     */
    public Collection<StationInterface> getStationInterface() {
        return stationInterfaces.values();
    }

    /**
     * Access the HyperEngineInterface.
     *
     * @return the Hyperngine Interface
     */
    public HyperEngineInterface getHyperEngineInterface() {
        return this.hyperEngine;
    }

    /**
     * Sets The HyperEngine Interface.
     *
     * @param hyperEngine the HyperEngineInterface object
     */
    public void setHyperEngineInterface(final HyperEngineInterface hyperEngine) {
        this.hyperEngine = hyperEngine;
    }

    /**
     * Access to the Mac addresses of stations and their Coordinates.
     *
     * @return the Map containing the coordinates.
     */
    public Map<String, Point> getStationCoordinates() {
        return stationCoordinates;
    }

    /**
     * Search and initialize a connection with the eu.funinnumbers.hyperengine.
     */
    protected void searchHyperEngine() {
        if ((System.getProperty("eu.funinnumbers.hyperengine.IP") != null) && (!System.getProperty("eu.funinnumbers.hyperengine.IP").startsWith("${"))) {
            Logger.getInstance().debug("HyperEngine IP: " + System.getProperty("eu.funinnumbers.hyperengine.IP"));
            try {
                final HyperEngineInterface registeredHyperEngine = (HyperEngineInterface) Naming.lookup("rmi://" + System.getProperty("eu.funinnumbers.hyperengine.IP") + "/" + HyperEngineInterface.RMI_NAME);
                this.hyperEngine = registeredHyperEngine;
                Logger.getInstance().debug("HyperEngine on " + System.getProperty("eu.funinnumbers.hyperengine.IP"));

            } catch (Exception e) {
                Logger.getInstance().debug("Problem while connecting to HyperEngine" + e);
                this.hyperEngine = null;
            }
        } else {
            Logger.getInstance().debug("No HyperEngine");
            this.hyperEngine = null;
        }
    }

    /**
     * Send Event to HyperEngine.
     *
     * @param event the event to be send
     */
    public void hyperEngineEvent(final Event event) {
        if (hyperEngine != null) {
            try {
                hyperEngine.addEvent(event);
            } catch (RemoteException e) {
                Logger.getInstance().debug("Unable to send event to HyperEngine", e);
            }

        }

    }
}
