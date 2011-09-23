package eu.funinnumbers.engine.ui.panels;

import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.util.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

/**
 * The main panel that displays the eu.funinnumbers.engine parameters.
 */
public class EnginePanel extends JPanel {

    /**
     * Serial Version UID.
     */
    public static final long serialVersionUID = 42L;

    /**
     * Default constructor.
     *
     * @param battleEngine the information of the eu.funinnumbers.engine.
     * @param myIP         the IP of the eu.funinnumbers.engine.
     */
    public EnginePanel(final BattleEngine battleEngine, final String myIP) { //NOPMD
        super();
        //border layout
        final BorderLayout myLayout = new BorderLayout();
        this.setLayout(myLayout);

        final JLabel jLabel1 = new JLabel();
        final int fontSize = 14;
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, fontSize));
        jLabel1.setText("Battle Engine Information ");

        final JPanel panelUp = new JPanel();
        panelUp.add(jLabel1, BorderLayout.CENTER);
        this.add(panelUp, BorderLayout.NORTH);

        final JPanel attribList = new JPanel();
        attribList.add(new eu.funinnumbers.engine.ui.support.LabelListEngine(), BorderLayout.LINE_START);

        //Center:Left List of info velues
        final eu.funinnumbers.engine.ui.support.LabelListEngineValues engList = new eu.funinnumbers.engine.ui.support.LabelListEngineValues();

        final JPanel panelCenter = new JPanel();
        panelCenter.add(attribList, BorderLayout.CENTER);
        panelCenter.add(engList, BorderLayout.CENTER);
        this.add(panelCenter, BorderLayout.CENTER);

        final JLabel jLabel2 = new JLabel();
        jLabel2.setFont(new java.awt.Font("Tahoma", 1, fontSize));

        final JPanel panelDown = new JPanel();
        panelDown.add(jLabel2, BorderLayout.CENTER);
        this.add(panelDown, BorderLayout.SOUTH);
    }


    /**
     * Determines if the eu.funinnumbers.engine has started or finished.
     *
     * @param engFnshTime the finish time of the eu.funinnumbers.engine.
     * @param engStrtTime the start time of the eu.funinnumbers.engine.
     * @return a String with time stats
     */
    public String determineStatus(final Date engFnshTime, final Date engStrtTime) {
        //find today's date
        //and print statistics for battle
        String timestats;
        final Date date = new Date();
        if (date.after(engFnshTime)) {
            Logger.getInstance().debug("The battle has  finished");
            timestats = "The battle has finished!";
        } else if (date.after(engStrtTime)) {
            Logger.getInstance().debug("The battle has  already started");
            timestats = "The battle has already started!";
        } else {
            Logger.getInstance().debug("The battle will start at " + engStrtTime);
            timestats = "The battle will start at " + engStrtTime;
        }
        return timestats;
    }
}
