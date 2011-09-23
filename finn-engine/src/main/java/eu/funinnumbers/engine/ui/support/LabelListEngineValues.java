package eu.funinnumbers.engine.ui.support;

import eu.funinnumbers.db.managers.BattleEngineManager;
import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.db.util.HibernateUtil;
import eu.funinnumbers.engine.util.IPFinder;
import org.hibernate.Transaction;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.util.Date;

/**
 * A JList with Engine's values.
 */
public class LabelListEngineValues extends JList {

    /**
     * Sereial Version UID.
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
    private static final BattleEngine BATTLEENGINE = BattleEngineManager.getInstance().getByIp(MYIP);

    /**
     * Title JLable.
     */
    private static final JLabel TITLELBL = new JLabel("<html><FONT color=\"black\"><b>Values</b></FONT></html>");

    /**
     * Ip Address Jlabel.
     */
    private static final JLabel IPADDRESS = new JLabel(MYIP);

    /**
     * Name Jlabel.
     */
    private static final JLabel NAME = new JLabel(BATTLEENGINE.getName());

    /**
     * Max Players Jlabel.
     */
    private static final JLabel MAX_P = new JLabel(Integer.toString(BATTLEENGINE.getMaxPlayers()));

    /**
     * Status Jlabel.
     */
    private static final JLabel STATUS = new JLabel(BATTLEENGINE.getStatus());

    /**
     * Date format.
     */
    private static DateFormat formatter; //NOPMD

    /**
     * Finish Time.
     */
    private static final Date ENGFNSHTIME = BATTLEENGINE.getFinishTime();

    /**
     * Start Time.
     */
    private static final Date ENGSTRTTIME = BATTLEENGINE.getStartTime();

    /**
     * Start time JLabel.
     */
    private static final JLabel START = new JLabel("" + ENGSTRTTIME); //NOPMD

    /**
     * Finish time JLabel.
     */
    private static final JLabel FINISH = new JLabel("" + ENGFNSHTIME); //NOPMD

    /**
     * Truce period JLabel.
     */
    private static final JLabel TRUCE_PERIOD = new JLabel(Integer.toString(BATTLEENGINE.getTrucePeriod()));

    /**
     * Location JLabel.
     */
    private static final JLabel LOCATION = new JLabel(BATTLEENGINE.getLocation());

    /**
     * Coordination JLabel.
     */
    private static final JLabel COORD = new JLabel(BATTLEENGINE.getCoordinates());

    /**
     * Aliveness JLabel.
     */
    private static final JLabel ALIVENESS = new JLabel(Boolean.toString(BATTLEENGINE.getIsAlive()));

    /**
     * List Data object array.
     */
    private static final Object[] LISTDATA = {TITLELBL, IPADDRESS, NAME, MAX_P, STATUS, START, FINISH,
            TRUCE_PERIOD, LOCATION, COORD, ALIVENESS};

    /**
     * Default font.
     */
    private static final Font FONT = new Font("Century Gothic", Font.PLAIN, 14);

    /**
     * Selected Color.
     */
    private final Color selectedColor = new Color(63, 255, 63);

    /**
     * A JList.
     */
    private final JList list;

    /**
     * Default Constructor.
     */
    public LabelListEngineValues() {
        super(LISTDATA);
        list = this;
        this.setCellRenderer(new CustomCellRenderer());
        this.setBackground(Color.WHITE);
        //this.setSize(new Dimension(200,120));
        this.validate();
        this.setVisible(true);
    }

    /**
     * Get Selected Color.
     *
     * @return the Selected Color
     */
    public Color getSelectedColor() {
        return selectedColor;
    }

    /**
     * Get List.
     *
     * @return the List
     */
    public JList getList() {
        return list;
    }

    /**
     * Custom Cell Renderer.
     */
    static class CustomCellRenderer implements ListCellRenderer {
        /**
         * Returns List Cell Renderer Component.
         *
         * @param list         Jlist
         * @param value        a value Object
         * @param index        int with the index
         * @param isSelected   boolean variable
         * @param cellHasFocus boolean variable
         * @return the component
         */
        public Component getListCellRendererComponent(final JList list, final Object value, final int index,
                                                      final boolean isSelected, final boolean cellHasFocus) {
            final JLabel component = (JLabel) value;
            component.setOpaque(true);
            component.setFont(FONT);
            final int thickness = 1;
            component.setBorder(BorderFactory.createLineBorder(Color.BLACK, thickness));
            component.setHorizontalAlignment(JLabel.LEFT);

            component.setBackground(Color.white);
            component.setForeground(Color.blue);


            return component;
        }
    }

}
