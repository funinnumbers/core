package eu.funinnumbers.db.model.item;

/**
 * Represents an item that can be used to attack another avatar/player.
 */
public class AttackItem extends Item {

    /**
     * Required by Serializable interface.
     */
    static final long serialVersionUID = 42L; //NOPMD

    /**
     * The power attack of the item.
     */
    private int powerAttack;

    /**
     * The accuracy of the weapon.
     */
    private int accuracy;

    /**
     * An int that shows how many times the item can be used.
     */
    private int weaponLimitation;

    /**
     * Get the accuracy of an attack item.
     *
     * @return An int with the accuracy
     */
    public int getAccuracy() {
        return accuracy;
    }

    /**
     * Set accuracy of an attac item.
     *
     * @param accuracyP An int with the accuracy
     */
    public void setAccuracy(final int accuracyP) {
        this.accuracy = accuracyP;
    }

    /**
     * Get weapon limitation.
     *
     * @return An int with the weapon limitation
     */
    public int getWeaponLimitation() {
        return weaponLimitation;
    }

    /**
     * Set weapon limitation.
     *
     * @param weaponLimitationP An int that shows how many times the item can be used
     */
    public void setWeaponLimitation(final int weaponLimitationP) {
        this.weaponLimitation = weaponLimitationP;
    }

    /**
     * Get power attack of an item.
     *
     * @return An int with power attack
     */
    public int getPowerAttack() {
        return powerAttack;
    }

    /**
     * Set the power attack of an item.
     *
     * @param powerAttackP An int with the power attack
     */
    public void setPowerAttack(final int powerAttackP) {
        this.powerAttack = powerAttackP;
    }
}
