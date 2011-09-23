package eu.funinnumbers.guardian.ui.action;

/*
 * ActionListener.java
 *
 */

/**
 * The <code> ActionListener </code> interface.
 */
public interface ActionListener {

    /**
     * Invoked when an action occurs in a Checkable MenuItem ( Knife, Gun e.t.c ).
     */
    void actionPerformed();

    /**
     * Invoked when an action occurs in a Checkable ( Knife, Gun e.t.c ) and
     * non Checkable MenuItem , primarily a display action regarding user interaction( Health, Ammo e.t.c ).
     */
    void actionSelected();

    /**
     * Invoked when an action occurs in a Checkable ( Knife, Gun e.t.c ) and
     * non Checkable MenuItem , primarily to stop action displaying in user interface( Health, Ammo e.t.c ).
     */
    void actionDeselected();
}
