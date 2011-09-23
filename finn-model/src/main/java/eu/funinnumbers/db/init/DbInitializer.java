package eu.funinnumbers.db.init; //NOPMD

import eu.funinnumbers.db.managers.GuardianManager;
import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.util.HibernateUtil;
import org.hibernate.Transaction;
import eu.funinnumbers.util.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Initialize the database.
 * Inserts into the database the guardians.
 */
public final class DbInitializer {

    /**
     * Create a New Guardian.
     *
     * @param address The mac address of the Guardian
     * @return the eu.funinnumbers.guardian
     */
    public static Guardian createGuardian(final String address) {
        final Guardian guardian = new Guardian();
        guardian.setAddress(address);
        guardian.setInitPhase(0);
        guardian.setStation("nostation");

        return guardian;
    }

    /**
     * Get the list with the mac addresses.
     *
     * @return A List with mac addresses.
     */
    public static List<String> getMacAddresses() {
        final List<String> macAddresses = new ArrayList<String>();

        macAddresses.add("0014.4F01.0000.3602");
        //macAddresses.add("0014.4F01.0000.40EA");

        //macAddresses.add("0014.4F01.0000.4110");
        macAddresses.add("0014.4F01.0000.410C");
        //macAddresses.add("0014.4F01.0000.4104");
        macAddresses.add("0014.4F01.0000.410F");

        macAddresses.add("0014.4F01.0000.477F");

        macAddresses.add("0014.4F01.0000.483E");
        macAddresses.add("0014.4F01.0000.4884");
        macAddresses.add("0014.4F01.0000.487E");

        macAddresses.add("0014.4F01.0000.4AD7");

        macAddresses.add("0014.4F01.0000.4BEB");

        //macAddresses.add("0014.4F01.0000.4C48");
        macAddresses.add("0014.4F01.0000.4C9B");
        macAddresses.add("0014.4F01.0000.4CB2");

        macAddresses.add("0014.4F01.0000.4DA4");
        //macAddresses.add("0014.4F01.0000.4D78");

        macAddresses.add("0014.4F01.0000.5442");
        macAddresses.add("0014.4F01.0000.53F2");
        macAddresses.add("0014.4F01.0000.53CF");
        //macAddresses.add("0014.4F01.0000.556D");

        return macAddresses;
    }

    /**
     * Save the eu.funinnumbers.guardian in the DB.
     */
    public static void saveGuardians() {
        //Create Guardians and add them a mac address and a ledID
        final Iterator<String> iter = getMacAddresses().iterator();
        int totGuardians = 1;
        final int rand1 = 1000;
        final int rand2 = 100;
        while (iter.hasNext()) {
            final Guardian guardian = createGuardian(iter.next());

            guardian.setLedId((totGuardians * rand1) + (totGuardians * rand2) + totGuardians);
            GuardianManager.getInstance().add(guardian);
            totGuardians++;
        }
        Logger.getInstance().debug("Inserted " + (totGuardians - 1) + " Guardians");
    }

    /**
     * Save eu.funinnumbers.guardian to the DB.
     *
     * @param args arguments
     */
    public static void main(final String[] args) {
        // HibernateUtil.setConfiguration("hibernateHP.cfg.xml");
        // final Transaction tranx = HibernateUtil.getInstance().getSession().beginTransaction();
        // saveGuardians();
        // tranx.commit();


        HibernateUtil.setConfiguration("eu/funinnumbers/hibernateMM.cfg.xml");
        final Transaction trans = HibernateUtil.getInstance().getSession().beginTransaction();
        saveGuardians();
        trans.commit();
    }


}


