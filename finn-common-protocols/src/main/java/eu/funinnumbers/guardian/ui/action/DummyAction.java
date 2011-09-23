package eu.funinnumbers.guardian.ui.action;

/**
 * Does nothing.
 */
public class DummyAction extends AbstractAction {

    /**
     * Invoked when an action occurs in a Checkable MenuItem ( Knife, Gun e.t.c ).
     */
    public void actionPerformed() {
        // dummy
    }

    /**
     * Invoked when an action occurs in a Checkable ( Knife, Gun e.t.c ) and
     * non Checkable MenuItem , primarily a display action regarding user interaction( Health, Ammo e.t.c ).
     */
    public void actionSelected() {
        // dummy
    }

    /**
     * Invoked when an action occurs in a Checkable ( Knife, Gun e.t.c ) and
     * non Checkable MenuItem , primarily to stop action displaying in user interface( Health, Ammo e.t.c ).
     */
    public void actionDeselected() {
        // dummy
    }
}
