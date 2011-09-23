package eu.funinnumbers.engine.util;

import eu.funinnumbers.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Connects to central server and retrieves local IP.
 */
public class IPFinder {

    /**
     * Contains the IP of the central server.
     */
    private final String serverURL;

    /**
     * Default constructor that initialize the class to a given central server.
     *
     * @param remoteURL the IP of the central server.
     */
    public IPFinder(final String remoteURL) {
        serverURL = remoteURL;
    }

    /**
     * Connects to the central server to discover local IP.
     *
     * @return the IP of this machine.
     */
    public String getMyIP() {
        InputStream istr = null;
        String myIP;

        try {
            // Construct URL of server
            final URL url = new URL(serverURL);

            // Connect to Server
            istr = url.openStream();
            Logger.getInstance().debug("Successed to run php script");

            // Open Input Stream and retrieve first line
            final BufferedReader dis = new BufferedReader(new InputStreamReader(istr));

            myIP = dis.readLine();
            Logger.getInstance().debug("My IP:" + myIP);

        } catch (MalformedURLException mue) {
            Logger.getInstance().debug("Ouch - a MalformedURLException happened.", mue);
            myIP = "unknown";

        } catch (IOException ioe) {
            Logger.getInstance().debug("Oops- an IOException happened.", ioe);
            myIP = "unknown";

        } finally {
            try {
                istr.close();
            } catch (IOException ioe) {
                Logger.getInstance().debug("Oops- an IOException happened.", ioe);
            }
        }

        return myIP;
    }


    /**
     * A simple test.
     *
     * @param args ignored.
     */
    public static void main(final String[] args) {
        Logger.getInstance().debug(new IPFinder("http://em1server.cti.gr/IP.php").getMyIP());
    }

}
