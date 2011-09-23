package eu.funinnumbers.engine.ui.support;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Table Station Values class.
 */
public class TableStationValues extends JTable {

    /**
     * Version for Serialization.
     */
    static final long serialVersionUID = 42L;

    /**
     * Default Constructor.
     */
    public TableStationValues() {
        super();
        final Object[][] rows = {{"one", "1"},
                {"two", "2"}, {"three", "3"},
                {"four", "4"}, {"five", "5"},
                {"six", "6"}, {"seven", "7"},
                {"eight", "8"}, {"nine", "9"},
                {"ten", "10"}};
        final Object[] headers = {"Numbers", "No"};
        final JTable table = new JTable(rows, headers); //NOPMD
        //TableCellRenderer renderer = new MyTableRenderer();
        //table.setDefaultRenderer(Object.class, renderer);
        //final JScrollPane scrollPane = new JScrollPane(table);
    }

    /**
     * My Table Renderer Class.
     */
    static class MyTableRenderer implements TableCellRenderer {

        /**
         * Default Table Cell Renderer.
         */
        public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

        /**
         * Creates a table Cell Renderer Component.
         *
         * @param table      a JTbale Object
         * @param value      an Object with a value
         * @param isSelected a boolean variable
         * @param hasFocus   a boolean variable
         * @param row        an integer with row number
         * @param column     an integer with column number
         * @return the component
         */
        public Component getTableCellRendererComponent(final JTable table, final Object value,
                                                       final boolean isSelected, final boolean hasFocus,
                                                       final int row, final int column) {
            final Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            ((JLabel) renderer).setOpaque(true);
            final Color foreground, background;
            if (isSelected) {
                foreground = Color.yellow;
                background = Color.green;
            } else {
                foreground = Color.white;
                background = Color.blue;
            }
            renderer.setForeground(foreground);
            renderer.setBackground(background);
            return renderer;
        }
    }
}
