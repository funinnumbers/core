package eu.funinnumbers.engine.ui.support;

import javax.swing.table.AbstractTableModel;
import java.awt.*;

/**
 * My Table Model class.
 */
public class MyTableModel extends AbstractTableModel {

    /**
     * Serial Version UID.
     */
    public static final long serialVersionUID = 42L;

    /**
     * Table Column names.
     */
    private final String[] columnNames = {"ID", "NAME", "IP", "Location", "Coordinates", "Led ID"};

    /**
     * Data Objects.
     */
    private final Object[][] data = {
            {"Mary", new Color(153, 0, 153),
                    "Snowboarding", new Integer(5), Boolean.FALSE},
            {"Alison", new Color(51, 51, 153),
                    "Rowing", new Integer(3), Boolean.TRUE},
            {"Kathy", new Color(51, 102, 51),
                    "Knitting", new Integer(2), Boolean.FALSE},
            {"Sharon", Color.red,
                    "Speed reading", new Integer(20), Boolean.TRUE},
            {"Philip", Color.pink,
                    "Pool", new Integer(10), Boolean.FALSE},
            {"Philip", Color.pink,
                    "Pool", new Integer(10), Boolean.FALSE}
    };

    /**
     * Get Number of columns.
     *
     * @return column number
     */
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Get Row of columns.
     *
     * @return row number
     */
    public int getRowCount() {
        return data.length;
    }

    /**
     * Get Column Name.
     *
     * @param col the column
     * @return the Name
     */
    public String getColumnName(final int col) {
        return columnNames[col];
    }

    /**
     * Get value at specific row, column.
     *
     * @param row the row
     * @param col the column
     * @return Value at [row][column]
     */
    public Object getValueAt(final int row, final int col) {
        return data[row][col];
    }
}
