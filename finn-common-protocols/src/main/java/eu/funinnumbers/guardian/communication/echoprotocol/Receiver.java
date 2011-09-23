/**
 * The Receiver Thread of Echo Protocol.
 * Seperates the received {eu.funinnumbers.guardian|eu.funinnumbers.station}
 * messages and invokes EchoProtocolManager's
 * methods to handle them respectevly
 */
package eu.funinnumbers.guardian.communication.echoprotocol;


import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.util.IEEEAddress;
import eu.funinnumbers.db.model.Guardian;
/*logloc_STATION_ONLY
import eu.funinnumbers.station.communication.localization.LocLogger;
import com.sun.spot.io.j2me.radiogram.Radiogram;
logloc_STATION_ONLY*/
import eu.funinnumbers.guardian.util.HashMap;
import eu.funinnumbers.util.Logger;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import java.io.IOException;


/**
 * The Receiver thread is constantly listening for
 * incoming messages on port [ECHOPORT].
 */
public class Receiver extends Thread { //NOPMD

    /**
     * The Receiver Datagram Connection.
     */
    private DatagramConnection dgConnection = null;

    /**
     * The Receiver datagram.
     */
    private Datagram datagram = null;

    /**
     * Indicates if the devices running the EchoProtocol is
     * a Base Station.
     */
    private boolean iAmStation = false;

    private final HashMap rssiCalibration = new HashMap();

    private int rssCalib = 0;

    private boolean enableAdHoc = true;

    /**
     * Setup a listening server connection.
     */
    public final void setupConnection() {

        try {
            // Creates a Server Radiogram Connection on port 100
            dgConnection = (RadiogramConnection) Connector.open("radiogram://:"
                    + EchoProtocolManager.ECHOPORT);

            // Creates a Datagram using the above Connection
            datagram = dgConnection.newDatagram(dgConnection.getMaximumLength());

        } catch (IOException e) {
            Logger.getInstance().debug("Could not open radiogram receiver connection", e);
        }
    }

    /**
     * If this thread was constructed using a separate
     * <code>Runnable</code> run object, then that
     * <code>Runnable</code> object's <code>run</code> method is called;
     * otherwise, this method does nothing and returns.
     * <p/>
     * Subclasses of <code>Thread</code> should override this method.
     *
     * @see #start()
     */
    public final void run() { //NOPMD

        //Indicates if the device is a {eu.funinnumbers.guardian|eu.funinnumbers.station}
        int deviceIdentifier;

        // A eu.funinnumbers.guardian is connected to a <MAC address> or to "no eu.funinnumbers.station"
        // A eu.funinnumbers.station is connected to "no eu.funinnumbers.station" (proper eu.funinnumbers.station)
        //   or to "mobile eu.funinnumbers.station" (not a infrastucture eu.funinnumbers.station)
        Long connectedTo;

        setupConnection();

//        initializeRssiCalib();

        while (true) {
            try {
                // Clean the Datagram
                datagram.reset();

                // Receive from Datagram
                dgConnection.receive(datagram);
                // The Input String
                // Read device identifier: eu.funinnumbers.station or eu.funinnumbers.guardian
                deviceIdentifier = datagram.readInt();
/*LITE
                // Read the number of one-way neighbours of the sender
                final int totSemiNeighs = datagram.readInt();

                final HashMap semisHash = new HashMap(); //NOPMD
                semisHash.put("ts", "" + new Date().getTime()); //NOPMD
                //semisHash.put("ts", "" + System.currentTimeMillis());
                for (int neighIndex = 0; neighIndex < totSemiNeighs; neighIndex++) {
                    semisHash.put(datagram.readUTF(), null);

                }
LITE*/
                //Logger.getInstance().debug(tmp);

                // This is some else's broadcast
                if (IEEEAddress.toLong(datagram.getAddress()) != EchoProtocolManager.getInstance().getMyAddress().longValue()) {
                    // A eu.funinnumbers.guardian message
                    if (deviceIdentifier == Broadcaster.TOKEN_GUARDIAN) {

                        final Long stationAddr = new Long(datagram.readLong()); //NOPMD
                        final int id = datagram.readInt();
                        final int ledId = datagram.readInt();
                        final int initPhase = datagram.readInt();

                        // PROCESS ONLY IF NEIGHBOUR IS NOT A FELLOW PLAYER when enableAdHoc is disabled
                        if (enableAdHoc || iAmStation
                                || initPhase == Guardian.INIT_NODATA) {

                            final Guardian tmpNeighbour = new Guardian();  //NOPMD
                            tmpNeighbour.setAddress(new Long(IEEEAddress.toLong(datagram.getAddress()))); //NOPMD
                            tmpNeighbour.setStation(stationAddr.toString());
                            tmpNeighbour.setID(id);
                            tmpNeighbour.setLedId(ledId);
                            tmpNeighbour.setInitPhase(initPhase);
                            tmpNeighbour.setLastRssi(((Radiogram) datagram).getRssi());

                            /*logloc_STATION_ONLY
                             Radiogram radiogram = (Radiogram) datagram;
                             LocLogger.getInstance().logThis(radiogram.getRssi() + rssCalib, radiogram.getLinkQuality(),radiogram.getAddress(),tmpNeighbour.getID());

                             // Training mode logging (use with eu.funinnumbers.games.other.logloc only)
                             //LocLogger.getInstance().logThis(radiogram.getRssi() + rssCalib, radiogram.getLinkQuality(),radiogram.getAddress(),tmpNeighbour.getID());
                            logloc_STATION_ONLY*/

                            // This is not a eu.funinnumbers.station one-way Neighbour
//LITE                      EchoProtocolManager.getInstance().setSemisOfSemi(datagram.getAddress(), semisHash);

                            // Set the Neighbours last alive to "now"
                            tmpNeighbour.setLastAlive();

                            // EchoProtocolManager should handle this message properly
                            EchoProtocolManager.getInstance().updateNeighbour(tmpNeighbour);
                            //beacproc.addToGuardianQueue(tmpNeighbour);
                        }
                        // A eu.funinnumbers.station message
                    } else if (deviceIdentifier == Broadcaster.TOKEN_STATION && !iAmStation) {
                        final Station tmpStation = new Station(); //NOPMD
                        // //parseStationInfo(deviceIdentifier, datagram.getAddress());

                        // Used to distiquence an infrastructure eu.funinnumbers.station from a mobile one
                        connectedTo = new Long(datagram.readLong()); //NOPMD

                        // This is a mobile eu.funinnumbers.station.
                        if (!((int) connectedTo.longValue() == Broadcaster.TOKEN_NOSTATION)) { //NOPMD
                            // Make clear that this is a mobile eu.funinnumbers.station
                            tmpStation.setMobileStation(true);
                        }

                        tmpStation.setAddress(datagram.getAddress());

                        tmpStation.setStationId(datagram.readInt());
                        tmpStation.setLEDId(datagram.readInt());
                        datagram.readInt();

                        // This is a eu.funinnumbers.station semi-neighbour
/*LITE                   EchoProtocolManager.getInstance().setSemisOfSemi("" + tmpStation.getLEDId(), //NOPMD
                                semisHash); //NOPMD

LITE*/
                        // Set the Neighbours last alive to "now"
                        tmpStation.setLastAlive();

                        // EchoProtocolManager should handle this message properly
                        //Logger.getInstance().debug("Updating eu.funinnumbers.station with address " + tmpStation.getAddress());
                        EchoProtocolManager.getInstance().updateStation(tmpStation);
                        //beacproc.addToStationQueue(tmpStation);

                    }
                }

            } catch (IOException e) {
                Logger.getInstance().debug("Nothing received", e);
            }
        }

    }


    /**
     * Sets the mode of this device.
     *
     * @param isStation true if this is a Station, false if this is a eu.funinnumbers.guardian
     */
    public final void setMode(final boolean isStation) {
        iAmStation = isStation;
    }

    private final void initializeRssiCalib() {
        rssiCalibration.put("0014.4F01.0000.48FB", new Integer(7));
        rssiCalibration.put("0014.4F01.0000.4A76", new Integer(6));
        rssiCalibration.put("0014.4F01.0000.47A4", new Integer(12));
        rssiCalibration.put("0014.4F01.0000.491C", new Integer(6));
        rssiCalibration.put("0014.4F01.0000.46A8", new Integer(10));
        rssiCalibration.put("0014.4F01.0000.4C5D", new Integer(10));
        rssiCalibration.put("0014.4F01.0000.739A", new Integer(45));
        final String myAddress = IEEEAddress.toDottedHex(RadioFactory.getRadioPolicyManager().getIEEEAddress());
        Logger.getInstance().debug(myAddress);
        //Logger.getInstance().debug(rssiCalibration.containsKey(myAddress));
        if (rssiCalibration.containsKey(myAddress)) {
            rssCalib = ((Integer) rssiCalibration.get(myAddress)).intValue();
            //Logger.getInstance().debug(rssCalib);
        }
    }

    protected final void setEnableAdHoc(final boolean enableAdHoc) {
        this.enableAdHoc = enableAdHoc;

    }
}
