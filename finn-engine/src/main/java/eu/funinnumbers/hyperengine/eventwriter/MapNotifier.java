package eu.funinnumbers.hyperengine.eventwriter;

import eu.funinnumbers.hyperengine.HyperStats;
import eu.funinnumbers.util.Logger;

import java.util.concurrent.BlockingQueue;

/**
 * Created by IntelliJ IDEA.
 * User: logaras
 * Date: May 15, 2010
 * Time: 4:22:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class MapNotifier extends Thread {


    /**
     * The Blocking Queue events received from Engine.
     */
    private final BlockingQueue<HyperStats> statsBuffer;


    /**
     * Default Constructor.
     *
     * @param queue the event queue
     */
    public MapNotifier(final BlockingQueue<HyperStats> queue) {
        //To change body of created methods use File | Settings | File Templates.
        statsBuffer = queue;

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
     * @see Thread#Thread(ThreadGroup, Runnable, String)
     */
    public void run() {


        while (true) {

            try {
                // Retrieves and removes the head of this buffer
                final HyperStats stats = statsBuffer.take();

                EventManager.getInstance().setChanged();
                EventManager.getInstance().notifyObservers(stats);

            } catch (InterruptedException e) {
                Logger.getInstance().debug("Cannot pop datagram from Queue", e);
            }

        }
    }

}
