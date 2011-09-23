package eu.funinnumbers.db.model.item;

/**
 * Represents items that cannot be used but are only carried by avatars/players and are important to the victory of a
 * battle.
 */
public class ArtifactItem extends Item {

    /**
     * Required by Serializable interface.
     */
    static final long serialVersionUID = 42L; //NOPMD

    /**
     * The victory points of the artifact.
     */
    private int victoryPoints;

    /**
     * Get victory points of an artifact.
     *
     * @return An int with victory points
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * Set victory points of an artifact.
     *
     * @param victoryPointsP An int with vicory points
     */
    public void setVictoryPoints(final int victoryPointsP) {
        this.victoryPoints = victoryPointsP;
    }
}
