package eu.funinnumbers.hyperengine;

import eu.funinnumbers.engine.util.GenericRMIServer;
import eu.funinnumbers.hyperengine.rmi.HyperEngineInterface;
import eu.funinnumbers.hyperengine.rmi.HyperEngineRMIImpl;
import eu.funinnumbers.util.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: marlog
 * Date: May 4, 2010
 * Time: 6:53:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class HyperEngineApp {


    /**
     * HyperEngine's IP address.
     */
    private String myIP;


    /**
     * Default Constructor.
     */
    public HyperEngineApp() {

        Logger.getInstance().debug("Starting HyperEngine on " + System.getProperty("java.rmi.server.hostname")); //NOPMD

        // Set HyperEngine IP
        setMyIP(System.getProperty("java.rmi.server.hostname"));


        try {
            // Start RMI HyperEngine Interface
            final HyperEngineRMIImpl hyperengine = new HyperEngineRMIImpl();


            // Register RMI Interface offered to Stations
            GenericRMIServer.getInstance().registerInterface(HyperEngineInterface.RMI_NAME, hyperengine);


        } catch (Exception ex) {
            Logger.getInstance().debug("Unable to register RMI interface", ex);
        }

        Logger.getInstance().debug("Engine up");


        // Start the HyperEngineLogic
        HyperEngineLogic.getInstance();

        // Start UI
        //new HFrame();        

        Logger.getInstance().debug("Hyper Engine started");
    }

    /**
     * Returns HyperEngine's IP address.
     *
     * @return String with HyperEngine's ip address
     */
    public String getMyIP() {
        return myIP;
    }

    /**
     * Set HyperEngine's IP address.
     *
     * @param ipAddress String with the IP address
     */
    public void setMyIP(final String ipAddress) {
        this.myIP = ipAddress;
    }


    /**
     * Main function.
     *
     * @param args String Arguments
     */
    public static void main(final String[] args) {
        new HyperEngineApp();
    }

}
