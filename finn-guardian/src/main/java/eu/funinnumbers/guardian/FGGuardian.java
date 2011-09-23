package eu.funinnumbers.guardian;

import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.sensorboard.peripheral.LEDColor;
import eu.funinnumbers.db.model.Avatar;
import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.StorableEntity;
import eu.funinnumbers.db.model.Team;
import eu.funinnumbers.db.model.GuardianInfo;
import eu.funinnumbers.guardian.communication.actionprotocol.ActionID;
import eu.funinnumbers.guardian.communication.dtservice.DTSManager;
import eu.funinnumbers.guardian.communication.echoprotocol.EchoProtocolManager;
import eu.funinnumbers.guardian.init.DataSetter;
import eu.funinnumbers.guardian.storage.PointsOfInterest;
import eu.funinnumbers.guardian.storage.StorageService;
import eu.funinnumbers.guardian.ui.misc.LEDManager;
import eu.funinnumbers.util.Logger;

import java.util.Vector;

/**
 * Abstract Application for Guardians.
 * Includes the code necessary to transmit game data over the air.
 */
public abstract class FGGuardian extends AbstractGuardianApp { //NOPMD

    /**
     * This function is executed when all data are loaded.
     * It does the actual startup of the game.
     */
    public abstract void startOldGameApp();

    /**
     * Checks if the Flash maintains data.
     *
     * @return true if the Guardian has been initialized with data properly.
     */
    protected boolean isInitialized() { //NOPMD

        /**
         * This boolean is used to determine if the <code>DataSetter</code> phase is required.
         * Default value is <code>false</code>.
         */
        boolean skipDataSetter = false;

        // Check if the user has requested a full reset
        if (1 == 0/*resetrq*/) {
            Logger.getInstance().debug("Full storage reset");
            StorageService.getInstance().clearAll();
            Logger.getInstance().debug("\t\tFlash memory has beed fully erased");
            return skipDataSetter;
        }

        // Try to retrieve any info stored in the flash memory about the identity of this eu.funinnumbers.guardian
        final Vector storedGuardians = StorageService.getInstance().listEntities(StorableEntity.GUARDIANENTITY);
        Logger.getInstance().debug("Guardian Records:" + storedGuardians.size());
        final Vector storedAvatars = StorageService.getInstance().listEntities(StorableEntity.AVATARENTITY);
        Logger.getInstance().debug("Avatar Records:" + storedAvatars.size());
        final Vector storedTeams = StorageService.getInstance().listEntities(StorableEntity.TEAMENTITY);
        Logger.getInstance().debug("Team Records:" + storedTeams.size());
        final Vector storedPOIs = StorageService.getInstance().listEntities(StorableEntity.POINTSOFINTEREST);
        Logger.getInstance().debug("Points of Interest:" + storedTeams.size());

        // If all the required info is available, skip the DataSetting phase
        if (!storedGuardians.isEmpty() && !storedAvatars.isEmpty() && !storedTeams.isEmpty() && !storedPOIs.isEmpty()) {

            Logger.getInstance().debug("All records are here.");

            // Obtain the first element of each returned vector
            final Guardian storedGuardian = (Guardian) storedGuardians.firstElement();
            final Avatar storedAvatar = (Avatar) storedAvatars.firstElement();
            final Team storedTeam = (Team) storedTeams.firstElement();
            final PointsOfInterest storedPOI = (PointsOfInterest) storedPOIs.firstElement();
            Logger.getInstance().debug("POIs " + storedPOI.getDSStation());

            // Initialize this eu.funinnumbers.guardian based on the info obtained above
            GuardianInfo.getInstance().setGuardian(storedGuardian);
            GuardianInfo.getInstance().setAvatar(storedAvatar);
            GuardianInfo.getInstance().setTeam(storedTeam);


            final ActionID actid = new ActionID();
            actid.setActionID(0);
            StorageService.getInstance().add(actid);
            // skip the DataSetter
            skipDataSetter = true;
        }

        return skipDataSetter;
    }

    /**
     * The Guardian is not initialized properly.
     * Sets dummy data based on hard-coded IDs.
     * todo: check why it doesnt run datasetter if eu.funinnumbers.guardian info is hardcoded
     */
    public void resetGuardianInfo() {
        // Debug Info
        Logger.getInstance().debug("Hard coded ID = " + 0/*put ID here*/
                + ", LEDID = " + 0/*put LED_ID here*/
                + ", TEAMID = " + 0/*put TEAM_ID here*/);

        // Initialize Guardian Info
        final Avatar avatar = new Avatar();
        avatar.setID(0/*put ID here*/);

        final Guardian guardian = new Guardian();
        guardian.setAddress(new Long(RadioFactory.getRadioPolicyManager().getIEEEAddress()));
        guardian.setID(avatar.getID());
        guardian.setLedId(0/*put LED_ID here*/);
        guardian.setInitPhase(0/*put INIT_PHASE here*/);

        final Team team = new Team();
        team.setTeamId(0/*put TEAM_ID here*/);
        team.setName("Monk");

        GuardianInfo.getInstance().setAvatar(avatar);
        GuardianInfo.getInstance().setGuardian(guardian);
        GuardianInfo.getInstance().setTeam(team);

        final ActionID actid = new ActionID();
        actid.setActionID(0);
        StorageService.getInstance().add(actid);

        // if the eu.funinnumbers.guardian is supposed to start with hard-coded data, save the data to the flash
        if (0/*put INIT_PHASE here*/ == 2) {
            try {
                // Add received information to the flash memory
                StorageService.getInstance().add(guardian);
                StorageService.getInstance().add(avatar);
                StorageService.getInstance().add(team);
            } catch (Exception e) {
                Logger.getInstance().debug("Error while writing on Storage");
            }

        }
    }

    /**
     * By passes all init phases and writes all info
     * passed as arguments from command line on storage.
     * This method resets any previous stored info except Events
     */
    public void byPassInit() {
        //Write hardcoded data on flash
        Logger.getInstance().debug("\t\tBypassing Init!");

        // Initialize Guardian Info
        final Avatar avatar = new Avatar();
        avatar.setID(0/*put ID here*/);
        avatar.setName("nameless");

        final Guardian guardian = new Guardian();
        guardian.setAddress(new Long(RadioFactory.getRadioPolicyManager().getIEEEAddress()));
        guardian.setID(2);
        final int ledId = 13111;
        guardian.setLedId(ledId);
        // Init Phase fixed
        guardian.setInitPhase(2);

        final Team team = new Team();
        team.setTeamId(0/*put TEAM_ID here*/);
        team.setName("Teamless");
        team.setBattleEngine(null);

        GuardianInfo.getInstance().setAvatar(avatar);
        GuardianInfo.getInstance().setGuardian(guardian);
        GuardianInfo.getInstance().setTeam(team);

        final ActionID actid = new ActionID();
        actid.setActionID(0);

        // Start DTS in case there are stored events.
        // You don't wanna loose the first update from EchoProtocol
        DTSManager.getInstance();

        //Check previous stored events
        Logger.getInstance().debug("Already stored events"
                + StorageService.getInstance().listEntities(StorableEntity.EVENTENTITY).size());

        // Store info on flash
        StorageService.getInstance().add(guardian);
        StorageService.getInstance().add(avatar);
        //StorageService.getInstance().add(team);
        StorageService.getInstance().add(actid);
    }


    /**
     * Initiates the Guardian Data so that basic protocols
     * like EchoProtocol are able to run.
     */
    private void startInitData() {
        //Write hardcoded data on flash
        Logger.getInstance().debug("\t\tWaiting for Init!");

        // Initialize Guardian Info
        final Avatar avatar = new Avatar();
        avatar.setID(-1);
        avatar.setName("not-initialized");

        final Guardian guardian = new Guardian();
        guardian.setAddress(new Long(RadioFactory.getRadioPolicyManager().getIEEEAddress()));

        guardian.setID(-1);
        guardian.setLedId(0);
        guardian.setInitPhase(Guardian.INIT_NODATA);

        final Team team = new Team();
        team.setTeamId(-1);
        team.setName("not-initalized-team");
        team.setBattleEngine(null);

        GuardianInfo.getInstance().setAvatar(avatar);
        GuardianInfo.getInstance().setGuardian(guardian);
        GuardianInfo.getInstance().setTeam(team);

        // Initialize EchoProtocol Manager
        EchoProtocolManager.getInstance().updateBcast();

        // Start DataSetter thread
        (new DataSetter(this)).run();

    }

    /**
     * The rest is boiler plate code, for Java ME compliance.
     * startApp() is the MIDlet call that starts the application.
     */
    public void startGameApp() { //NOPMD
        Logger.getInstance().debug("Starting MIDlet");

        // Set the SenSys freq. channel
        //Logger.getInstance().debug("Changing Transmission channel for HotPotato");
        //IRadioPolicyManager rpm = Spot.getInstance().getRadioPolicyManager();
        //rpm.setChannelNumber(12);

        // This is supposed to be a call to isInitialized()
        final boolean dataexist = isInitialized();
        //int initPhase = Guardian.INIT_NODATA;
        // bypass init
        byPassInit();
        int initPhase = Guardian.INIT_COMPLETE;
        if (dataexist) {
            initPhase = GuardianInfo.getInstance().getGuardian().getInitPhase();
        }

        // Check init phase
        switch (initPhase) {

            // The eu.funinnumbers.guardian has no data -- but correct APP
            case Guardian.INIT_NODATA:
                LEDManager.getInstance().setOnLEDS(LEDManager.FIRST_LED, LEDManager.LAST_LED, LEDColor.BLUE);

                // Start Echo and DataSetter
                startInitData();

                break;

            case Guardian.INIT_COMPLETE:
                startOldGameApp();
                break;


            case Guardian.INIT_LOST:
                LEDManager.getInstance().setOnLEDS(LEDManager.FIRST_LED, LEDManager.LAST_LED, LEDColor.RED);

                // Start Echo and DataSetter
                startInitData();

                // Start DTS in case there are stored events.
                // You don't wanna loose the first update from EchoProtocol
                DTSManager.getInstance();

                break;

            default:

        }


    }

}

