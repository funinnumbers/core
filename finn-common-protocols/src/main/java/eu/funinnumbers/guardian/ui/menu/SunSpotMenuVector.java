package eu.funinnumbers.guardian.ui.menu;

import com.sun.spot.sensorboard.peripheral.LEDColor;
import eu.funinnumbers.guardian.ui.action.ActionListener;
import eu.funinnumbers.guardian.ui.misc.LEDManager;
import eu.funinnumbers.util.Logger;

import java.util.Vector;

/**
 * Each <code> SunSpotMenuVector </code> contains a Vector of <code>SunSpotMenuItems</code>,
 * an unique vectorID, an action and an index that indicates the current
 * <code> SunSpotMenuItem </code> of the Vector.
 */
public class SunSpotMenuVector extends SunSpotMenuItem {

    /**
     * Vector of <code> SunSpotMenuItems </code> of the current <code> SunSpotMenuVector </code>.
     */
    private final Vector menuItems;

    /**
     * Index that show the current item of the <code> SunSpotMenuVector </code>.
     */
    private int index;

    /**
     * Constructor for a new instance of <code> SunSpotMenuVector </code>.
     *
     * @param vectorID The vectorID of the vector
     * @param action   The ActionListener that relates with the vector
     * @param color    The Color that relates to the menu vector
     */
    public SunSpotMenuVector(final int vectorID, final ActionListener action, final LEDColor color) {
        super(vectorID, action, color);
        menuItems = new Vector(); // NOPMD
    }

    /**
     * Constructor for a new instance of <code> SunSpotMenuVector </code>.
     * The color of the menu is selected based on the vectorID.
     * Can support up to 10 different colors.
     *
     * @param vectorID The vectorID of the vector
     * @param action   The ActionListener that relates with the vector
     */
    public SunSpotMenuVector(final int vectorID, final ActionListener action) {
        this(vectorID, action, LEDManager.LED_COLORS[vectorID]);
    }

    /**
     * This method adds a new element in the end of the <code> SunSpotMenuVector </code>.
     *
     * @param menuItem to add in the vector
     */    
    public void addItemToVector(final SunSpotMenuItem menuItem) {
        menuItems.addElement(menuItem);
    }

    /**
     * This method removes the index-th element of the <code> SunSpotMenuVector </code>.
     *
     * @param indexP An index of the <code>menuItems</code> vector
     */
    public void removeItemFromVectorAt(final int indexP) {
        menuItems.removeElementAt(indexP);
    }

    /**
     * This method removes the element according to the method's parameter, of the <code> SunSpotMenuVector </code>.
     *
     * @param itemID an identification number of a <code> SunSpotMenuItem </code>
     *               element of the <code> menuItems </code> vector
     */
    public void removeItemFromVectorWithID(final int itemID) {
        SunSpotMenuItem temp;
        for (int i = 0; i < menuItems.size(); i++) {
            temp = (SunSpotMenuItem) menuItems.elementAt(i);
            if (temp.getItemID() == itemID) {
                menuItems.removeElementAt(i);
                break;
            }
        }
    }

    /**
     * This method removes the current element of the <code> SunSpotMenuVector </code>.
     */
    public void removeCurrentItemFromVector() {
        menuItems.removeElementAt(this.index);
    }

    /**
     * Go to the first element of the <code> SunSpotMenuVector </code>.
     */
    public void goToFirstItemOfVector() {
        this.index = 0;
    }

    /**
     * Go to the next element of the <code> SunSpotMenuVector </code>.
     */
    public void goToNextItemOfVector() {
        if (menuItems.isEmpty()) {
            return;
        }
        if (menuItems.lastElement() == menuItems.elementAt(this.index)) {
            this.goToFirstItemOfVector();
        } else {
            this.index++;
        }
    }

    /**
     * Get the id of the current <code> SunSpotMenuVector </code>.
     *
     * @return the vectorID of the selected <code> SunSpotMenuVector </code>
     */
    public int getCurrentVectorID() {
        return this.getItemID();
    }

    /**
     * Get the current element of the <code> SunSpotMenuVector </code>.
     *
     * @return the current <code> SunSpotMenuItem </code> of the current <code> SunSpotMenuVector </code>
     */
    public SunSpotMenuItem getCurrentItemOfVector() {
        if (menuItems.isEmpty()) {
            return null;
        }
        return (SunSpotMenuItem) menuItems.elementAt(index);
    }

    /**
     * Get info abou the <code> SunSpotMenuVector </code>.
     */
    public void getMenuInfo() {
        Logger.getInstance().debug(this.getCurrentVectorID() + " " + menuItems.size() + " " + this.index);
    }
}
