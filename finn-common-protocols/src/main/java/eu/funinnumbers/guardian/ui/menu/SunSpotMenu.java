package eu.funinnumbers.guardian.ui.menu;

/*
 * SunSpotMenu.java
 *
 */

import eu.funinnumbers.util.Logger;

import java.util.Vector;


/**
 * Each <code> SunSpotMenu </code> contains an Vector of <code> SunSpotMenuVectors </code>
 * and an index that indicates the current <code> SunSpotMenuVector </code> of the Vector.
 */
public class SunSpotMenu {

    /**
     * Vector of <code> SunSpotMenuVectors </code> of the current <code> SunSpotMenu </code>.
     */
    private final Vector menuVectors;

    /**
     * Index that show the current item of the <code> SunSpotMenu </code>.
     */
    private int index;

    /**
     * Constructor for a new instance of <code> SunSpotMenu </code>.
     */
    public SunSpotMenu() {
        this.menuVectors = new Vector(); // NOPMD
        this.index = 0;
    }

    /**
     * This method adds a new <code> SunSpotMenuVector </code> element in the end of the <code> SunSpotMenu </code>.
     *
     * @param menuVector the vector of menu elements to add
     */    
    public void addVectorToMenu(final SunSpotMenuVector menuVector) {
        menuVectors.addElement(menuVector);
    }

    /**
     * This method removes the index-th element of the <code> SunSpotMenu </code>.
     *
     * @param indexP the index of the menu element to remove
     */
    public void removeVectorFromMenuAt(final int indexP) {
        menuVectors.removeElementAt(indexP);
    }

    /**
     * This method removes the current element of the <code> SunSpotMenu </code>.
     */
    public void removeCurrentVectorFromMenu() {
        menuVectors.removeElementAt(this.index);
    }

    /**
     * Go to the first element of the <code> SunSpotMenu </code>.
     */
    public void goToFirstVectorOfMenu() {
        this.index = 0;
    }

    /**
     * Go to the next element of the <code> SunSpotMenu </code>.
     *
     * @return the next <code> SunSpotMenuVector </code>
     */
    public SunSpotMenuVector goToNextVectorOfMenu() {
        if (menuVectors.isEmpty()) {
            this.goToFirstVectorOfMenu();
        }

        if (menuVectors.lastElement() == menuVectors.elementAt(this.index)) {
            this.goToFirstVectorOfMenu();
        } else {
            this.index++;
        }

        return (SunSpotMenuVector) menuVectors.elementAt(this.index);
    }

    /**
     * Get the current element of the <code> SunSpotMenu </code>.
     *
     * @return the current <code> SunSpotMenuVector </code>
     */
    public SunSpotMenuVector getCurrentVectorOfMenu() {
        if (menuVectors.isEmpty()) {
            return null;
        }
        return (SunSpotMenuVector) menuVectors.elementAt(this.index);
    }

    /**
     * Get the current's Vector ID of the <code> SunSpotMenu </code>.
     *
     * @return the id of the current <code> SunSpotMenuVector </code>
     */
    public int getCurrentVectorIDOfMenu() {
        SunSpotMenuVector temp;
        temp = (SunSpotMenuVector) menuVectors.elementAt(this.index);
        return temp.getCurrentVectorID();
    }

    /**
     * Get info about the <code> SunSpotMenu </code>.
     */
    public void getMenuInfo() {
        Logger.getInstance().debug(" " + this.menuVectors.size() + " " + this.index);
    }


}
