package eu.funinnumbers.engine.ui.support;

import javax.swing.*;
import java.awt.*;

/**
 * List all Engines.
 */
public class LabelListEngine extends JList {

    /**
     * Serial Version UID.
     */
    public static final long serialVersionUID = 42L;

    /**
     * Static Info Jlabel.
     */
    private static final JLabel TITLELBL = new JLabel("<html><b>Info</b></html>");

    /**
     * Static IP Jlabel.
     */
    private static final JLabel IPADDRESS = new JLabel("IP:");

    /**
     * Static Name Jlabel.
     */
    private static final JLabel NAME = new JLabel("Name:");

    /**
     * Static Max Players Jlabel.
     */
    private static final JLabel MAX_P = new JLabel("Max Players:");

    /**
     * Static Status Jlabel.
     */
    private static final JLabel STATUS = new JLabel("Status:");

    /**
     * Static Start Time Jlabel.
     */
    private static final JLabel START = new JLabel("Start Time:");

    /**
     * Static Finish Time Jlabel.
     */
    private static final JLabel FINISH = new JLabel("Finish Time:");

    /**
     * Static Truce Period Jlabel.
     */
    private static final JLabel TRUCE_PERIOD = new JLabel("Truce Period:");

    /**
     * Static Location Jlabel.
     */
    private static final JLabel LOCATION = new JLabel("Location:");

    /**
     * Static Coordinates Jlabel.
     */
    private static final JLabel COORD = new JLabel("Coordinates:");

    /**
     * Static aliveness Jlabel.
     */
    private static final JLabel ALIVENESS = new JLabel("Is Alive:");

    /**
     * An Array with Jlabels.
     */
    private static final Object[] LISTDATA = {TITLELBL, IPADDRESS, NAME, MAX_P, STATUS, START, FINISH, TRUCE_PERIOD,
            LOCATION, COORD, ALIVENESS};
    /**
     * The default font.
     */
    private static final Font FONT = new Font("Century Gothic", Font.PLAIN, 14);

    /**
     * The selected Color.
     */
    private final Color selectedColor = new Color(63, 255, 63); //NOPMD

    /**
     * A Jlist.
     */
    private final JList list; //NOPMD

    /**
     * LabelListEngine Constructor.
     */
    public LabelListEngine() {
        super(LISTDATA);
        list = this;
        this.setCellRenderer(new CustomCellRenderer());
        this.setBackground(Color.WHITE);
        //this.setSize(new Dimension(200,120));
        this.validate();
        this.setVisible(true);
    }

    /**
     * CustomCellRenderer Class.
     */
    static class CustomCellRenderer implements ListCellRenderer {

        /**
         * Return List Cell Renderer Component.
         *
         * @param list         a JList object
         * @param value        An Object with a value
         * @param index        int for index
         * @param isSelected   boolean variable
         * @param cellHasFocus boolean variable
         * @return the component object
         */
        public Component getListCellRendererComponent(final JList list, final Object value,
                                                      final int index, final boolean isSelected,
                                                      final boolean cellHasFocus) {
            final JLabel component = (JLabel) value;
            component.setOpaque(true);
            component.setFont(FONT);
            final int thickness = 1;
            component.setBorder(BorderFactory.createLineBorder(Color.BLACK, thickness));
            component.setHorizontalAlignment(JLabel.RIGHT);

            component.setBackground(Color.white);
            component.setForeground(Color.black);


            return component;
        }
    }
}
