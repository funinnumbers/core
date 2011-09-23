package eu.funinnumbers.hyperengine.eventwriter;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;
import eu.funinnumbers.hyperengine.HyperStats;
import eu.funinnumbers.util.Logger;

import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

/**
 * Created by IntelliJ IDEA.
 * User: logaras
 * Date: May 15, 2010
 * Time: 4:22:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class SoundNotifier extends Thread { //NOPMD


    /**
     * The Blocking Queue events received from Engine.
     */
    private final BlockingQueue<HyperStats> statsBuffer;

    private int towPlayers;

    /**
     * Default Constructor.
     *
     * @param queue the event queue
     */
    public SoundNotifier(final BlockingQueue<HyperStats> queue) {
        //To change body of created methods use File | Settings | File Templates.
        statsBuffer = queue;
        towPlayers = 0;

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
    public void run() { //NOPMD
       Logger.getInstance().debug("Sound notifier running");
        while (true) {

            try {
                // Retrieves and removes the head of this buffer
                final InetAddress host = InetAddress.getByName("192.168.1.13");
                final HyperStats stats = statsBuffer.take();

                final OSCPortOut sender = new OSCPortOut(host, 57120); //NOPMD


                if (stats.getTow() > towPlayers) {
                    Logger.getInstance().debug("Sending to george +");
                    towPlayers = stats.getTow();
                    final OSCMessage msg = new OSCMessage("/increaseNumberOfPlayers"); //NOPMD
                    sender.send(msg);

                } else if (stats.getTow() < towPlayers) {
                    Logger.getInstance().debug("Sending to george -");
                    towPlayers = stats.getTow();
                    final OSCMessage msg = new OSCMessage("/decreaseNumberOfPlayers"); //NOPMD
                    sender.send(msg);

                }

            } catch (Exception e) {
                Logger.getInstance().debug("Cannot pop datagram from Queue", e);
            }

        }
    }


}

