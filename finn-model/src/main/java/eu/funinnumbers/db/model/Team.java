package eu.funinnumbers.db.model;

import eu.funinnumbers.util.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Contains information about the team of a battle eu.funinnumbers.engine.
 */
public class Team implements StorableEntity /*, java.io.Serializable*/ {

    /**
     * Required by Serializable interface.
     */
    static final long serialVersionUID = 42L; //NOPMD

    /**
     * Team's id.
     */
    private int teamId;

    /**
     * Team's name.
     */
    private String name;

    /**
     * The number of the players that can join the team .
     */
    private int maxPlayers;

    /**
     * Team's battleEngine.
     */
    private BattleEngine battleEngine = null;

    /**
     * A Set of Avatars associated with team.
     */
    private java.util.Set avatars /*START_OF_COMMENT*/ = new java.util.HashSet()/*END_OF_COMMENT*/;

    /**
     * A Set of avatars of the team.
     *
     * @return A set of Avatars
     */
    public java.util.Set getAvatars() {
        return avatars;
    }

    /**
     * Set of Avatars associated with this team.
     *
     * @param avatarsP A set of avatars
     */
    public void setAvatars(final java.util.Set avatarsP) {
        avatars = avatarsP;
    }

    /**
     * Adds an avatar to the existing Avatar set of this team.
     *
     * @param avatar The avatar
     */      /*START_OF_COMMENT*/    
    public void addAvatar(final Avatar avatar) {
        this.getAvatars().add(avatar);
    }
    /*END_OF_COMMENT*/

    /**
     * Get the Battle Engine associated with the team.
     *
     * @return The MonkEngine
     */
    public BattleEngine getBattleEngine() {
        return battleEngine;
    }

    /**
     * Set the Battle Engine associated with the team.
     *
     * @param battleEngineP The battleEngine
     */
    public void setBattleEngine(final BattleEngine battleEngineP) {
        this.battleEngine = battleEngineP;
    }

    /**
     * Get teamId.
     *
     * @return An int with teamId
     */
    public int getTeamId() {
        return teamId;
    }

    /**
     * Set teamId.
     *
     * @param teamIdP An int with teamId
     */
    public void setTeamId(final int teamIdP) {
        this.teamId = teamIdP;
    }

    /**
     * Get the name of the team.
     *
     * @return A string with name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the team.
     *
     * @param nameP A string with the name
     */
    public void setName(final String nameP) {
        this.name = nameP;
    }

    /**
     * Get the maxPlayers.
     *
     * @return An int with the max number of players
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Set maxPlayers.
     *
     * @param maxPlayersP An int with the max number of players
     */
    public void setMaxPlayers(final int maxPlayersP) {
        this.maxPlayers = maxPlayersP;
    }

    /**
     * Return Entity type id number.
     *
     * @return Team Entity id number
     */
    public int getEntityType() {
        return StorableEntity.TEAMENTITY;
    }

    /**
     * Get this team unique id.
     *
     * @return An int with the id
     */
    public int getID() {
        return teamId;
    }

    /**
     * Set the unique id of this team.
     *
     * @param entityId An int with the id
     */
    public void setID(final int entityId) {
        this.teamId = entityId;
    }

    /**
     * Save the BattleEgine to a Byte Array.
     *
     * @return the Byte Array with battleEngine's data
     */
    public byte[] toByteArray() {

        byte[] data = null;

        try {
            // Create byte array
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            final DataOutputStream dout = new DataOutputStream(bout);

            dout.writeInt(teamId);
            dout.writeUTF(name);
            dout.writeInt(maxPlayers);
            dout.writeInt(battleEngine.getId());

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
     * Construct new BattlEngine from a Byte Array.
     *
     * @param entityBuffer - the entity's buffer
     */
    public void fromByteArray(final byte[] entityBuffer) {
        try {
            // Read entity's fields from byte array
            final ByteArrayInputStream bin = new ByteArrayInputStream(entityBuffer);
            final DataInputStream din = new DataInputStream(bin);

            teamId = din.readInt();
            name = din.readUTF();
            maxPlayers = din.readInt();
            battleEngine = new BattleEngine();
            battleEngine.setId(din.readInt());

            din.close();
            bin.close();

        } catch (Exception e) {
            Logger.getInstance().debug(e);
        }


    }
}

