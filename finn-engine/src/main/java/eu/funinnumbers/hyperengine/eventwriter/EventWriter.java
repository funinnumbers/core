package eu.funinnumbers.hyperengine.eventwriter;

import eu.funinnumbers.db.model.event.Event;

import java.util.concurrent.BlockingQueue;
import java.util.Date;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import eu.funinnumbers.util.Logger;

/**
 * Write the Events to a file.
 */
public class EventWriter extends Thread {


    /**
     * The Blocking Queue events received from Engine.
     */
    private final BlockingQueue<Event> eventBuffer;

    /**
     * The Output file.
     */
    private final String outputFile;

    /**
     * Default Constructor.
     *
     * @param queue the event queue
     */
    public EventWriter(final BlockingQueue<Event> queue) {
        //To change body of created methods use File | Settings | File Templates.
        eventBuffer = queue;
        outputFile = (new Date()).toString();
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


                final FileWriter fstream = new FileWriter(outputFile, true); //NOPMD
                final BufferedWriter out = new BufferedWriter(fstream); //NOPMD
                out.write(event.getDebugInfo());
                out.newLine();
                out.close();

            } catch (IOException e) {
                Logger.getInstance().debug("Cannot write to file", e);
            } catch (InterruptedException e) {
                Logger.getInstance().debug("Cannot pop datagram from Queue", e);
            }

        }
    }

}
