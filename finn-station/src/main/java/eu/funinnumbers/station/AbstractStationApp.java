package eu.funinnumbers.station;

import eu.funinnumbers.station.communication.dtservice.DTSManager;
import eu.funinnumbers.util.Logger;

/**
 * Abstract Station Application Class.
 */
public abstract class AbstractStationApp {

    /**
     * Station's IP address.
     */
    private String myIP;

    /**
     * Engine's IP address.
     */
    private String engineIP;

    /**
     * Default Constructor.
     */
    public AbstractStationApp() {

        //Get Engine's Ip address
        setEngineIP(System.getProperty("engine.IP")); //NOPMD

        //Print Engine's ip address
        Logger.getInstance().debug("Engine: " + getEngineIP()); //NOPMD

        //Get Station's IP address -- eu.funinnumbers.station.IP
        setMyIP(System.getProperty("java.rmi.server.hostname")); //NOPMD

        //Print Stations's ip address
        Logger.getInstance().debug("Station: " + getMyIP()); //NOPMD

        /*// Reduce output power to -10
        Spot.getInstance().getRadioPolicyManager().setOutputPower(30);*/

        // Start the DTSManager Thread
        /*DTSManager.getInstance();
        Logger.getInstance().debug("DTSManager started successfully");*/

        //Specific Game Station Application Code
        stationApp();

        Logger.getInstance().debug("Station application started");
    }

    /**
     * Specific game application code.
     */
    public abstract void stationApp();

    /**
     * Returns Station's IP address.
     *
     * @return String with Station's ip address
     */
    public String getMyIP() {
        return myIP;
    }

    /**
     * Set Station's IP address.
     *
     * @param ipAddress String with the IP address
     */
    public void setMyIP(final String ipAddress) {
        this.myIP = ipAddress;
    }

    /**
     * Returns Engine IP address.
     *
     * @return String with Engine's IP
     */
    public String getEngineIP() {
        return engineIP;
    }

    /**
     * Set Engine's IP address.
     *
     * @param engineIpAd String with the IP
     */
    public void setEngineIP(final String engineIpAd) {
        this.engineIP = engineIpAd;
    }
}
