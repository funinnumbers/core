package eu.funinnumbers.guardian;

import com.sun.spot.peripheral.radio.RadioFactory;
import eu.funinnumbers.db.model.Avatar;
import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.Team;
import eu.funinnumbers.db.model.GuardianInfo;
import eu.funinnumbers.guardian.communication.actionprotocol.ActionID;
import eu.funinnumbers.guardian.storage.StorageService;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * Abstract Guardian Application is the parent class for all FINN eu.funinnumbers.games.
 */
public abstract class AbstractGuardianApp extends MIDlet {

    /**
     * This function is executed when all data are loaded.
     * It does the actual startup of the game.
     */
    public abstract void startGameApp();


    /**
     * Never used on SunSPOTs.
     *
     * @param b as boolean
     * @throws MIDletStateChangeException whe something goes wrong.
     */
    protected void destroyApp(final boolean b) throws MIDletStateChangeException { //NOPMD

    }

    /**
     * Never used on SunSPOTS.
     */
    protected void pauseApp() { //NOPMD
        //Do nothing
    }

    /**
     * Starts Game Application code.
     *
     * @throws MIDletStateChangeException State Change Exception
     */
    protected void startApp() throws MIDletStateChangeException {
        startGameApp();
    }

    /**
     * Basic eu.funinnumbers.guardian init.
     */
    protected void basicInit() {
        final Avatar avatar = new Avatar();
        avatar.setID(-1);
        avatar.setName("not-initialized");

        final Guardian guardian = new Guardian();
        guardian.setAddress(new Long(RadioFactory.getRadioPolicyManager().getIEEEAddress()));

        guardian.setID(-1);
        guardian.setLedId(0);
        guardian.setInitPhase(Guardian.INIT_COMPLETE);

        final Team team = new Team();
        team.setTeamId(-1);
        team.setName("not-initalized-team");
        team.setBattleEngine(null);

        GuardianInfo.getInstance().setAvatar(avatar);
        GuardianInfo.getInstance().setGuardian(guardian);
        GuardianInfo.getInstance().setTeam(team);

        final ActionID actid = new ActionID();
        actid.setActionID(0);
        StorageService.getInstance().add(actid);
    }
}
