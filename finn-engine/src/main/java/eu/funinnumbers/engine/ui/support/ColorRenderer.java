package eu.funinnumbers.engine.ui.support;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import java.awt.*;


/**
 * Color Rendere Class.
 */
public class ColorRenderer extends JLabel implements TableCellRenderer { //NOPMD
    /**
     * The Serial Version UID.
     */
    public static final long serialVersionUID = 42L;

    /**
     * Unselected Border.
     */
    private Border unselectedBorder = null;

    /**
     * Selected Border.
     */
    private Border selectedBorder = null;

    /**
     * Boolean for borders. True with borders.
     */
    private boolean isBordered = true; //NOPMD

    /**
     * ColorRendere constructor.
     *
     * @param isBorderedP boolean variable
     */
    public ColorRenderer(final boolean isBorderedP) {
        super();
        this.isBordered = isBorderedP;
        setOpaque(true); //MUST do this for background to show up.
    }

    /**
     * Return Table Cell Renderer Component.
     *
     * @param table      JTable object
     * @param color      color Object
     * @param isSelected boolean variable
     * @param hasFocus   boolean variable
     * @param row        int for row
     * @param column     int for column
     * @return the component
     */
    public final Component getTableCellRendererComponent( //NOPMD
                                                          final JTable table, final Object color,
                                                          final boolean isSelected, final boolean hasFocus,
                                                          final int row, final int column) {
        final Color newColor = (Color) color;
        setBackground(newColor);
        final int borderX = 2;
        final int borderY = 5;
        if (isBordered) {
            if (isSelected) {
                if (selectedBorder == null) {

                    selectedBorder = BorderFactory.createMatteBorder(borderX, borderY, borderX, borderY,
                            table.getSelectionBackground());
                }
                setBorder(selectedBorder);
            } else {
                if (unselectedBorder == null) {
                    unselectedBorder = BorderFactory.createMatteBorder(borderX, borderY, borderX, borderY,
                            table.getBackground());
                }
                setBorder(unselectedBorder);
            }
        }

        setToolTipText("RGB value: " + newColor.getRed() + ", "
                + newColor.getGreen() + ", "
                + newColor.getBlue());
        return this;
    }
}
