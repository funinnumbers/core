package eu.funinnumbers.db.model;


import eu.funinnumbers.util.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;


/**
 * Contains information related to the player (the Avatar).
 */
public class Avatar implements StorableEntity /*, java.io.Serializable*/ {

    /**
     * Required by Serializable interface.
     */
    static final long serialVersionUID = 42L; //NOPMD

    /**
     * Avatar's ID.
     */
    private int ID; // NOPMD

    /**
     * The number of the battles that an avatar won.
     */
    private int gamesWon;

    /**
     * The number of the battles that an avatar lost.
     */
    private int gamesLost;

    /**
     * Avatar's name.
     */
    private String name;

    /**
     * An integer that associates Liferay Player Id with eu.funinnumbers.db Id.
     */
    private int liferayId;

    /**
     * A Set of the Avatar's Teams.
     */
    private java.util.Set teams /*START_OF_COMMENT*/ = new java.util.HashSet()/*END_OF_COMMENT*/;

    /**
     * A Set of the Avatar's Guardians.
     */
    private java.util.Set guardians /*START_OF_COMMENT*/ = new java.util.HashSet()/*END_OF_COMMENT*/;

    /**
     * A Set of the Avatar's Events.
     */
    private java.util.Set events /*START_OF_COMMENT*/ = new java.util.HashSet()/*END_OF_COMMENT*/;

    /**
     * Set of BattleEngines associated to this Avatar.
     */
    private java.util.Set battleEngines /*START_OF_COMMENT*/ = new java.util.HashSet()/*END_OF_COMMENT*/;

    /**
     * Get a Set of events of the Avatar.
     *
     * @return A Set of events
     */
    public java.util.Set getEvents() {
        return events;
    }

    /**
     * Set the events of the avatar.
     *
     * @param eventSet a set of events
     */
    public void setEvents(final java.util.Set eventSet) {
        this.events = eventSet;
    }

    /**
     * Get a Set of teams of the Avatar.
     *
     * @return A Set of teams
     */
    public java.util.Set getTeams() {
        return teams;
    }

    /**
     * Set the teams of the Avatar.
     *
     * @param teamSet a set of teams
     */
    public void setTeams(final java.util.Set teamSet) {
        this.teams = teamSet;
    }


    /**
     * Adds a team to the existing Set of teams.
     *
     * @param team The team
     */      /*START_OF_COMMENT*/
    public void addTeam(final Team team) {
        this.getTeams().add(team);
    }
    /*END_OF_COMMENT*/

    /**
     * Set the guardians of the Avatar.
     *
     * @return a Set of guardians
     */
    public java.util.Set getGuardians() {
        return guardians;
    }

    /**
     * Set the Guardians of the Avatar.
     *
     * @param guardianSet A set of guardians
     */
    public void setGuardians(final java.util.Set guardianSet) {
        this.guardians = guardianSet;
    }


    /**
     * Adds a eu.funinnumbers.guardian ton the existing Set of guardians.
     *
     * @param guardian A eu.funinnumbers.guardian
     */    /*START_OF_COMMENT*/
    public void addGuardian(final Guardian guardian) {
        this.getGuardians().add(guardian);
    }
    /*END_OF_COMMENT*/

    /**
     * Returns the liferay Id of the avatar.
     *
     * @return an Inte with the liferay ID
     */
    public int getLiferayId() {
        return liferayId;
    }

    /**
     * Get the battleEngines of an Avatar.
     *
     * @return a Set of BatttleEngines
     */
    public java.util.Set getBattleEngines() {
        return battleEngines;
    }

    /**
     * Set the battleEngines of an Avatar.
     *
     * @param battleEnginesSet A Set of BattleEngines
     */
    public void setBattleEngines(final java.util.Set battleEnginesSet) {
        this.battleEngines = battleEnginesSet;
    }

    /**
     * Add a battleEngine to this Avatar.
     *
     * @param battle Add a BattleEngine
     */ /*START_OF_COMMENT*/    
    public void addBattleEngine(final BattleEngine battle) {
        this.getBattleEngines().add(battle);
    }
    /*END_OF_COMMENT*/


    /**
     * Set the liferay id of an avatar.
     *
     * @param liferayIdP an Int with the liferay ID
     */
    public void setLiferayId(final int liferayIdP) {
        this.liferayId = liferayIdP;
    }

    /**
     * Returns the number of Battles the Avatar won.
     *
     * @return an Inte with the number of battles won
     */
    public int getGamesWon() {
        return gamesWon;
    }

    /**
     * Set the number of Battles the Avatar won.
     *
     * @param gamesWonP An int with the number of battles won
     */
    public void setGamesWon(final int gamesWonP) {
        this.gamesWon = gamesWonP;
    }


    /**
     * Returns the number of Battles the Avatar Lost.
     *
     * @return an Int with the number of battles Lost
     */
    public int getGamesLost() {
        return gamesLost;
    }

    /**
     * Set the number of Battles the Avatar Lost.
     *
     * @param gamesLostP An int with the number of battles Lost
     */
    public void setGamesLost(final int gamesLostP) {
        this.gamesLost = gamesLostP;
    }

    /**
     * Get the avatarId.
     *
     * @return An int with the avatarId
     */
    public int getID() {
        return ID;
    }

    /**
     * Set avatarId.
     *
     * @param avatarIdP An int with avatarId
     */
    public void setID(final int avatarIdP) {
        this.ID = avatarIdP;
    }

    /**
     * Returns the type of the entity.
     *
     * @return the type of the entity
     */
    public final int getEntityType() {
        return StorableEntity.AVATARENTITY;
    }

    /**
     * Get the name of the avatar.
     *
     * @return A string with the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the avatar.
     *
     * @param nameP A string with the name
     */
    public void setName(final String nameP) {
        this.name = nameP;
    }


    /**
     * Converts the entity to byte array.
     *
     * @return the byte array of the entity
     */
    public final byte[] toByteArray() {

        byte[] data = null;

        try {
            // Create byte array
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            final DataOutputStream dout = new DataOutputStream(bout);


            dout.writeInt(ID);
            dout.writeUTF(name);


            dout.flush();
            dout.close();

            data = bout.toByteArray();

        } catch (Exception e) {
            Logger.getInstance().debug(e);
        }

        // Return byte array
        return data;
    }

    /**
     * Initializes the entity by reading its byte array.
     *
     * @param entityBuffer - the entity's buffer
     */
    public final void fromByteArray(final byte[] entityBuffer) {

        try {
            // Read entity's fields from byte array
            final ByteArrayInputStream bin = new ByteArrayInputStream(entityBuffer);
            final DataInputStream din = new DataInputStream(bin);

            ID = din.readInt();
            name = din.readUTF();

            din.close();
            bin.close();

        } catch (Exception e) {
            Logger.getInstance().debug(e);
        }
    }


}
