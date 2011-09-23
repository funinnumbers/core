package eu.funinnumbers.engine.ui.panels;

import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.engine.ui.support.SimpleTableDemo;

import javax.swing.*;
import java.awt.*;


/**
 * Displays the eu.funinnumbers.station information.
 */
public class StationPanel extends JPanel {

    /**
     * Serial Version UID.
     */
    public static final long serialVersionUID = 42L;

    /**
     * Default constructor.
     *
     * @param battleEngine the eu.funinnumbers.engine object.
     */
    public StationPanel(final BattleEngine battleEngine) { //NOPMD
        super();
        //border layout
        final BorderLayout myLayout = new BorderLayout();
        this.setLayout(myLayout);

        final JLabel jLabel3 = new JLabel();
        final int fontSize = 14;
        jLabel3.setFont(new java.awt.Font("Tahoma", 1, fontSize));
        jLabel3.setText("Stations in Database for this battle");

        final JPanel stationUp = new JPanel();
        stationUp.add(jLabel3, BorderLayout.CENTER);
        this.add(stationUp, BorderLayout.NORTH);

        //Create and set up the content pane.
        final SimpleTableDemo newContentPane = new SimpleTableDemo();

        //TableStationValues StatTable = new TableStationValues();
        //JPanel StationCenter = new JPanel();
        //StationCenter.add(StatTable, BorderLayout.CENTER);
        this.add(newContentPane, BorderLayout.CENTER);

        /*java.eu.funinnumbers.util.Set<Station> Stationlist  = battleEngine.getStations();
   Logger.getInstance().debug("Tot Stations: " + Stationlist.size());
   int NoStations = Stationlist.size();
   String data[][]  =  new String[NoStations][6];
   int NoStation = 0;
   for (final Iterator StationIterator = Stationlist.iterator(); StationIterator.hasNext();) {
       Station eu.funinnumbers.station = (Station) StationIterator.next();
       Logger.getInstance().debug("Station ID=" + eu.funinnumbers.station.getStationId());
       int StatID = eu.funinnumbers.station.getStationId();
       String SID = Integer.toString(StatID);
       String StatName = eu.funinnumbers.station.getName();
       String StatIP = eu.funinnumbers.station.getIpAddr();
       String StatLoc =  eu.funinnumbers.station.getLocation();
       String StatCoord = eu.funinnumbers.station.getCoordinates();
       int StatLedID = eu.funinnumbers.station.getLEDId();
       String SLedID =  Integer.toString(StatLedID);
       data[NoStation][0] = SLedID;
       data[NoStation][1] = StatName;
       data[NoStation][2] = StatIP;
       data[NoStation][3] = StatLoc;
       data[NoStation][4] = StatCoord;
       data[NoStation][5] = SID;
       NoStation = NoStation + 1;
     }
   String fields[] = {"ID", "NAME", "IP", "Location", "Coordinates", "Led ID"};
   JTable StationsTable = new JTable(data,fields);
   this.add(StationsTable, BorderLayout.CENTER);
   final JPopupMenu Pmenu= new JPopupMenu();
   JMenuItem menuItem1 = new JMenuItem("Start Station");
   Pmenu.add(menuItem1);
   JMenuItem menuItem2 = new JMenuItem("Pause Station");
   Pmenu.add(menuItem2);
   JMenuItem menuItem3 = new JMenuItem("Kill Station");
   Pmenu.add(menuItem3);
   StationsTable.addMouseListener(new MouseAdapter() {
         //public void mouseReleased(MouseEvent me) {
          public void mouseClicked(MouseEvent e) {
             if (e.getButton() == MouseEvent.BUTTON1) {
               Logger.getInstance().debug("Mouse Left Click");
             }
             if (e.getButton() == MouseEvent.BUTTON3) {
               Logger.getInstance().debug("Mouse Right Click");
               Pmenu.show(e.getComponent(), e.getX(), e.getY());
             }
         }
       }); */

        final JButton startAll = new JButton("Start All");
        final JButton pauseAll = new JButton("Pause All");
        final JButton killAll = new JButton("Kill All");
        final JPanel koumpia = new JPanel();
        koumpia.add(startAll, BorderLayout.LINE_START);
        koumpia.add(pauseAll, BorderLayout.CENTER);
        koumpia.add(killAll, BorderLayout.LINE_END);
        this.add(koumpia, BorderLayout.SOUTH);

    }
}
