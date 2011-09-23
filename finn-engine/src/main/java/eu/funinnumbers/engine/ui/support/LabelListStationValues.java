package eu.funinnumbers.engine.ui.support;

import eu.funinnumbers.db.managers.BattleEngineManager;
import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.db.util.HibernateUtil;
import eu.funinnumbers.engine.util.IPFinder;
import org.hibernate.Transaction;

import javax.swing.*;
import java.awt.*;

/**
 * Label List Station Values Class.
 */
public class LabelListStationValues extends JList {

    /**
     * Serial Version UID.
     */
    public static final long serialVersionUID = 42L;

    /**
     * Discover local IP.
     */
    private static final String MYIP = new IPFinder("http://em1server.cti.gr/IP.php").getMyIP();

    /**
     * Start transaction with DB via Hibernate.
     */
    private final Transaction trans = HibernateUtil.getInstance().getSession().beginTransaction(); //NOPMD

    /**
     * Get Battle Engine from Database based on myIP that already was discovered.
     */
    private static final BattleEngine BATTLEENGINE = BattleEngineManager.getInstance().getByIp(MYIP); //NOPMD

    /**
     * Station Fields.
     */
    private static final String[] FIELDS = {"ID", "NAME", "IP", "Location", "Coordinates", "Led ID"};

    /**
     * String data.
     */
    private static String[][] data; // = {"ID", "NAME", "IP", "Location", "Coordinates", "Led ID"};

    /**
     * JTable title.
     */
    private static final JTable TITILELBL = new JTable(data, FIELDS);

    /**
     * Object array with data.
     */
    private static final Object[] LISTDATA = {TITILELBL};

    /**
     * Default font.
     */
    private static final Font FONT = new Font("Century Gothic", Font.PLAIN, 14);

    /**
     * Selected Color.
     */
    private final Color selectedColor = new Color(63, 255, 63); //NOPMD

    /**
     * A JList.
     */
    private final JList list;

    /**
     * Default Constructor.
     */
    public LabelListStationValues() {
        super(LISTDATA);
        list = this;
        this.setCellRenderer(new CustomCellRenderer());
        this.setBackground(Color.WHITE);
        //this.setSize(new Dimension(200,120));
        this.validate();
        this.setVisible(true);
    }

    /**
     * Rerurn the JList.
     *
     * @return a JList object
     */
    public final JList getList() {
        return list;
    }

    /**
     * Custom Cell Renderer.
     */
    static class CustomCellRenderer implements ListCellRenderer {
        /**
         * Get List Cell Renderer Component.
         *
         * @param list         a JList object
         * @param value        an Object with value
         * @param index        an integer with the index
         * @param isSelected   a boolean variable
         * @param cellHasFocus a boolean variable
         * @return the component
         */
        public Component getListCellRendererComponent(final JList list, final Object value, final int index,
                                                      final boolean isSelected, final boolean cellHasFocus) {
            final JTable component = (JTable) value;
            component.setOpaque(true);
            component.setFont(FONT);
            final int thickness = 1;
            component.setBorder(BorderFactory.createLineBorder(Color.BLACK, thickness));
            //component.setHorizontalAlignment(JLabel.LEFT);

            component.setBackground(Color.white);
            component.setForeground(Color.blue);


            return component;
        }
    }

}
