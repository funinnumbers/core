package eu.funinnumbers.engine.ui.support;

import javax.swing.*;
import java.awt.*;

/**
 * My Renderer class.
 */
class MyRenderer extends JLabel implements ListCellRenderer {

    /**
     * Serial Version UID.
     */
    public static final long serialVersionUID = 42L;

    /**
     * Default constructor.
     */
    public MyRenderer() {
        super();
        setOpaque(true);
    }

    /**
     * Get List Cell Renderer Component.
     *
     * @param list         a JList
     * @param value        an Object
     * @param index        an Integer
     * @param isSelected   a Boolean
     * @param cellHasFocus a Boolean
     * @return the Cell Component
     */
    public Component getListCellRendererComponent(final JList list,
                                                  final Object value,
                                                  final int index,
                                                  final boolean isSelected,
                                                  final boolean cellHasFocus) {

        setText(value.toString());

        final Color background;
        final Color foreground;
        /*
       // check if this cell represents the current DnD drop location
       JList.DropLocation dropLocation = list.getDropLocation();
       if (dropLocation != null
               && !dropLocation.isInsert()
               && dropLocation.getIndex() == index) {

           background = Color.BLUE;
           foreground = Color.WHITE;
       } else*/

        // check if this cell is selected
        if (isSelected) {
            background = Color.RED;
            foreground = Color.WHITE;

            // unselected, and not the DnD drop location
        } else {
            background = Color.WHITE;
            foreground = Color.BLACK;
        }


        setBackground(background);
        setForeground(foreground);

        return this;
    }
}
