package eu.funinnumbers.engine.ui;


import eu.funinnumbers.db.managers.BattleEngineManager;
import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.db.util.HibernateUtil;
import eu.funinnumbers.engine.ui.panels.*;
import eu.funinnumbers.engine.util.IPFinder;
import org.hibernate.Transaction;
import eu.funinnumbers.util.Logger;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;


/**
 * This is the main window of the eu.funinnumbers.engine.
 */
public class EngineFrame extends JFrame {

    /**
     * Serial Version UID.
     */
    public static final long serialVersionUID = 42L;

    /**
     * Default Constructor.
     */
    public EngineFrame() {
        /********************Engine  Interface*********************/
        super("Engine Main Application");
        // Discover local IP
        final String myIP = new IPFinder("http://em1server.cti.gr/IP.php").getMyIP();
        final BattleEngine battleEngine;
        //Start transaction with DB via Hibernate
        final Transaction trans = HibernateUtil.getInstance().getSession().beginTransaction();
        //Get Battle Engine from Database based on myIP that already was discovered
        battleEngine = BattleEngineManager.getInstance().getByIp(myIP);
        //= battleEngine.getStations();
        Logger.getInstance().debug("BattleEngine ID=" + battleEngine.getId() + ", Name=" + battleEngine.getName());
        final EnginePanel engine = new EnginePanel(battleEngine, myIP);
        final StationPanel station = new eu.funinnumbers.engine.ui.panels.StationPanel(battleEngine);
        final PlayersPanel players = new PlayersPanel(battleEngine);
        final EventsPanel events = new EventsPanel(battleEngine);
        final CreditsPanel credits = new CreditsPanel();

        // Create the tabbed pane
        final JTabbedPane tabs = new JTabbedPane();

        // Add tabs
        tabs.addTab("Engine", engine);
        tabs.addTab("Stations", station);
        tabs.addTab("Players", players);
        tabs.addTab("Events", events);
        tabs.addTab("Credits", credits);
        setContentPane(tabs);


        // Finalize DB transaction
        trans.commit();
        /*********************Engine Interface**********************/
        // Start Engine
        /* try {
            startEngine(myIP);
        } catch (Exception ex) {
            Logger.getInstance().debug("Cannot start eu.funinnumbers.engine", ex);
        }*/

    }

    /**
     * Start Engine.
     *
     * @param myIP IP address.
     * @throws RemoteException Throws Remote Exception
     */
    public void startEngine(final String myIP) throws RemoteException {
        // Get Environment Property
        //final String engineAddr =
        //      System.getProperty("java.rmi.server.hostname");
        //Logger.getInstance().debug("Engine Address: " + engineAddr);

        // Initialize the MonkEngine
/*       final FGEngineApp eapp = new MonkEngine(myIP);

        // Start the FGEngineRMIImpl
        eapp.startApp();

        // Start the Stations
        eapp.startStations();
         // Retrieve list of Stations

        //for (final Iterator StationIterator = StationsList.iterator(); StationIterator.hasNext();) {
          //  final Station eu.funinnumbers.station = (Station) StationIterator.next();
           // Logger.getInstance().debug("Station ID=" + eu.funinnumbers.station.getID());
            //String StatIP = eu.funinnumbers.station.getIP();
        //   SSHConnection sshStation = new SSHConnection();
         //  sshStation.setStationIP(StatIP);
         //  sshStation.startSSHConnection();


       // }



       // This thread should sleep
       while (true) {
         try {
               Thread.sleep(eapp.THREAD_SLEEP);
             } catch (Exception e) {
               e.notifyAll();
            }
        } */
    }

    /**
     * Classic main.
     *
     * @param args Input Args
     */
    public static void main(final String[] args) {

        final JFrame frame = new EngineFrame();
        final int magic1 = 200;
        final int magic2 = 450;
        final int magic3 = 380;

        frame.setBounds(magic1, magic1, magic2, magic3);
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosed(final WindowEvent event) {
                Logger.getInstance().debug("Shutting down...");
                System.exit(0);
            }
        });
    }
}
