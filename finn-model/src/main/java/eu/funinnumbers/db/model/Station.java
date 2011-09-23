package eu.funinnumbers.db.model;

import com.sun.spot.util.IEEEAddress;

/**
 * Contains information related to the Station that used to play game.
 */
public class Station /*implements java.io.Serializable*/ {

    /**
     * Required by Serializable interface.
     */
    static final long serialVersionUID = 42L; //NOPMD

    /**
     * Station's name.
     */
    private String name;

    /**
     * Station's ID.
     */
    private int stationId;

    /**
     * Station's ip address.
     */
    private String ipAddr;

    /**
     * Station's address.
     */
    private Long address;

    /**
     * Station's location.
     */
    private String location;

    /**
     * Station's coordinates.
     */
    private String coordinates;

    /**
     * The BattleEngine of the Station.
     */
    private BattleEngine battleEngine;

    /**
     * The representation of the StationApp ID on the Guardian LEDS is stored on the DB and defined by user.
     */
    private int ledId;

    /**
     * Empty constructor.
     */
    public Station() {
        // Nothing to do  
    }

    /**
     * Constructor for subclassing.
     *
     * @param nameP         the name of the Station
     * @param stationIdP    the unique ID of the eu.funinnumbers.station
     * @param ipAddrP       the IP address
     * @param addressP      the MAC address of the eu.funinnumbers.station
     * @param locationP     a description of the physical location
     * @param coordinatesP  the coordinates of the position of the eu.funinnumbers.station
     * @param ledIdP        the representation of the StationApp ID on the Guardian LEDS is stored on the DB
     *                      and defined by user
     * @param battleEngineP the Engine controlling this eu.funinnumbers.station
     */
    public Station(final String nameP, final int stationIdP, final String ipAddrP, final Long addressP,
                   final String locationP, final String coordinatesP, final int ledIdP,
                   final BattleEngine battleEngineP) {
        this.name = nameP;
        this.stationId = stationIdP;
        this.ipAddr = ipAddrP;
        this.address = addressP;
        this.location = locationP;
        this.coordinates = coordinatesP;
        this.ledId = ledIdP;
        this.battleEngine = battleEngineP;
    }


    /**
     * Returns eu.funinnumbers.station's ip.
     *
     * @return a String with the ip
     */
    public String getIpAddr() {
        return ipAddr;
    }

    /**
     * Set eu.funinnumbers.station's ip address.
     *
     * @param ipAddrP String with the ip
     */
    public void setIpAddr(final String ipAddrP) {
        this.ipAddr = ipAddrP;
    }

    /**
     * Returns the Battle Engine associated with the eu.funinnumbers.station.
     *
     * @return The BattleEngine
     */
    public BattleEngine getBattleEngine() {
        return battleEngine;
    }

    /**
     * Set of Events associated with this battleEngine.
     */
    private java.util.Set events /*START_OF_COMMENT*/ = new java.util.HashSet()/*END_OF_COMMENT*/;

    /**
     * Get the Events of this Station.
     *
     * @return A Set of events
     */
    public java.util.Set getEvents() {
        return events;
    }

    /**
     * Set the events of this battleEngine.
     *
     * @param eventsP A Set of events
     */
    public void setEvents(final java.util.Set eventsP) {
        this.events = eventsP;
    }

    /**
     * Sets the Battle Engine of the Station.
     *
     * @param battleEngineP A BattleEngine object
     */
    public void setBattleEngine(final BattleEngine battleEngineP) {
        this.battleEngine = battleEngineP;
    }

    /**
     * Get the name of the eu.funinnumbers.station.
     *
     * @return A string with the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the eu.funinnumbers.station.
     *
     * @param nameP A string with the name
     */
    public void setName(final String nameP) {
        this.name = nameP;
    }

    /**
     * Get stationId.
     *
     * @return An int with the stationId
     */
    public int getStationId() {
        return stationId;
    }

    /**
     * Set stationId.
     *
     * @param stationIdP An int with stationId
     */
    public void setStationId(final int stationIdP) {
        this.stationId = stationIdP;
    }

    /**
     * Get the address of a eu.funinnumbers.station.
     *
     * @return A Long with the address
     */
    public Long getAddress() {
        return address;
    }

    /**
     * Set the address of a eu.funinnumbers.station.
     *
     * @param addressP A Long with the address
     */
    public void setAddress(final Long addressP) {
        this.address = addressP;
    }

    /**
     * Set the address of a eu.funinnumbers.station.
     *
     * @param addressP A string with the address
     */
    public void setAddress(final String addressP) {
        this.address = new Long(IEEEAddress.toLong(addressP));
    }

    /**
     * Get the address of a eu.funinnumbers.station.
     *
     * @return A String with the address
     */
    public String getStringAddress() {
        return IEEEAddress.toDottedHex(this.address.longValue());
    }

    /**
     * Get eu.funinnumbers.station ledId.
     *
     * @return An int with the eu.funinnumbers.station ledId
     */
    public int getLEDId() {
        return ledId;
    }

    /**
     * Set eu.funinnumbers.station ledId.
     *
     * @param ledIdP An int with eu.funinnumbers.station ledId
     */
    public void setLEDId(final int ledIdP) {
        this.ledId = ledIdP;
    }

    /**
     * Returns the location of the Station.
     *
     * @return A String with the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set the location of the Station.
     *
     * @param locationP A String with the location
     */
    public void setLocation(final String locationP) {
        this.location = locationP;
    }

    /**
     * Return the coordinates of the eu.funinnumbers.station.
     *
     * @return A String with the coordinates
     */
    public String getCoordinates() {
        return coordinates;
    }

    /**
     * Set the coordinates of the eu.funinnumbers.station.
     *
     * @param coordinatesP A String with the coordinates
     */
    public void setCoordinates(final String coordinatesP) {
        this.coordinates = coordinatesP;
    }
}
