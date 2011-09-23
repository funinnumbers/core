package eu.funinnumbers.guardian.util;


import eu.funinnumbers.util.Logger;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.db.model.GuardianInfo;
import eu.funinnumbers.guardian.communication.dtservice.DTSManager;

import java.util.Date;


/**
 * Used to keep record of different network, platform, game parameters.
 */
public class FinnLogger {
    /**
     * Defines the sampling period as ECHO_INTERVAL x SAMP_PERIOD.
     */
    public static final int SAMP_PERIOD = 2;

    /**
     *
     */
    public static int overAllcounter = 0;
    /**
     * The unique instance of the Logger class.
     */
    private static FinnLogger ourInstance = null;

    /**
     * Defines the size of logs. Should be relevant to sampling rate.
     */
    public static final int LOG_SIZE = 1000;

    /*******************************************************/
    /* The following Vectors store data periodacally sampled.
    /*******************************************************/

    /**
     * Sampling Vector of uniDirectional neighbors.
     */
    private final int[] uniDirectional;

    /**
     * Sampling Vector of biDirectional neighbors.
     */
    private int[] biDirectional;

    /**
     * Sampling Vector of stations.
     */
    private final int[] stations;

    /**
     * Sampling Vector of battery level.
     */
    private final int[] battery;

    /**
     * Sampling Vector of free memory.
     */
    private long[] memory;


    /****************************************************************/
    /* The following variables represent counters                   */
    /***************************************************************/

    /**
     * Number of stored events.
     */
    private int storedEvents;

    /**
     * Number of failed actions.
     */
    private int actionsFailed = 0;

    /**
     * Number of Succeeded actions.
     */
    private int actionsSucceeded = 0;

    /**
     * Number of neighborhood changes.
     */
    private int changedNeighborhood = 0; //NOPMD

    /**
     * Number of received potatoes.
     */
    private int receivedPotatoes = 0;

    /**
     * Number of generated potatoes.
     */
    private int generatedPotatoes = 0;

    /**
     * Number of sent potatoes.
     */
    private int sentPotatoes = 0;

    /**
     * Times of potatoes have been merging.
     */
    private int mergedPotatoes = 0;
    /**
     * Number of recognized gestures.
     */
    private int gesturesRecognized = 0;

    /**
     *
     */
    private int neighborLessGestures = 0;

    /**
     * Number of gestures recognized when player wasn't holding any potato.
     */
    private int potatoLessGestures = 0; //NOPMD


    /**
     * Indicated potato status (active|inactive).
     */
    private boolean activePotato = false;


    /*******************************************************/
    /* The following Vectors store all counters so that     */
    /* matching between sampled info can be achieved       */
    /*******************************************************/

    /**
     * Sampling Vector of stored events.
     */
    private int[] storedEventsAr;

    /**
     * Sampling Vector of failes actions.
     */
    private int[] actionsFailedAr;

    /**
     * Sampling Vector of succeeded actions.
     */
    private int[] actionsSucceededAr;

    /**
     * Sampling Vector of neighborhood changes.
     */
    private int[] changedNeighborAr;

    /**
     * Sampling Vector of received potatoes.
     */
    private int[] receivedPotatoesAr;

    /**
     * Sampling Vector of sent potatoes.
     */
    private int[] sentPotatoesAr;

    /**
     * Sampling Vector of generated potatoes.
     */
    private int[] generatedPotatoesAr;

    /**
     * Sampling Vector of sent potatoes.
     */
    private int[] mergedPotatoesAr;

    /**
     * Sampling Vector of recognized gestures.
     */
    private int[] gesturesRecognizedAr;

    /**
     * Sampling Vector of recognized gestures when player wasn't holding any potato.
     */
    private int[] potatoLessGesturesAr;

    /**
     * Samplling Vector of recognized gestures when player had no active neighbors.
     */
    private int[] neighborLessGesturesAr;

    /**
     * Sampling Vector of CPU Usage.
     */
    private long[] idleUsageAr;


    /**
     * Sampling array of potato status. (active|inactive)
     */
    private int[] activePotatoAr;


    /**
     * Provides access to the unique FinnLogger instance.
     *
     * @return FinnLogger instance
     */
    public static FinnLogger getInstance() {

        synchronized (FinnLogger.class) {
            // Check if an instance has already been created
            if (ourInstance == null) {
                // Create a new instance if not
                ourInstance = new FinnLogger();
            }
        }
        return ourInstance;


    }

    /**
     * Default constructor.
     */
    private FinnLogger() {

        /* Get free Memory*/
        //TODO
        /* Initialize sampling Vectors */
        uniDirectional = new int[LOG_SIZE];
        biDirectional = new int[LOG_SIZE];
        stations = new int[LOG_SIZE];
        battery = new int[LOG_SIZE];
        memory = new long[LOG_SIZE];
        idleUsageAr = new long[5 * LOG_SIZE];

        /* Initialize sampling Vectors */
        storedEventsAr = new int[LOG_SIZE];
        actionsFailedAr = new int[LOG_SIZE];
        actionsSucceededAr = new int[LOG_SIZE];
        changedNeighborAr = new int[LOG_SIZE];
        receivedPotatoesAr = new int[LOG_SIZE];
        sentPotatoesAr = new int[LOG_SIZE];
        generatedPotatoesAr = new int[LOG_SIZE];
        mergedPotatoesAr = new int[LOG_SIZE];
        gesturesRecognizedAr = new int[LOG_SIZE];
        potatoLessGesturesAr = new int[LOG_SIZE];
        neighborLessGesturesAr = new int[LOG_SIZE];
        storedEventsAr = new int[LOG_SIZE];
        activePotatoAr = new int[LOG_SIZE];

        // Put first elements
        initVectors();

        /* Get free memory */
        //TODO
        /* Calculate memory allocated by Logger*/
        //TODO


    }


    /****************************************************/
    /*                  Sampled info                   */
    /****************************************************/

    /**
     * Logs the number of uni directional neighbors.
     *
     * @param count the number as int
     */
    public void uniDirectional(final int count) {
        //uniDirectional[overAllcounter] = count;
    }

    /**
     * Logs the number of uniDirectional neighbors.
     *
     * @param count the number as int
     */
    public void biDirectional(final int count) {
        biDirectional[overAllcounter] = count;

    }


    /**
     * Logs the number of in-range stations.
     *
     * @param count the number as int
     */
    public void stations(final int count) {
        //stations[overAllcounter] = count;

    }

    /**
     * Logs the idle time of CPU Usage thread.
     *
     * @param time time in ms
     */
    public void idleUsage(final long time) {
        idleUsageAr[overAllcounter] = time;
    }

    /**
     * Logs the battery level.
     */
    public void batteryLevel() {
        /*START_OF_FINNLOGGER*/
        //battery[overAllcounter] = Spot.getInstance().getPowerController().getBattery().getBatteryLevel();
        /*END_OF_FINNLOGGER*/

    }

    /**
     * Logs available memory.
     */
    public void memory() {
        memory[overAllcounter] = Runtime.getRuntime().freeMemory();
        //Runtime.getRuntime().totalMemory();

    }

    /**
     * Changes the status of the potato.
     *
     * @param activePotatoP the status of the potato
     */
    public void setPotatoActive(final boolean activePotatoP) {
        this.activePotato = activePotatoP;
    }

    /****************************************************/
    /*              Counter-like info                   */
    /**
     * ************************************************
     */

    public void increaseStoredEvents() {
        //storedEvents++;

    }

    public void decreaseStoredEvents() {
        //storedEvents--;
    }

    /**
     * Logs the times a playes changes its neighborhood.
     */
    public void changedNeighborhood() {
        //changedNeighborhood++;

    }

    /**
     * Should be invoked whenever a gesture is succefully recognized.
     *
     * @param type indicates if the players carries a potato
     */
    public void gesturesRecognized(final int type) {

        if (type == 0) {
            gesturesRecognized++;

        } else if (type == 1) {
            // potatoLessGestures++;

        } else if (type == 2) {
            neighborLessGestures++;
        }

    }

    /**
     * Invoked wheneved an action has failed.
     */
    public void actionFailed() {
        actionsFailed++;

    }

    /**
     * Should be invoked whenever an action has been succefully completed.
     */
    public void actionSucceeded() {
        actionsSucceeded++;

    }

    /**
     * Invoked whenever a potato has been received.
     */
    public void potatoReceived() {
        receivedPotatoes++;

    }

    /**
     * Invoked whenever a potato has been sent.
     */
    public void potatoSent() {
        sentPotatoes++;
    }

    /**
     * Invoked whenever a potato has been generated.
     */
    public void potatoGenerated() {
        generatedPotatoes++;


    }

    /**
     * Invoked whenever two potatos are merged.
     */
    public void mergedPotato() {
        mergedPotatoes++;
    }


    /****************************************************/
    /*                 Other methods                   */
    /****************************************************/

    /**
     * Writes each counter on the corresponding Vector.
     * Important! here overAllCounter is also increased.
     */
    public void logCounters() {
        overAllcounter++;

        storedEventsAr[overAllcounter] = storedEvents;
        actionsFailedAr[overAllcounter] = actionsFailed;
        actionsSucceededAr[overAllcounter] = actionsSucceeded;
        changedNeighborAr[overAllcounter] = changedNeighborhood;
        receivedPotatoesAr[overAllcounter] = receivedPotatoes;
        sentPotatoesAr[overAllcounter] = sentPotatoes;
        generatedPotatoesAr[overAllcounter] = generatedPotatoes;
        mergedPotatoesAr[overAllcounter] = mergedPotatoes;
        gesturesRecognizedAr[overAllcounter] = gesturesRecognized;
        potatoLessGesturesAr[overAllcounter] = potatoLessGestures;
        neighborLessGesturesAr[overAllcounter] = neighborLessGestures;
        storedEventsAr[overAllcounter] = storedEvents;
        activePotatoAr[overAllcounter] = (activePotato ? 1 : 0);
    }


    /**
     * Bulk screen debugging.
     */
    public void screenDebug() {
        Logger.getInstance().debug("N" + "\t"
                + "uni-d\t"
                + "bi-d\t"
                + "stations\t"
                //+ idleUsageAr[i] + "\t"
                + "batt\t"
                + "freemem\t"
                + "events\t"
                + "actF\t"
                + "actS\t"
                + "chN\t"
                + "pRCV\t"
                + "pGEN\t"
                + "pSNT\t"
                + "pMRG\t"
                + "gREC\t"
                + "pLGes\t"
                + "nLGes\t"
                + "pot?");

        for (int i = 1; i < overAllcounter; i++) {
            Logger.getInstance().debug(i + "\t"
                    + uniDirectional[i] + "\t"
                    + biDirectional[i] + "\t"
                    + stations[i] + "\t"
                    //+ idleUsageAr[i] + "\t"
                    + storedEventsAr[i] + "\t"
                    + battery[i] + "\t"
                    + memory[i] + "\t"
                    + storedEventsAr[i] + "\t"
                    + actionsFailedAr[i] + "\t"
                    + actionsSucceededAr[i] + "\t"
                    + changedNeighborAr[i] + "\t"
                    + receivedPotatoesAr[i] + "\t"
                    + generatedPotatoesAr[i] + "\t"
                    + sentPotatoesAr[i] + "\t"
                    + mergedPotatoesAr[i] + "\t"
                    + gesturesRecognizedAr[i] + "\t"
                    + potatoLessGesturesAr[i] + "\t"
                    + neighborLessGesturesAr[i] + "\t"
                    + activePotatoAr[i]

            );

        }

    }

    /**
     * Bulk debugging using events.
     */
    public void eventDebug() {
        final Event logEvent = new Event();
        logEvent.setType("LOG");
        logEvent.setGuardianID(GuardianInfo.getInstance().getGuardian().getID());
        logEvent.setTimeStamp(new Date());


        StringBuffer data;


        for (int i = 1; i < overAllcounter; i++) {
            data = new StringBuffer(); //NOPMD
            data.append(i);
            data.append("\t");
            data.append(uniDirectional[i]);
            data.append("\t");
            data.append(biDirectional[i]);
            data.append("\t");
            data.append(stations[i]);
            data.append("\t");
            data.append(storedEventsAr[i]);
            data.append("\t");
            data.append(battery[i]);
            data.append("\t");
            data.append(memory[i]);
            data.append("\t");
            data.append(storedEventsAr[i]);
            data.append("\t");
            data.append(actionsFailedAr[i]);
            data.append("\t");
            data.append(actionsSucceededAr[i]);
            data.append("\t");
            data.append(changedNeighborAr[i]);
            data.append("\t");
            data.append(receivedPotatoesAr[i]);
            data.append("\t");
            data.append(sentPotatoesAr[i]);
            data.append("\t");
            data.append(generatedPotatoesAr[i]);
            data.append("\t");
            data.append(mergedPotatoesAr[i]);
            data.append("\t");
            data.append(gesturesRecognizedAr[i]);
            data.append("\t");
            data.append(potatoLessGesturesAr[i]);
            data.append("\t");
            data.append(activePotatoAr[i]);

            logEvent.setDescription(data.toString());
            DTSManager.getInstance().sendEvent(logEvent);
        }
        //Utils.sleep(2000);

        //eventCPUDebug();
    }

    /**
     * CPU Usage debugging using events.
     */
    public void eventCPUDebug() {
        final Event logEvent = new Event();
        logEvent.setType("CPULOG");
        logEvent.setGuardianID(GuardianInfo.getInstance().getGuardian().getID());
        logEvent.setTimeStamp(new Date());


        StringBuffer data;


        for (int i = 1; i < idleUsageAr.length; i++) {
            data = new StringBuffer(); //NOPMD
            data.append(i);
            data.append("\t");
            data.append(idleUsageAr[i]);
            logEvent.setDescription(data.toString());
            DTSManager.getInstance().sendEvent(logEvent);
        }
    }


    /**
     * Writes initial values to all logging vectors.
     */
    private void initVectors() {
        ;
        // useless
        /*uniDirectional.addElement(new Integer(-1));
        biDirectional.addElement(new Integer(-1));
        stations.addElement(new Integer(-1));
        storedEventsAr.addElement(new Integer(-1));
        battery.addElement(new Integer(-1));
        memory.addElement(new Integer(-1));
        storedEventsAr.addElement(new Integer(-1));
        actionsFailedAr.addElement(new Integer(-1));
        actionsSucceededAr.addElement(new Integer(-1));
        changedNeighborAr.addElement(new Integer(-1));
        receivedPotatoesAr.addElement(new Integer(-1));
        sentPotatoesAr.addElement(new Integer(-1));
        generatedPotatoesAr.addElement(new Integer(-1));
        mergedPotatoesAr.addElement(new Integer(-1));
        gesturesRecognizedAr.addElement(new Integer(-1));
        potatoLessGesturesAr.addElement(new Integer(-1));
        neighborLessGesturesAr.addElement(new Integer(-1));
        */
        //idleUsageAr.addElement(new Integer(-1));

    }
}
