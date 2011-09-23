package eu.funinnumbers.guardian.ui.menu;

/*
 * SunSpotMenuItem.java
 *
 */

import com.sun.spot.sensorboard.peripheral.LEDColor;
import eu.funinnumbers.guardian.ui.action.ActionListener;
import eu.funinnumbers.guardian.ui.misc.LEDManager;

/**
 * Each <code> SunSpotMenuItem </code> contains an unique itemID, an action and a boolean
 * variable that indicates if the current object is Checkable.
 */
public class SunSpotMenuItem {

    /**
     * itemID of <code> SunSpotMenuItem </code>.
     */
    private int itemID;

    /**
     * The color of the MenuItem.
     */
    private LEDColor color;

    /**
     * ActionListener that relates with the <code> SunSpotMenuItem </code>.
     */
    private final ActionListener action;

    /**
     * Constructor for a new instance of <code> SunSpotMenuItem </code>.
     *
     * @param itemIDP The itemID of the item
     * @param actionP The ActionListener that relates with the item
     * @param colorP  The Color of the Low-Level LED
     */
    public SunSpotMenuItem(final int itemIDP, final ActionListener actionP, final LEDColor colorP) {
        this.itemID = itemIDP;
        this.action = actionP;
        this.color = colorP;
    }

    /**
     * Constructor for a new instance of <code> SunSpotMenuItem </code>.
     * The color of the menu is selected based on the itemID.
     * Can support up to 10 different colors.
     *
     * @param itemIDP The itemID of the item
     * @param actionP The ActionListener that relates with the item
     */
    public SunSpotMenuItem(final int itemIDP, final ActionListener actionP) {
        this(itemIDP, actionP, LEDManager.LED_COLORS[itemIDP]);
    }

    /**
     * Get the itemID of the current <code> SunSpotMenuItem </code>.
     *
     * @return the itemID of the selected <code> SunSpotMenuItem </code>
     */
    public int getItemID() {
        return itemID;
    }

    /**
     * Get the ActionListener associated to this MenuItem.
     *
     * @return the ActionListener
     */
    public ActionListener getAction() {
        return action;
    }

    /**
     * Returns the color of the LED for this menu item.
     *
     * @return the Color for the Low-level LED
     */
    public LEDColor getColor() {
        return color;
    }
}
