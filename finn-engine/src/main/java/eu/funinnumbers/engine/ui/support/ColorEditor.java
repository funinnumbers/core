package eu.funinnumbers.engine.ui.support;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The ColorEditor Class.
 */
public class ColorEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    /**
     * Serial version UID.
     */
    public static final long serialVersionUID = 42L;

    /**
     * Current Color.
     */
    private Color currentColor;

    /**
     * JButton.
     */
    private final JButton button;

    /**
     * Color Chooser.
     */
    private final JColorChooser colorChooser;

    /**
     * JDialog.
     */
    private final JDialog dialog;

    /**
     * String EDIT.
     */
    private static final String EDIT = "edit";

    /**
     * ColorEditor Constructor.
     */
    public ColorEditor() {
        super();
        //Set up the editor (from the table's point of view),
        //which is a button.
        //This button brings up the color chooser dialog,
        //which is the editor from the user's point of view.
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);

        //Set up the dialog that the button brings up.
        colorChooser = new JColorChooser();
        dialog = JColorChooser.createDialog(button,
                "Pick a Color",
                true,  //modal
                colorChooser,
                this,  //OK button handler
                null); //no CANCEL button handler
    }

    /**
     * Handles events from the editor button and from the dialog's OK button.
     *
     * @param actionEv An action Event
     */
    public final void actionPerformed(final ActionEvent actionEv) {
        if (EDIT.equals(actionEv.getActionCommand())) {
            //The user has clicked the cell, so
            //bring up the dialog.
            button.setBackground(currentColor);
            colorChooser.setColor(currentColor);
            dialog.setVisible(true);

            //Make the renderer reappear.
            fireEditingStopped();

        } else { //User pressed dialog's "OK" button.
            currentColor = colorChooser.getColor();
        }
    }


    /**
     * Implement the one CellEditor method that AbstractCellEditor doesn't.
     *
     * @return the current color
     */
    public final Object getCellEditorValue() {
        return currentColor;
    }


    /**
     * Implement the one method defined by TableCellEditor.
     *
     * @param table      Jtable
     * @param value      a value object
     * @param isSelected boolean variable
     * @param row        an integer for row
     * @param column     an integer for column
     * @return the button
     */
    public final Component getTableCellEditorComponent(final JTable table,
                                                       final Object value,
                                                       final boolean isSelected,
                                                       final int row,
                                                       final int column) {
        currentColor = (Color) value;
        return button;
    }


}

