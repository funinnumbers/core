package eu.funinnumbers.db.model.item;

import eu.funinnumbers.db.model.Avatar;

/**
 * Represents an item that can be used from an avatar/player.
 */
public class Item /*implements java.io.Serializable*/ {

    /**
     * Required by Serializable interface.
     */
    static final long serialVersionUID = 42L; //NOPMD

    /**
     * Item's Id.
     */
    private int itemId;

    /**
     * Item's name.
     */
    private String name;

    /**
     * The description of an item.
     */
    private String description;

    /**
     * The item's avatar.
     */
    private Avatar avatar;

    /**
     * Get the avatar associated with the item.
     *
     * @return The avatar
     */
    public Avatar getAvatar() {
        return avatar;
    }

    /**
     * Set the avatar associated with the item.
     *
     * @param avatarP The avatar
     */
    public void setAvatar(final Avatar avatarP) {
        this.avatar = avatarP;
    }

    /**
     * Get the itemId.
     *
     * @return An int with itemId
     */
    public int getItemId() {
        return itemId;
    }

    /**
     * Set itemId.
     *
     * @param itemIdP An int with the new itemId
     */
    public void setItemId(final int itemIdP) {
        this.itemId = itemIdP;
    }

    /**
     * Get the name of the item.
     *
     * @return A string with the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set item's name.
     *
     * @param nameP Name of the item
     */
    public void setName(final String nameP) {
        this.name = nameP;
    }

    /**
     * Get the description of an item.
     *
     * @return A string with the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of an item.
     *
     * @param descriptionP A string with the description
     */
    public void setDescription(final String descriptionP) {
        this.description = descriptionP;
    }


}
