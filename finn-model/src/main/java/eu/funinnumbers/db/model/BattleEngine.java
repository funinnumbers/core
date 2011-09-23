package eu.funinnumbers.db.model;

import java.util.Date;


/**
 * Represents the eu.funinnumbers.engine of a game.
 */
public class BattleEngine /*implements java.io.Serializable*/ {        // NOPMD

    /**
     * Required by Serializable interface.
     */
    static final long serialVersionUID = 42L;

    /**
     * BattleEngine's ID.
     */
    private int id;  // NOPMD

    /**
     * BattleEngine's name.
     */
    private String name;

    /**
     * BattleEngine's ip address.
     */
    private String ipAddr;

    /**
     * Max number of the players that can participate in battle.
     */
    private int maxPlayers;

    /**
     * Battle's status.
     */
    private String status;

    /**
     * Start time of a battle.
     */
    private Date startTime;

    /**
     * Finish time of a battle.
     */
    private Date finishTime;

    /**
     * Truce period in a battle.
     */
    private int trucePeriod;

    /**
     * BattleEngine's location.
     */
    private String location;

    /**
     * BattleEngine's coordinates.
     */
    private String coordinates;

    /**
     * Set true if battleEngine is alive else false.
     */
    private boolean isAlive;

    /**
     * The id of owner's battle eu.funinnumbers.engine.
     */
    private int ownerId;

    /**
     * This is not stored in the database.
     */
    private String buildXMLpath;

    /**
     * Set of Stations associated to this BattleEngine.
     */
    private java.util.Set stations /*START_OF_COMMENT*/ = new java.util.HashSet()/*END_OF_COMMENT*/;

    /**
     * Set of Teams associated to this BattleEngine.
     */
    private java.util.Set teams /*START_OF_COMMENT*/ = new java.util.HashSet()/*END_OF_COMMENT*/;

    /**
     * Set of Avatars associated to this BattleEngine.
     */
    private java.util.Set avatars /*START_OF_COMMENT*/ = new java.util.HashSet()/*END_OF_COMMENT*/;

    /**
     * Set of Events associated with this battleEngine.
     */
    private java.util.Set events /*START_OF_COMMENT*/ = new java.util.HashSet()/*END_OF_COMMENT*/;

    /**
     * Get the Events of the battlEngine.
     *
     * @return A Set of events
     */
    public java.util.Set getEvents() {
        return events;
    }

    /**
     * Set the events of the battleEngine.
     *
     * @param eventsP A Set of events
     */
    public void setEvents(final java.util.Set eventsP) {
        this.events = eventsP;
    }

    /**
     * Get the avatars of the battleEngine.
     *
     * @return A set of Avatars
     */
    public java.util.Set getAvatars() {
        return avatars;
    }

    /**
     * Get the battle eu.funinnumbers.engine owner id.
     *
     * @return Id with owners ID
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * Set the battle eu.funinnumbers.engine owner id.
     *
     * @param ownerIdP id of the owner as integer
     */
    public void setOwnerId(final int ownerIdP) {
        this.ownerId = ownerIdP;
    }

    /**
     * Set the avatars of the battleEngine.
     *
     * @param avatarsSet A set of avatars.
     */
    public void setAvatars(final java.util.Set avatarsSet) {
        this.avatars = avatarsSet;
    }


    /**
     * Add a new Avatar.
     *
     * @param avatar an Avanar object
     */ /*START_OF_COMMENT*/
    public void addAvatar(final Avatar avatar) {
        this.getAvatars().add(avatar);

    }
    /*END_OF_COMMENT*/

    /**
     * Get the teams of the battlEngine.
     *
     * @return A Set of teams
     */
    public java.util.Set getTeams() {
        return teams;
    }

    /**
     * Set the teams of the battleEngine.
     *
     * @param teamsSet A Set of teams
     */
    public void setTeams(final java.util.Set teamsSet) {
        this.teams = teamsSet;
    }


    /**
     * Add a new Team.
     *
     * @param team the Team object
     */ /*START_OF_COMMENT*/
    public void addTeam(final Team team) {
        this.getTeams().add(team);
    }
    /*END_OF_COMMENT*/

    /**
     * Get the Stations of the battleEngine.
     *
     * @return A Set of stations
     */    
    public java.util.Set<Station> getStations() {
        return stations;
    }

    /**
     * Set the stations of the battleEngine.
     *
     * @param stationsSet A Set of stations
     */
    public void setStations(final java.util.Set stationsSet) {
        stations = stationsSet;
    }


    /**
     * Add a new Station.
     *
     * @param station the Station object
     */ /*START_OF_COMMENT*/
    public void addStation(final Station station) {
        this.getStations().add(station);
    }
    /*END_OF_COMMENT*/

    /**
     * Return the ID of the battle ebgine.
     *
     * @return the ID of this BattleEngine object
     */
    public int getId() {
        return id;
    }

    /**
     * Set the ID of the battle eu.funinnumbers.engine.
     *
     * @param idP An Int with the id
     */
    public void setId(final int idP) {
        this.id = idP;
    }

    /**
     * Get the name of the eu.funinnumbers.engine.
     *
     * @return A string with the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the eu.funinnumbers.engine.
     *
     * @param nameP A string with the name
     */
    public void setName(final String nameP) {
        this.name = nameP;
    }

    /**
     * Get the ip address.
     *
     * @return A String with the ip address
     */
    public String getIpAddr() {
        return ipAddr;
    }

    /**
     * Set the ip address.
     *
     * @param ipAddrP A String with the ip address
     */
    public void setIpAddr(final String ipAddrP) {
        this.ipAddr = ipAddrP;
    }

    /**
     * Returns the max number of Players.
     *
     * @return An Int with the maximum number of players
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Set the max number of Players.
     *
     * @param maxPlayersP An Int with the maximun number of players
     */
    public void setMaxPlayers(final int maxPlayersP) {
        this.maxPlayers = maxPlayersP;
    }

    /**
     * Return the Status of the batte eu.funinnumbers.engine.
     *
     * @return A String with the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the Status of the batte eu.funinnumbers.engine.
     *
     * @param statusP A String with the status
     */
    public void setStatus(final String statusP) {
        this.status = statusP;
    }

    /**
     * Return the start time of the battle.
     *
     * @return A Date object with the start time
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Set the start time of the battle.
     *
     * @param startTimeP A Date object with the start time
     */
    public void setStartTime(final Date startTimeP) {
        this.startTime = startTimeP;
    }

    /**
     * Return the finish time of the battle.
     *
     * @return A Date object with the finish time
     */
    public Date getFinishTime() {
        return finishTime;
    }

    /**
     * Set the finish time of the battle.
     *
     * @param finishTimeP A Date object with the finish time
     */
    public void setFinishTime(final Date finishTimeP) {
        this.finishTime = finishTimeP;
    }

    /**
     * Return the truce period.
     *
     * @return An Int with the truce period
     */
    public int getTrucePeriod() {
        return trucePeriod;
    }

    /**
     * Set truce period.
     *
     * @param trucePeriodP An Int with the truce period
     */
    public void setTrucePeriod(final int trucePeriodP) {
        this.trucePeriod = trucePeriodP;
    }

    /**
     * Return the location of the battle eu.funinnumbers.engine.
     *
     * @return A String with the location of the battle eu.funinnumbers.engine
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set the location of the battle eu.funinnumbers.engine.
     *
     * @param locationP A String with the location of the battle eu.funinnumbers.engine
     */
    public void setLocation(final String locationP) {
        this.location = locationP;
    }

    /**
     * Return the coordinates of the battle eu.funinnumbers.engine.
     *
     * @return A String with the coordinates of th batte ebgine
     */
    public String getCoordinates() {
        return coordinates;
    }

    /**
     * Set the coordinates of the battle eu.funinnumbers.engine.
     *
     * @param coordinatesP A String with the coordinates of th batte ebgine
     */
    public void setCoordinates(final String coordinatesP) {
        this.coordinates = coordinatesP;
    }

    /**
     * Returns true if the battle eu.funinnumbers.engine is alive and false otherwise.
     *
     * @return A boolean
     */
    public boolean getIsAlive() { // NOPMD
        return isAlive;
    }

    /**
     * Set true if the battle eu.funinnumbers.engine is alive and false otherwise.
     *
     * @param alive A boolean
     */
    public void setIsAlive(final boolean alive) {
        isAlive = alive;
    }

    /**
     * Get the path of the build.XML file used to compile and deploy the code for the guardians.
     *
     * @return the path of the build.xml file
     */
    public String getBuildXMLpath() {
        return buildXMLpath;
    }

    /**
     * Set the path of the build.XML file used to compile and deploy the code for the guardians.
     *
     * @param buildXMLpathP the path of the build.xml file
     */
    public void setBuildXMLpath(final String buildXMLpathP) {
        buildXMLpath = buildXMLpathP;
    }
}
