package eu.funinnumbers.db.model.item;

/**
 * Represents an item that can be used to defend against attacks from other avatars/players.
 */
public class DefenseItem extends Item {

    /**
     * Required by Serializable interface.
     */
    static final long serialVersionUID = 42L; //NOPMD

    /**
     * Indicates the power defense of an item.
     */
    private int powerDefense;

    /**
     * Get the power defense of an item.
     *
     * @return An int with the power defense
     */
    public int getPowerDefense() {
        return powerDefense;
    }

    /**
     * Set power defense.
     *
     * @param powerDefenseP An int with the power defense
     */
    public void setPowerDefense(final int powerDefenseP) {
        this.powerDefense = powerDefenseP;
    }

}
