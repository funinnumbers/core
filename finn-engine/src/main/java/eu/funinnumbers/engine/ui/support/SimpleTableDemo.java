package eu.funinnumbers.engine.ui.support;

import eu.funinnumbers.util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Simple Table Demo Class.
 */
public class SimpleTableDemo extends JPanel {

    /**
     * Serial Version UID.
     */
    public static final long serialVersionUID = 42L;

    /**
     * Debug boolean variable. Initiazed to false.
     */
    private static final boolean DEBUG = false;


    /**
     * Default constructor.
     */
    public SimpleTableDemo() {
        super(new GridLayout(1, 0));

        /**
         * A String array with column names.
         */
        final String[] columnNames = {"First Name",
                "Last Name",
                "Sport",
                "# of Years",
                "Vegetarian"};

        /**
         * An object Array with data.
         */
        final Object[][] data = {
                {"Mary", "Campione",
                        "Snowboarding", new Integer(5), Boolean.FALSE},
                {"Alison", "Huml",
                        "Rowing", new Integer(3), Boolean.TRUE},
                {"Kathy", "Walrath",
                        "Knitting", new Integer(2), Boolean.FALSE},
                {"Sharon", "Zakhour",
                        "Speed reading", new Integer(20), Boolean.TRUE},
                {"Philip", "Milne",
                        "Pool", new Integer(10), Boolean.FALSE}
        };

        /**
         * The JTable with the column names and data.
         */
        final JTable table = new JTable(data, columnNames);
        final int xDim = 500;
        final int yDim = 70;
        table.setPreferredScrollableViewportSize(new Dimension(xDim, yDim));
        table.setFillsViewportHeight(true);

        if (DEBUG) {
            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(final MouseEvent mouseEvent) {
                    printDebugData(table);
                }
            });
        }

        //Create the scroll pane and add the table to it.
        final JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);
    }

    /**
     * Prints Debug data of a specific JTable.
     *
     * @param table a JTable object
     */
    private void printDebugData(final JTable table) {
        final int numRows = table.getRowCount();
        final int numCols = table.getColumnCount();
        final javax.swing.table.TableModel model = table.getModel();

        Logger.getInstance().debug("Value of data: ");
        for (int i = 0; i < numRows; i++) {
            Logger.getInstance().debug("    row " + i + ":");
            for (int j = 0; j < numCols; j++) {
                Logger.getInstance().debug("  " + model.getValueAt(i, j));
            }
            Logger.getInstance().debug("\n");
        }
        Logger.getInstance().debug("--------------------------");
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        final JFrame frame = new JFrame("SimpleTableDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        final SimpleTableDemo newContentPane = new SimpleTableDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Default Main function.
     *
     * @param args input arguments
     */
    public static void main(final String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
