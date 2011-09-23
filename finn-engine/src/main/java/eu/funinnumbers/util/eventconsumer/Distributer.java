package eu.funinnumbers.util.eventconsumer;

import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.util.Logger;

import java.util.concurrent.BlockingQueue;

/**
 * Continuously monitor event buffer and send to observers.
 */
public class Distributer extends Thread {

    /**
     * The Blocking Queue events received from Engine.
     */
    private final BlockingQueue<Event> eventBuffer;

    /**
     * Default Constructor.
     *
     * @param queue the event queue
     */
    public Distributer(final BlockingQueue<Event> queue) {
        super();

        eventBuffer = queue;
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
                final Event event = eventBuffer.take();

                // Update all observers
                EventConsumer.getInstance().setChanged();
                EventConsumer.getInstance().notifyObservers(event);

            } catch (InterruptedException e) {
                Logger.getInstance().debug("Cannot pop datagram from Queue", e);
            }

        }
    }
}
