package eu.funinnumbers.station.communication.localization;

import eu.funinnumbers.engine.rmi.SGEngineInterface;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.spot.util.Queue;


/**
 * Responsible for RSSI and LQI logging.
 */
public final class LocLogger extends Observable {

    /**
     * The unique LocLogger instance.
     */
    static LocLogger thisLocLogger = null;

    /**
     * Storing RSSI for each MAC address.
     */
    private final HashMap<String, ArrayList<Integer>> rssiLogMap;

    /**
     * Storing LQI for each MAC address.
     */
    private final HashMap<String, ArrayList<Integer>> lqiLogMap;

    /**
     * Engine RMI interface.
     */
    private SGEngineInterface engine;

    /**
     * Process the LocLogData from the LocLogDataQueue.
     */
    private LocLogDataProcessor dataProcessor;
    /**
     * The Data Queue.
     */
    private final Queue dataQueue;

    /**
     * Default constructor.
     */
    private LocLogger() {
        lqiLogMap = new HashMap<String, ArrayList<Integer>>();
        rssiLogMap = new HashMap<String, ArrayList<Integer>>();
        dataQueue = new Queue();
    }

    /**
     * Public access to LocLogger.
     *
     * @return LocLogger instance.
     */
    public static synchronized LocLogger getInstance() {
        if (thisLocLogger == null) {
            thisLocLogger = new LocLogger();
        }
        return thisLocLogger;
    }


    /**
     * Logs the link quality indicator and received signal strength of a mac address.
     *
     * @param rssi rss indicator as integer
     * @param lqi  lq indicator as integer
     * @param mac  mac address as String
     */
    public void logThis(final int rssi, final int lqi, final String mac) throws IOException {
        if (!rssiLogMap.containsKey(mac)) {
            //eu.funinnumbers.engine.createFiles(System.getProperty("IEEE_ADDRESS"),mac);
            rssiLogMap.put(mac, new ArrayList<Integer>());
            lqiLogMap.put(mac, new ArrayList<Integer>());

        }
        rssiLogMap.get(mac).add(rssi);
        lqiLogMap.get(mac).add(lqi);
        Logger.getInstance().debug(mac + " : " + rssi + " - " + lqi);
        final LocLogData locLogData = new LocLogData(rssi, lqi, mac);
        dataQueue.put(locLogData);
    }


    /**
     * Logs the link quality indicator and received signal strength of a mac address.
     * Use this method to if you want to log to file and take special parameters into account.
     *
     * @param rssi  rss indicator as integer
     * @param lqi   lq indicator as integer
     * @param mac   mac address as String
     * @param param special parameter integer
     */
    public void logThis(final int rssi, final int lqi, final String mac, final int param) throws IOException {

        Logger.getInstance().debug(mac + " : " + rssi + " - " + lqi);
        final LocLogData locLogData = new LocLogData(rssi, lqi, mac, param);
        dataQueue.put(locLogData);
    }

    /**
     * Logs a synchronization signal from the device.
     *
     * @param mac the MAC address of the device.
     */
    public void logSynch(final String mac) {

        Logger.getInstance().debug(mac + " SYNCH");

        final LocLogData locLogData = new LocLogData(mac);
        dataQueue.put(locLogData);
    }

    /**
     * Initiates the RMI interaface.
     *
     * @param engine the EngineRMI interface
     */
    public void setEngine(final SGEngineInterface engine) {
        this.engine = engine;
        dataProcessor = new LocLogDataProcessor(engine, dataQueue);
        dataProcessor.start();
    }

}