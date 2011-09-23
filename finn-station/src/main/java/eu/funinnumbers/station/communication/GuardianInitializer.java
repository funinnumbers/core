package eu.funinnumbers.station.communication;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import eu.funinnumbers.db.model.Avatar;
import eu.funinnumbers.db.model.BattleEngine;
import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.Station;
import eu.funinnumbers.db.model.StorableEntity;
import eu.funinnumbers.db.model.Team;
import eu.funinnumbers.guardian.communication.echoprotocol.EchoProtocolManager;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import eu.funinnumbers.station.FGStationApp;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observable;
import eu.funinnumbers.util.Observer;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Observes the EchoProtocol for incomming/outgoing guardians.
 * GuardianInitializer is also an observable object. It is observed only by the GuardianObserver class only
 * in the case that the data setting phase has finished and a new Motion Event has to be generated.
 * <p/>
 * Examines eu.funinnumbers.guardian and updates their jar if they are not properly initialized.
 * This is done by compiling from scratch the eu.funinnumbers.guardian code and performing remote deployment.
 * <p/>
 * IMPORTANT: Note that 2 basestations are needed for OTA deploy.
 * Depending on the number of basestations available, we may have to
 * limit initialization to a few or even to one basestation's range
 */
public class GuardianInitializer extends Observable implements Observer { //NOPMD

    /**
     * The port in which the termination message will be sent.
     */
    protected static final int REPLYPORT = 148;

    /**
     * The termination message.
     */
    protected static final String REPLY_TOKEN = "terminate";

    /**
     * The path of the <code>build.xml</code> matching the current game.
     */
    private final String buildXMLpath;

    /**
     * This is a Hash Map containing all guardians that have already been flashed.
     */
    private final HashMap<Long, Guardian> flashedGuardians = new HashMap<Long, Guardian>();

    /**
     * This Map is obtained by the BattleEngine and contains avatarID's with the guardianID as keys.
     */
    private final Map guardiansAvatarsMap; //NOPMD

    /**
     * This Map is obtained by the BattleEngine and contains TeamIDs with the avatarIDs as keys.
     */
    private final Map avatarsTeamsMap;

    /**
     * This Map contains all the avatars of the game, with tha avatarIDs as keys.
     */
    private final Map avatars;

    /**
     * This Map contains all the teams of the game, with tha teamIDs as keys.
     */
    private final Map teams;

    /**
     * This Map contains all the guardians of the game, with the eu.funinnumbers.guardian addresses as keys.
     */
    private final Map<Long, Guardian> guardians;

    /**
     * Indicates for which eu.funinnumbers.station this instance is running.
     */
    private final FGStationApp fgStation;

    /**
     * Indicates for which eu.funinnumbers.engine  this station is running.
     */
    private final BattleEngine engine; //NOPMD

    /**
     * Simple constructor that registers this class as observer to the EchoProtocol.
     *
     * @param station    this eu.funinnumbers.station's instance
     * @param fgStationP that runs this class
     */
    public GuardianInitializer(final Station station, final FGStationApp fgStationP) {
        super();
        // The GuardianInitilizier is running on this eu.funinnumbers.station
        this.fgStation = fgStationP;
        this.engine = station.getBattleEngine();

        this.buildXMLpath = station.getBattleEngine().getBuildXMLpath();
        EchoProtocolManager.getInstance().addObserver(this);

        // obtain the avatars HashMap from the BattleEngine
        avatars = convertSetToHashMap(station.getBattleEngine().getAvatars());

        // obtain the teams HashMap from the BattleEngine
        teams = convertSetToHashMap(station.getBattleEngine().getTeams());

        // obtain the guardians HashMap from the FGStationApp
        final Collection guardiansSet = fgStationP.getGuardians();
        guardians = new HashMap<Long, Guardian>(guardiansSet.size());

        // Associates every eu.funinnumbers.guardian based on its MAC Address
        final Iterator iter = guardiansSet.iterator();
        while (iter.hasNext()) {
            final Guardian entity = (Guardian) iter.next();
            guardians.put(entity.getAddress(), entity);
        }

        // Obtain the guardiansAvatarsMap HashMap from the FGStationApp
        guardiansAvatarsMap = fgStationP.getGuardiansAvatarsMap();

        // Obtain the avatarsTeamsMap from the AvatarManager
        avatarsTeamsMap = fgStationP.getAvatarsTeamsMap();

    }

    /**
     * Converts a Collection of StorableEntity objects to a HashMap.
     * The key of each entry is provided by the StorableEntity interface.
     *
     * @param inputSet the Collection of StorableEntity objects
     * @return the HashMap with the same entities indexed by entity ID
     */
    private HashMap convertSetToHashMap(final Collection inputSet) {
        final HashMap<Integer, StorableEntity> outputMap = new HashMap<Integer, StorableEntity>(inputSet.size());

        // Associates every object based on its ID
        final Iterator iter = inputSet.iterator();
        while (iter.hasNext()) {
            final StorableEntity entity = (StorableEntity) iter.next();
            outputMap.put(entity.getID(), entity); //NOPMD
            Logger.getInstance().debug(entity.getClass().getName()
                    + " -- ID=" + entity.getID() + " -- Value=" + entity.toString());
        }

        return outputMap;
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param obj the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     *            method.
     */
    public void update(final Observable obj, final Object arg) { //NOPMD
        Logger.getInstance().debug(" GuardianInitilizer received an update");

        // Ignore updates from other managers
        if (!(obj instanceof EchoProtocolManager)) {
            return;
        }

        // If we just heard a beacon from a nearby eu.funinnumbers.station -- ignore it
        if (!(arg instanceof Guardian)) {
            return;
        }

        // So this is a neighbouring spot
        final Guardian guardian = (Guardian) arg;
        // Check what kind of initialization we must perform
        switch (guardian.getInitPhase()) {
            case Guardian.INIT_NOJAR:
                /*
                initializeJAR(eu.funinnumbers.guardian);
                break;
                  */
            case Guardian.INIT_NODATA:
                if (guardian.isAlive()) {
                    initializeDATA(guardian);
                }
                break;

            default:
                // Guardian fully initialized
                break;
        }
    }

    /**
     * Transmission of the data objects to particular eu.funinnumbers.guardian based on MAC address.
     *
     * @param guardian the information of the eu.funinnumbers.guardian to initalize
     */
    protected void initializeDATA(final Guardian guardian) {
        Logger.getInstance().debug(" Initilization started");

        if (!guardians.containsKey(guardian.getAddress())) {
            // This is an unknown SPOT -- ignore
            return;
        }

        final Guardian guardianInfo = guardians.get(guardian.getAddress());

        // if the request is indeed valid proceed with the data transfer
        if (guardianInfo.getInitPhase() != Guardian.INIT_COMPLETE) {
            // transmit the data to the eu.funinnumbers.guardian
            transmitData(guardianInfo);
        }

    }

    /**
     * this method is rensponsible for transmiting the required data to the guadians that have
     * just been initialized.
     *
     * @param guardian the eu.funinnumbers.guardian class containing all the needed information
     */
    protected void transmitData(final Guardian guardian) {
        Logger.getInstance().debug("\n*** GuardianInitializer: Beginning Sunspot's "
                + guardian.getAddress() + " data transfer: ***");

        try {
            // Creates a Client Radiogram Connection on port REPLYPORT
            final DatagramConnection dgconnection = (RadiogramConnection) Connector.open("radiogram://"
                    + guardian.getAddress() + ":" + REPLYPORT);

            // Creates a Datagram using the above Connection
            final Datagram datagram = dgconnection.newDatagram(dgconnection.getMaximumLength());

            // Clean the Datagram
            datagram.reset();

            // Send message Type
            datagram.writeUTF("data");

            // convert the Guardian class to byte array
            byte[] byteArray = guardian.toByteArray();

            // Send length
            datagram.writeInt(byteArray.length);

            // Send the Guardian class
            datagram.write(byteArray);

            // Obtain avatarId from the guardiansAvatarsMap
            final Integer avatarID = (Integer) guardiansAvatarsMap.get(guardian.getAddress());
            final Avatar avatar = (Avatar) avatars.get(avatarID);
            final List pointsOfInterest = fgStation.getPointsOfInterest();

            final Iterator pointsIter = pointsOfInterest.iterator();

            // convert the Avatar class to byte array
            byteArray = avatar.toByteArray();

            // Send length
            datagram.writeInt(byteArray.length);

            // Send the Avatar class
            datagram.write(byteArray);

            // obtain the team id from the avatarsTeamsMap HashMap
            final Integer teamID = (Integer) avatarsTeamsMap.get(avatarID);
            final Team team = (Team) teams.get(teamID);

            Logger.getInstance().debug("AvatarID = " + avatarID + " -- TeamID = " + teamID);

            // convert the Team class to byte array
            byteArray = team.toByteArray();

            // Send length
            datagram.writeInt(byteArray.length);

            // Send the Team class
            datagram.write(byteArray);

            // Send the Number of PointsOfInterest
            datagram.writeInt(pointsOfInterest.size());

            // Send the actual ids of the above points
            while (pointsIter.hasNext()) {
                datagram.writeInt((Integer) pointsIter.next());
            }

            // Send the datagram
            dgconnection.send(datagram);
            dgconnection.close();

            /** Change the initPhase of the eu.funinnumbers.guardian to INIT_COMPLETE
             * to signify that the eu.funinnumbers.guardian is ready to start playing the game.
             */
            guardian.setInitPhase(Guardian.INIT_COMPLETE);

            // Notify Observers that the data setting phase has completed successfully
            this.setChanged();
            notifyObservers(guardian);

            Logger.getInstance().debug("GuardianInitializer: initialization data transmited");

        } catch (IOException ex) {
            Logger.getInstance().debug("GuardianInitializer: unable to transmit the initialization data ", ex);
        }

    }

    /**
     * Remote deployment of jar to particular eu.funinnumbers.guardian based on MAC address.
     *
     * @param guardian the information of the eu.funinnumbers.guardian to initialize
     */
    protected void initializeJAR(final Guardian guardian) { //NOPMD
        if (!guardians.containsKey(guardian.getAddress())) {
            // This is an unknown SPOT -- ignore
            Logger.getInstance().debug("This is an unknown SPOT -- ignore");
            return;
        }

        // If this sunspot has already been flashed, ignore this INIT_NOJAR request
        if (flashedGuardians.containsKey(guardian.getAddress())) {
            Logger.getInstance().debug("Sunspot has already been flashed, ignore this INIT_NOJAR request");
            return;
        }

        Logger.getInstance().debug("\n*** GuardianInitializer: Beginning Sunspot's "
                + guardian.getAddress() + " initialization sequence: ***");

        final Guardian guardianInfo = guardians.get(guardian.getAddress());

        // if the request is indeed valid proceed with the remoteDeploy
        if (guardianInfo.getInitPhase() == Guardian.INIT_NOJAR) {
            try {
                // Add Guardian to flashed guardians HashMap, associated with its address
                flashedGuardians.put(guardian.getAddress(), guardian);

                // Compile JAR and deploy
                prepareJAR(guardian);
                Logger.getInstance().debug("GuardianInitializer: compilation and deployment finished");

                // Deployment has completed successfully -- inform eu.funinnumbers.guardian to reset
                sendInitJARComplete(guardian);
                Logger.getInstance().debug("GuardianInitializer: initialization sequence finished");

            } catch (BuildException ex) {
                Logger.getInstance().debug("GuardianInitializer:---->BuildException!! you should re-deploy");
                // If there a problem occured while remoteDeploy, remove eu.funinnumbers.guardian from the flashedGuardians HashMap.
                flashedGuardians.remove(guardian.getAddress());
            }
        } else {
            Logger.getInstance().debug("GuardianInitializer: Not valid JAR-initialization request");
        }
    }

    /**
     * Compiles the eu.funinnumbers.guardian code, prepares the jar and remote deploys it to the eu.funinnumbers.guardian.
     *
     * @param guardian the information of the eu.funinnumbers.guardian to deploy the jar
     * @throws BuildException if remote deployment failed
     */
    protected void prepareJAR(final Guardian guardian) throws BuildException {
        // this is the xml file where the project to be deployed is located
        final File buildFile = new File(buildXMLpath);
        Logger.getInstance().debug("GuardianInitializer: opened xml file");

        final Project project = new Project();

        // Set initial properties
        project.setUserProperty("ant.file", buildFile.getAbsolutePath());

        final DefaultLogger consoleLogger = new DefaultLogger();
        //consoleLogger.setErrorPrintStream(System.err);
        // if this is uncommented ant's code will be shown on screen
        consoleLogger.setOutputPrintStream(System.out);
        consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
        project.addBuildListener(consoleLogger);

        project.fireBuildStarted();

        project.init();
        Logger.getInstance().debug("GuardianInitializer: xml Project initialized");

        final ProjectHelper helper = ProjectHelper.getProjectHelper();
        Logger.getInstance().debug("GuardianInitializer: new Project Helper");

        project.setProperty("remoteID", guardian.getAddress().toString());

        project.setProperty("port", "/dev/ttyACM1");

        // these properties will be set on the INIT_NODATA phase
        project.setProperty("guardianID", Integer.toString(1));
        project.setProperty("teamID", Integer.toString(1));
        final int ledId = 88888;
        project.setProperty("ledID", Integer.toString(ledId));

        project.addReference("ant.projectHelper", helper);
        helper.parse(project, buildFile);

        Logger.getInstance().debug("GuardianInitializer: beginning Over The Air deploy to remote Sunspot");
        Logger.getInstance().debug("GuardianInitializer: (this will take aproximately 20 seconds)\n");
        Logger.getInstance().debug("---------------------------apache--ant's--code--start--------------------------");

        project.executeTarget("deploy");
        project.fireBuildFinished(null);

        Logger.getInstance().debug("-------------------------apache--ant's--code--end-----------------------------");
    }

    /**
     * Sends a simple message to this eu.funinnumbers.guardian informing that the JAR deployment has completed.
     *
     * @param guardian the information of the eu.funinnumbers.guardian to inform
     */
    protected void sendInitJARComplete(final Guardian guardian) {
        /** Sends a "terminate" message to the remote Sunspot that was initialized to notify it for the
         * completion of its remoteDeploy
         * */
        try {
            // Creates a Client Radiogram Connection on port REPLYPORT
            final DatagramConnection dgconnection = (RadiogramConnection) Connector.open("radiogram://"
                    + guardian.getAddress() + ":" + REPLYPORT);

            // Creates a Datagram using the above Connection
            final Datagram datagram = dgconnection.newDatagram(dgconnection.getMaximumLength());

            // Clean the Datagram
            datagram.reset();

            // Write eu.funinnumbers.guardian
            datagram.writeUTF(REPLY_TOKEN);

            // Send the String
            dgconnection.send(datagram);
            dgconnection.close();

            Logger.getInstance().debug("GuardianInitializer: terminate message sent");

        } catch (IOException ex) {
            Logger.getInstance().debug("GuardianInitializer: unable to transmit init-jar-terminated message ", ex);
        }
    }
}
