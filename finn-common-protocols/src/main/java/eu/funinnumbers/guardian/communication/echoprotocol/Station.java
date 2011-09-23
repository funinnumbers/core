package eu.funinnumbers.guardian.communication.echoprotocol;

import eu.funinnumbers.util.Logger;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import java.io.IOException;
import java.util.Date;

/**
 * Represents the Battle Station to which the device is connected.
 */
public final class Station extends eu.funinnumbers.db.model.Station {

    /**
     * The Datagram Connection used to communicate with the Station.
     */
    private DatagramConnection dgConnection = null;

    /**
     * Last beacon's timestamp.
     */
    private Date lastAlive = null;

    /**
     * Indicates that a one-way connection to eu.funinnumbers.station exists.
     */
    private boolean semiConnection = false;

    /**
     * Indicates if Station operates as a mobile eu.funinnumbers.station.
     */
    private boolean mobileStation = false;

    /**
     * Creates a new instance of Station.
     */
    public Station() {
        super();

        // Initially there is no eu.funinnumbers.station connection
        setAddress(new Long(Broadcaster.TOKEN_NOSTATION));

        // The initial Alive timestamp (0)
        setLastAlive(new Date(0));
    }

    /**
     * Default Station Constructor.
     *
     * @param superStation superclasse's instance
     */
    public Station(final eu.funinnumbers.db.model.Station superStation) {
        super(superStation.getName(),
                superStation.getStationId(),
                superStation.getIpAddr(),
                superStation.getAddress(),
                superStation.getLocation(),
                superStation.getCoordinates(),
                superStation.getLEDId(),
                superStation.getBattleEngine());

        // Initially there is no eu.funinnumbers.station connection
        setAddress(new Long(Broadcaster.TOKEN_NOSTATION));

        // The initial Alive timestamp (0)
        setLastAlive(new Date(0));
    }

    /**
     * Establishes a connection to the Battle Station
     * as long as such a eu.funinnumbers.station really exists.
     * isActive()==true
     */
    public void createConnection() {
        if (this.isActive()) {
            try {
                // The Connection is a broadcast so we specify it in the creation string
                dgConnection = (DatagramConnection) Connector.open("radiogram://"
                        + this.getAddress() + ":"
                        + EchoProtocolManager.ECHOPORT);
                // Then, we ask for a datagram with the maximum size allowed
                final Datagram datagram = dgConnection.newDatagram(dgConnection.getMaximumLength());

                datagram.reset();
                datagram.writeUTF("accept");
                dgConnection.send(datagram);
                dgConnection.close();
            } catch (IOException ex) {
                destroyConnection();
                Logger.getInstance().debug("Could not open radiogram connection", ex);
            }

        }
    }

    /**
     * Closes the connection to the Battle Station
     * and changes the myStation address.
     */
    public void destroyConnection() {
        synchronized (Station.class) {
            this.setAddress(new Long(Broadcaster.TOKEN_NOSTATION));
            if (dgConnection != null) {
                try {
                    dgConnection.close();

                } catch (IOException ex) {
                    Logger.getInstance().debug("Could not close radiogram connection", ex);
                }
            }
        }
    }

    /**
     * Evaluates wheather this eu.funinnumbers.guardian is connected to a eu.funinnumbers.station or not.
     *
     * @return true if a connection to some eu.funinnumbers.station is present
     *         false otherwise
     */
    public boolean isActive() {
        synchronized (Station.class) {
            return ((int) this.getAddress().longValue() != Broadcaster.TOKEN_NOSTATION);
        }
    }

    /**
     * @return true if the device is connected to a mobile eu.funinnumbers.station
     *         false otherwise.
     */
    public boolean isMobileStation() {
        synchronized (Station.class) {
            return this.mobileStation;
        }
    }

    /**
     * @param isMobStation true if the eu.funinnumbers.station is a mobile eu.funinnumbers.station
     */
    public void setMobileStation(final boolean isMobStation) {
        this.mobileStation = isMobStation;
    }

    /**
     * @return the last known alive stamp
     */
    public Date getLastAlive() {
        return lastAlive;
    }

    /**
     * Sets the last known time stamp of the Station.
     *
     * @param alive the timestamp
     */
    public void setLastAlive(final Date alive) {
        this.lastAlive = alive;
    }

    /**
     * Sets the the last known timestamp to current date.
     */
    public void setLastAlive() {
        this.lastAlive = new Date();
    }

    /**
     * @return true if a one-way connection exists, otherwise false.
     */
    public boolean isSemiStation() {
        return this.semiConnection;
    }

    /**
     * @param semiConn true if a one-way connection exists,otherwise false.
     */
    public void setSemiStation(final boolean semiConn) {
        this.semiConnection = semiConn;
    }


}

