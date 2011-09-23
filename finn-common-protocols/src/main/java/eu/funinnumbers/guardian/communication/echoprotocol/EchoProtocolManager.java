/**
 *  Manages the Echo Protocol. Starts the execution of Receiver and Broadcaster threads.
 *
 * The EchoProtocol Manager follows the Singleton Design Pattern.
 * Only one instance of the EchoProtocolManager class should be created.
 *
 *
 */

package eu.funinnumbers.guardian.communication.echoprotocol;


import com.sun.spot.peripheral.Spot;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.peripheral.radio.RadioFactory;
import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.guardian.util.HashMap;
import eu.funinnumbers.guardian.util.Map;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observable;

import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

/*logloc_STATION_ONLY
import eu.funinnumbers.station.communication.localization.LocLogger;
logloc_STATION_ONLY*/

/**
 * EchoProtocolManager is an Observable object. Objects implementing the Obsever interface
 * can be not ified when changes regarding the EchoProtocol have occured.
 */
public final class EchoProtocolManager extends Observable { //NOPMD

    /**
     * The default beacon interval.
     */
    public static final int DEFAULT_BEACON_INTERVAL = 500;

    /**
     * The EchoProtocol port.
     */
    public static final int ECHOPORT = 100;

    /**
     * Indicates full signal strength.
     */
    public static final int FULL_SIGNAL_STRENGTH = 20; //NOPMD

    /**
     * The length of the MAC address string.
     */
    public static final int MAC_LENGTH = 16;

    /**
     * Beacon interval.
     */
    private int beaconInterval;

    /**
     * Time after which a neighbour is considered disconnected.
     */
    private int deadTime;

    /**
     * Time after which a neighbour is considered disconnected.
     */
    private int initTime;

    /**
     * If this variable is set to true then all the observers are notified every
     * time a new echo beacon is received.
     */
    private boolean forceNotifyObservers;

    /**
     * The IEEE MAC address of this device.
     */
    private Long myAddress = new Long(RadioFactory.getRadioPolicyManager().getIEEEAddress());

    /**
     * A Vector of all neighboring devices.
     * These are bi-directional neighbours
     */
    private final HashMap neighbours;

    /**
     * A HashMap containing all the neigbouring devices.
     * At least one-way neighbours.
     */
    private final HashMap semiNeighbours;

    /**
     *
     */
    private boolean iAmStation = false;

    /**
     * If this is a eu.funinnumbers.guardian, this valiable holds the
     * Station that the eu.funinnumbers.guardian is connected to.
     */
    private Station myStation;

    /**
     * Unique instance of this class.
     */
    private static EchoProtocolManager thisecho;

    /**
     * The EchoProtocol Broadcaster Thread.
     */
    private final Broadcaster bcast = new Broadcaster();

    /**
     * The EchoProtocol Receiver Thread.
     */
    private final Receiver rcvr = new Receiver();

    /**
     * Beacon Proccessor Thread.
     */
    private final BeaconProccessor beacproc = new BeaconProccessor();

    /**
     * Creates the single instance of EchoProtocolManager.
     */
    private EchoProtocolManager() {

        // Init variables
        beaconInterval = DEFAULT_BEACON_INTERVAL;
        deadTime = 3 * beaconInterval;
        initTime = 4 * beaconInterval;
        forceNotifyObservers = false;

        // Setting Radio Signal to full power.
        //Spot.getInstance().getRadioPolicyManager().setOutputPower(FULL_SIGNAL_STRENGTH);

        neighbours = new HashMap(); // NOPMD
        semiNeighbours = new HashMap();
        myStation = new Station();
        myStation.setAddress(new Long(Broadcaster.TOKEN_NOSTATION));

        // Staring the Receiver Thread
        rcvr.start();

        // Starting the Broadcaster Thread
        bcast.start();

        // Starting the Beacon Proccessor Thread
        //beacproc.start();
    }

    /**
     * Is invoked each time access to the EchoProtocolManager instance is needed.
     *
     * @return the EchoProtocolManager instance
     */
    public static EchoProtocolManager getInstance() {
        synchronized (EchoProtocolManager.class) {
            // Check if an instance has already been created
            if (thisecho == null) {
                // Create a new instance if not
                /*debug msg*/
                Logger.getInstance().debug("EchoProtocol Created");
                thisecho = new EchoProtocolManager();
            }
        }
        // Return the EchoProtocolManager instance
        return thisecho;

    }

    /**
     * Sets the eu.funinnumbers.station to which the Guardian is connected.
     *
     * @param station the Station
     */
    public void setStation(final Station station) {
        this.myStation = station;
    }

    /**
     * Sets the proper  mode according to the device on which the protocol is running.
     *
     * @param isStation true if protocol is running on Battle Station
     *                  false if protocol is running on Guardian
     */
    public void setMode(final boolean isStation) {
        iAmStation = isStation;
        this.bcast.setMode(isStation);
        this.rcvr.setMode(isStation);
    }

    /**
     * Gets the mode according to the device on which the protocol is running.
     *
     * @return true if protocol is running on Battle Station,
     *         otherwise false if protocol is running on Guardian
     */
    public boolean isStation() {
        return iAmStation;

    }

    /**
     * Defines if this eu.funinnumbers.station is mobile or not.
     *
     * @param isMobileStation true if the eu.funinnumbers.station is mobile
     */
    public void setMobileStation(final boolean isMobileStation) {
        iAmStation = true;
        this.bcast.setMode(true);
        this.bcast.setMobileStation(isMobileStation);
        this.rcvr.setMode(true);
    }

    /**
     * Sets the Device MAC address.
     *
     * @param adr the Device MAC address
     */
    public void setAddress(final Long adr) {
        this.myAddress = adr;
    }

    /**
     * @return the Device MAC address.
     */
    public Long getMyAddress() {
        return this.myAddress;
    }

    /**
     * Loops through the neighbours Vector detecting dead neighbours.
     * Also decides if the connection to the eu.funinnumbers.station is alive
     */
    protected void cleanDeadNeighbours() { //NOPMD
        // Clean the one-way neighbours
        if (!semiNeighbours.isEmpty()) {
            cleanSemiNeighbours();
        }

        // There is/was some connection to one eu.funinnumbers.station
        if (getMyStation().isActive() || getMyStation().isMobileStation()) {
            // Check if connection with eu.funinnumbers.station is lost
            checkStationConnection();
        }

        // Check if at least one Guardian is stored
        if (!getNeighbours().isEmpty()) {
            // Cleanup neighbours that seem to be disconnected
            checkNeighboursConnection();
        }
    }

    /**
     * For each neighbour it checks the timestamp of the last beacon received.
     * If it is longer than DEADTIME it assumes that the connection with this neighbour is lost.
     * In such case it notifies all EchoProtocol observers.
     */
    private void checkNeighboursConnection() {
        //final Date now;
        final long nowLong;
        long aliveLong;

        // Create an Enumerator for the neighbours Vector
        Enumeration neigh;
        //now = new Date(); // NOPMD
        nowLong = System.currentTimeMillis(); // now.getTime();
        // Loop through the neighbours Vector
        for (neigh = neighbours.getEntries(); neigh.hasMoreElements();) {
            final Map.Entry entry = (Map.Entry) neigh.nextElement();
            final Guardian guardian = (Guardian) entry.getValue();

            aliveLong = guardian.getLastAlive().getTime();

            // Check if the neighbour is alive
            if (nowLong - aliveLong > deadTime) {
                Logger.getInstance().debug("\t " + guardian.getAddress()
                        + " seems to be dead. Last alive stamp: "
                        + aliveLong);

                // Remove dead neighbour from Vector
                neighbours.remove(guardian.getAddress());

                // Change n's alive status before notifying observers
                guardian.setAlive(false);

                /*logloc_STATION_ONLY
                LocLogger.getInstance().logSynch(eu.funinnumbers.guardian.getAddress());
                logloc_STATION_ONLY*/

                // Notify Observers
                this.setChanged();
                notifyObservers(guardian);
                //FinnLogger.getInstance().changedNeighborhood();
            }
        }
    }

    /**
     * Checks the timestamp of the last beacon received from the active Station.
     * If it is longer than DEADTIME it assumes that the connection with this eu.funinnumbers.station is lost.
     * In such case it notifies all EchoProtocol observers.
     */
    private void checkStationConnection() {
        final long stationAliveLong = this.myStation.getLastAlive().getTime();
        //final Date now = new Date();
        final long nowLong = System.currentTimeMillis(); //now.getTime();

        // Check if the connection is actually alive
        if (nowLong - stationAliveLong > deadTime) {
            Logger.getInstance().debug("\tConnection to the eu.funinnumbers.station lost");

            getMyStation().setAddress(new Long(Broadcaster.TOKEN_NOSTATION));
            getMyStation().setStationId(0);
            getMyStation().setLEDId(0);
            getMyStation().setLastAlive(new Date(0));
            getMyStation().setMobileStation(false);
            // Update beacon
            updateBcast();

            //Notify Observers
            this.setChanged();
            notifyObservers(myStation);
            //DTSManager.getInstance().update(this, myStation);
        }
    }

    /**
     * Clean semiNeighours hashmap.
     */
    public void cleanSemiNeighbours() {
        Enumeration semiEnum;

        semiEnum = semiNeighbours.getKeys();

        HashMap tmpHashMap;
        //final Date now = new Date();
        final long nowLong = System.currentTimeMillis(); // now.getTime();
        while (semiEnum.hasMoreElements()) {

            final Object nextEntry = semiEnum.nextElement();
            tmpHashMap = (HashMap) semiNeighbours.get((String) nextEntry);

            final String sLastTimestamp = (String) tmpHashMap.get("ts");
            final long lastTimestamp = Long.parseLong(sLastTimestamp);

            if (nowLong - lastTimestamp > deadTime) {
                // You are about to change semi-eu.funinnumbers.station status.
                // Keylength < 16
                // Observers should get notified
                if (((String) nextEntry).length() < MAC_LENGTH) {
                    getMyStation().setAddress(new Long(Broadcaster.TOKEN_NOSTATION)); //NOPMD

                    //TODO change status only when ALL stations have been removed
                    getMyStation().setSemiStation(false);
                    /* this.hasChanged();
                    notifyObservers(EchoProtocolManager.getInstance().getMyStation());
                   */
                    this.setChanged();
                    notifyObservers(getMyStation());
                    //FinnLogger.getInstance().changedNeighborhood();
                }

                semiNeighbours.remove(nextEntry);
            }
        }
    }

    /**
     * Either updates neighbours's timestamp
     * or requests neighbours addition to the neighbours Vector.
     *
     * @param neighbour the Guardian instance
     */
    public void updateNeighbourNONLITE(final Guardian neighbour) { //NOPMD
        // Get the semi-neighbours of this device
        final HashMap semisHash = (HashMap) semiNeighbours.get(neighbour.getAddress());

        // By default use MAC address to do the lookup
        Long key = myAddress;

        // If devices runs as eu.funinnumbers.station, ledID should be use for lookup
        if (iAmStation) {
            key = new Long(myStation.getLEDId()); //NOPMD
        }

        /*
         * Check if this neighbor is connected to some eu.funinnumbers.station
         */

        // A bi-directional neighbour
        if (semisHash.containsKey(key)) {

            // This is an already known neighbour
            if (neighbours.containsKey(neighbour.getAddress())) {
                //final Guardian thisGuardian = (Guardian) neighbours.get(neighbour.getAddress());
                // Update timestamp
                neighbour.setLastAlive();

                // Update the init phase of the device
                //thisGuardian.setInitPhase(neighbour.getInitPhase());

                // Update neighbour
                neighbours.put(neighbour.getAddress(), neighbour);

                // Notify observers if init phase of the neighbor guardians has changed
                final Guardian guardian = (Guardian) neighbours.get(neighbour.getAddress());
                if ((guardian.getInitPhase() != neighbour.getInitPhase()) || (forceNotifyObservers)) {
                    this.setChanged();
                    notifyObservers(neighbour);
                }

                // A new bi-directional neighbour!
                // Add her to the neighbours hashmap
            } else {
                Logger.getInstance().debug("\tNew Guardian found "
                        + neighbour.getAddress()
                        + " @ " + neighbour.getLastAlive()
                        + " phase " + neighbour.getInitPhase());
                //receiveLeds("newguardian");
                neighbours.put(neighbour.getAddress(), neighbour);
                neighbour.setAlive(true);
                this.setChanged();
                notifyObservers(neighbour);
                //FinnLogger.getInstance().changedNeighborhood();

            }

            // This device is not a bi-directional neighbour anymore.
            // Set neighbour to be inactive
        } else if (neighbours.containsKey(neighbour.getAddress())) {
            ((Guardian) neighbours.get(neighbour.getAddress())).setAlive(false);

            //Remove the inactive neighbour
            neighbours.remove(neighbour.getAddress());

            // Notify Observers
            this.setChanged();
            notifyObservers(neighbour);
            //FinnLogger.getInstance().changedNeighborhood();

        }
    }

    /**
     * Either updates neighbours's timestamp
     * or requests neighbours addition to the neighbours Vector.
     *
     * @param neighbour the Guardian instance
     */
    public void updateNeighbour(final Guardian neighbour) { //NOPMD


        // By default use MAC address to do the lookup
        Long key = myAddress;

        // If devices runs as eu.funinnumbers.station, ledID should be use for lookup
        if (iAmStation) {
            key = new Long(myStation.getLEDId()); //NOPMD
        }

        // This is an already known neighbour
        if (neighbours.containsKey(neighbour.getAddress())) {
            //final Guardian thisGuardian = (Guardian) neighbours.get(neighbour.getAddress());
            // Update timestamp
            neighbour.setLastAlive();

            // Update the init phase of the device
            //thisGuardian.setInitPhase(neighbour.getInitPhase());

            // Notify observers if init phase of the neighbor guardians has changed
            final Guardian guardian = (Guardian) neighbours.get(neighbour.getAddress());
            if ((guardian.getInitPhase() != neighbour.getInitPhase()) || (forceNotifyObservers)) {
                neighbour.setAlive(true);
                //Logger.getInstance().debug("\t\trestored UID " + guardian.getID());
                neighbour.setID(guardian.getID());
                //Logger.getInstance().debug("\t\tEchoProtocol: updating due to phase change");
                this.setChanged();
                notifyObservers(neighbour);
            }
            // Update neighbour
            neighbours.put(neighbour.getAddress(), neighbour);

            // A new bi-directional neighbour!
            // Add her to the neighbours hashmap
        } else {
            Logger.getInstance().debug("\tNew Guardian found "
                    + neighbour.getAddress()
                    + " @ " + neighbour.getLastAlive()
                    + " phase " + neighbour.getInitPhase());
            //receiveLeds("newguardian");
            neighbours.put(neighbour.getAddress(), neighbour);
            neighbour.setAlive(true);
            this.setChanged();
            notifyObservers(neighbour);
            //FinnLogger.getInstance().changedNeighborhood();

        }

        // This device is not a bi-directional neighbour anymore.
        // Set neighbour to be inactive
    }

    /**
     * Adds a eu.funinnumbers.guardian as new Guardian to the neighbours Vector.
     *
     * @param neighbour Guardian instance to be added
     */
    protected void addNeighbour(final Guardian neighbour) {
        neighbours.put(neighbour.getAddress(), neighbour);
    }

    /**
     * Either establishes a connection to the eu.funinnumbers.station
     * or updates stations timestamp if already connected to it.
     *
     * @param station the Station instance
     */
    public void updateStationNONLITE(final Station station) { //NOPMD
        /*
            TODO You cannot see a mobile eu.funinnumbers.station if you are already connected to a proper infrastructure eu.funinnumbers.station.
         */
        final HashMap semisHash = (HashMap) semiNeighbours.get(Integer.toString(station.getLEDId()));
        if (getMyStation().isActive() || getMyStation().isMobileStation()) {
            if (getMyStation().getAddress().equals(station.getAddress())
                    && semisHash.containsKey(myAddress)) {

                getMyStation().setLastAlive();

            }

            getMyStation().setLEDId(station.getLEDId());

            /**
             * TODO: REMOVE THE FOLLOWING LINES AFTER LITHOGRAFIO
             */
            station.setMobileStation(true);
            this.setChanged();
            notifyObservers(station);

            // It's bi-directional. Get connected!
        } else if (semisHash.containsKey(myAddress)) {

            // Connect to this eu.funinnumbers.station
            getMyStation().setLastAlive();
            getMyStation().setAddress(station.getAddress());
            getMyStation().setStationId(station.getStationId());
            getMyStation().setLEDId(station.getLEDId());
            getMyStation().setMobileStation(station.isMobileStation());

            Logger.getInstance().debug("\tI am now connected to " + getMyStation().getAddress());

            // Update beacon
            updateBcast();
            // TODO This might be a connection to a mobile eu.funinnumbers.station.Will be considered as disconnection though.
            // Notify listeners
            this.setChanged();
            notifyObservers(myStation);

        } /* else if (!myStation.isActive()
                && !semisHash.containsKey(myAddress)
                && !myStation.isSemiStation()) {
            //Logger.getInstance().debug(" If I increase the strength might get connected");

            //
            getMyStation().setSemiStation(true);

            // Notify listeners
            //this.setChanged();
            //notifyObservers(myStation);
        }*/

    }

    /**
     * Either establishes a connection to the eu.funinnumbers.station
     * or updates stations timestamp if already connected to it.
     *
     * @param station the Station instance
     */
    public void updateStation(final Station station) { //NOPMD
        /*
            TODO You cannot see a mobile eu.funinnumbers.station if you are already connected to a proper infrastructure eu.funinnumbers.station.
         */
        //final HashMap semisHash = (HashMap) semiNeighbours.get(Integer.toString(eu.funinnumbers.station.getLEDId()));
        if (getMyStation().isActive() || getMyStation().isMobileStation()) {

            if (getMyStation().getAddress().equals(station.getAddress())) {

                getMyStation().setLastAlive();
            }

            //getMyStation().setLEDId(eu.funinnumbers.station.getLEDId());
            station.setMobileStation(true);
            this.setChanged();
            notifyObservers(station);

            // It's bi-directional. Get connected!
        } else {

            // Connect to this eu.funinnumbers.station
            getMyStation().setLastAlive();
            getMyStation().setAddress(station.getAddress());
            getMyStation().setStationId(station.getStationId());
            getMyStation().setLEDId(station.getLEDId());
            getMyStation().setMobileStation(station.isMobileStation());

            Logger.getInstance().debug("\tI am now connected to " + getMyStation().getAddress());

            // Update beacon
            updateBcast();
            // TODO This might be a connection to a mobile eu.funinnumbers.station.Will be considered as disconnection though.
            // Notify listeners
            this.setChanged();
            notifyObservers(myStation);

        } /* else if (!myStation.isActive()
                && !semisHash.containsKey(myAddress)
                && !myStation.isSemiStation()) {
            //Logger.getInstance().debug(" If I increase the strength might get connected");

            //
            getMyStation().setSemiStation(true);

            // Notify listeners
            //this.setChanged();
            //notifyObservers(myStation);
        }*/

    }

    /**
     * @return the neighbours Vector
     */
    public Vector getNeighbours() {
        return neighbours.getValues();
    }

    /**
     * @param initPhase the the desired init phase
     * @return the active neighbours Vector
     */
    public Vector getActiveNeighbours(final int initPhase) {
        final HashMap.Entry[] table = neighbours.getTable();

        final Vector neighVec = new Vector(); //NOPMD
        final int totEntries = table.length;
        for (int index = 0; index < totEntries; index++) {
            HashMap.Entry entry = table[index];
            while (entry != null) {
                final Guardian guardian = (Guardian) entry.value;
                if (guardian.getInitPhase() < initPhase) {
                    neighVec.addElement(guardian);
                }
                entry = entry.next;
            }
        }
        return neighVec;
    }

    /**
     * @return the instance of myStation
     */
    public Station getMyStation() {
        synchronized (EchoProtocolManager.class) {
            return this.myStation;
        }
    }

    /**
     * Is invoked if the neighbour is a known neighbour.
     * It updates the neighbour's Station.
     *
     * @param neighbour neighbour device
     * @return true if neighbour's eu.funinnumbers.station has changed, otherwise false
     */
    public boolean changedStation(final Guardian neighbour) {

        if (neighbours.containsKey(neighbour.getAddress())) {
            final Guardian guardian = (Guardian) neighbours.get(neighbour.getAddress());
            if (neighbour.getAddress().equals(guardian.getAddress())
                    && !neighbour.getStation().equals(guardian.getStation())) {

                // Update neighbour's n eu.funinnumbers.station
                guardian.setStation(neighbour.getStation());
                Logger.getInstance().debug("updated the guardians eu.funinnumbers.station to "
                        + guardian.getStation()
                        + " == " + getMyAddress());
                return true;
            }
        }

        return false;
    }

    /**
     * @return the HashMap of the one-way neighbours
     */
    public HashMap getSemiNeighbours() {
        return semiNeighbours;
    }

    /**
     * Constructs the beacon message.
     */
    public void updateBcast() {
        bcast.constructBeaconMsg();
        Logger.getInstance().debug("New Beacon [" + bcast + "]");
    }

    /**
     * Checks if the neighbour is already a known neighbour
     * If so, updates the neighbour's alive stamp.
     *
     * @param neighbour neighbour device
     * @return true if neighbour is already known,otherwise false
     */
    public boolean isKnownNeighbour(final Guardian neighbour) {

        return neighbours.containsKey(neighbour.getAddress());

    }

    /**
     * @param address   the MAC address of the neighbouring device
     * @param semisHash a HashMap containing neighbouring devices of this device
     */
    public void setSemisOfSemi(final String address, final HashMap semisHash) {

        semiNeighbours.put(address, semisHash);


    }

    /**
     * Checks connectivity to a specific player.
     *
     * @param opponentAddress - the address of that player
     * @return true if the specific player is connected or false otherwise
     */
    public boolean checkConnectivity(final String opponentAddress) {
        return neighbours.containsKey(opponentAddress);
    }

    /**
     * @return the instance of the Beacon Proccessor.
     */
    public BeaconProccessor getBeaconProccessor() {
        return this.beacproc;
    }

    /**
     * Returns the neightbour Guardian with the specific address.
     *
     * @param address - the neighbour address
     * @return the neightbour Guardian with the specific address
     */
    public Guardian getNeighbour(final String address) {

        if (checkConnectivity(address)) {
            return (Guardian) neighbours.get(address);
        } else {
            return null;
        }

    }

    /**
     * Changes the transmission channel.
     *
     * @param channel an integer between 11 and 26
     */
    public void setChannel(final int channel) {
        Logger.getInstance().debug("Changing Transmission channel");
        final IRadioPolicyManager rpm = Spot.getInstance().getRadioPolicyManager();
        rpm.setChannelNumber(channel);

    }

    /**
     * Disables echo protocol transmissions.
     * USE IT WISELY
     */
    public void disable() {
        Logger.getInstance().debug("Disabling Broadcaster.");
        bcast.enabled = false;
    }

    /**
     * Enables echo protocol transmissions.
     * Actually restarts the Broadcaster thread.
     * USE IT WISELY
     */
    public void enable() {
        Logger.getInstance().debug("Enabling Broadcaster.");
        if (!bcast.enabled) {
            bcast.enabled = true;
        }
    }

    public void toggleBroadcaster() {
        if (bcast.enabled)
            disable();
        else
            enable();

    }

    /**
     * Sets the beacon interval of the echo protocol.
     *
     * @param beaconInterval - the beacon interval
     */
    public void setBeaconInterval(final int beaconInterval) {
        this.beaconInterval = beaconInterval;
        deadTime = 3 * beaconInterval;
        initTime = 4 * beaconInterval;
    }

    /**
     * Returns the beacon interval of the echo protocol.
     *
     * @return the beacon interval.
     */
    public int getBeaconInterval() {
        return beaconInterval;
    }

    /**
     * Sets the force notify neighbors option. If this option is set to true then
     * all the observers are notified every time a new echo beacon is received.
     *
     * @param forceNotifyObservers - force notify observers
     */
    public void setForceNotifyObservers(final boolean forceNotifyObservers) {
        this.forceNotifyObservers = forceNotifyObservers;
    }

    /**
     * Get the time after which a neighbour is considered disconnected.
     *
     * @return the time after which a neighbour is considered disconnected.
     */

    public int getDeadTime() {
        return deadTime;
    }

    /**
     * Get the time after which a neighbour is considered disconnected.
     *
     * @return the time after which a neighbour is considered disconnected.
     */

    public int getInitTime() {
        return initTime;
    }

    /**
     * Set Station's ID.
     *
     * @param stationID
     */
    public void setStationID(final int stationID) {
        bcast.setStationID(stationID);
    }

    /**
     * Set eu.funinnumbers.station's Led ID.
     *
     * @param ledId
     */
    public void setStationLedId(final int ledId) {
        bcast.setStationLedId(ledId);
    }

    public void setEnableAdHoc(final boolean enableAdHoc) {
        rcvr.setEnableAdHoc(enableAdHoc);
    }

    /**
     * Returns true if the broadcaster is enabled.
     *
     * @return a boolean, true if the broadcaster is enabled
     */
    public boolean isEnabled() {
        return bcast.enabled;
    }
}


