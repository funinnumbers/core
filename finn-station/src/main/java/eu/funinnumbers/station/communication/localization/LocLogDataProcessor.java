package eu.funinnumbers.station.communication.localization;

import eu.funinnumbers.engine.rmi.SGEngineInterface;
import com.sun.spot.util.Queue;
import eu.funinnumbers.util.Logger;


/**
 * Process LocLogData and Send them to Engine.
 */
public class LocLogDataProcessor extends Thread {

    private final SGEngineInterface engine;

    private final Queue dataQueue;

    private boolean isEnabled = true;

    public LocLogDataProcessor(final SGEngineInterface sgEngine, final Queue queue) {
        super();
        this.engine = sgEngine;
        this.dataQueue = queue;
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
     * @see #stop()
     * @see #Thread(ThreadGroup, Runnable, String)
     */
    public void run() {
        while (isEnabled) {
            final LocLogData locData = (LocLogData) dataQueue.get();
            try {
                if (engine != null) {
                    engine.logLocalizationValues((System.getProperty("IEEE_ADDRESS")), locData.getMacAddress(),
                            locData.getRssi(), locData.getLqi(), locData.getParam());
                }
            } catch (Exception e) {
                Logger.getInstance().debug("Eat it!");
            }
        }
    }

    public void kill() {
        this.isEnabled = false;
    }
}
