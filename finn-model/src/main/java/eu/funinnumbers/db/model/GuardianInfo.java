package eu.funinnumbers.db.model;

import java.util.Vector;

/**
 * Holds all information regarding this Guardian.
 */
public final class GuardianInfo {

    /**
     * Unique instance of this class.
     */
    private static GuardianInfo instance;

    /**
     * The Guardian info of this device.
     */
    private Guardian guardian;

    /**
     * The Avatar info of this device.
     */
    private Avatar avatar;

    /**
     * The Team info of this device.
     */
    private Team team;

    /**
     * A vector of enemy ids.
     */
    private Vector enemyGuardiansID;

    /**
     * Simple Constructor.
     */
    private GuardianInfo() {
        enemyGuardiansID = new Vector(); // NOPMD
    }

    /**
     * Returns the current instance of the singleton class. If it does not exist, it constructs a new class.
     *
     * @return the current global GuardianInfo instance
     */
    public static GuardianInfo getInstance() {
        synchronized (GuardianInfo.class) {
            if (instance == null) {
                instance = new GuardianInfo();
            }
        }

        return instance;
    }

    /**
     * Returns the <code>Guardian</code> object of this Guardian.
     *
     * @return <code>Guardian</code> object
     */
    public Guardian getGuardian() {
        return guardian;
    }

    /**
     * Sets a <code>Guardian</code> object for this Guardian.
     *
     * @param thisGuardian <code>Guardian</code> Object
     */
    public void setGuardian(final Guardian thisGuardian) {
        guardian = thisGuardian;
    }

    /**
     * Returns the <code>Avatar</code> object related to this Guardian.
     *
     * @return <code>Avatar</code> object of this Guardian
     */
    public Avatar getAvatar() {
        return avatar;
    }

    /**
     * Sets the <code>Avatar</code> object to be related with this Guardian.
     *
     * @param thisAvatar <code>Avatar</code> object
     */
    public void setAvatar(final Avatar thisAvatar) {
        avatar = thisAvatar;
    }

    /**
     * Returns the team which this Guardian is assigned to.
     *
     * @return a <code>Team</code> object
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Sets the team which this Guardian will be assigned to.
     *
     * @param thisTeam a <code>Team</code> object
     */
    public void setTeam(final Team thisTeam) {
        team = thisTeam;
    }

    /**
     * Returns a vector of hostile guardians within the radio range of this Guardian.
     *
     * @return a vector of enemy guardians
     */
    public Vector getEnemyGuardiansIDs() {
        return enemyGuardiansID;
    }

    /**
     * Sets the vector of the hostile guardians in the radio range of this Guardian.
     *
     * @param vector Vector of enemy guardians
     */
    public void setEnemyGuardiansIDs(final Vector vector) {
        enemyGuardiansID = vector;
    }

}


