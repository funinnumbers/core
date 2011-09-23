package eu.funinnumbers.engine.event.stopwatch;

import eu.funinnumbers.util.Logger;

import java.util.Vector;
import java.util.concurrent.BlockingQueue;

/**
 * StopWatch for Engine Metrics.
 */
public class StopWatch extends Thread { //NOPMD

    /**
     * Start Time queue.
     */
    private final BlockingQueue<Long> startTimeQueue;

    /**
     * Stop Time queue.
     */
    private final BlockingQueue<Long> stopTimeQueue;

    /**
     * StopWatch constructor.
     *
     * @param startQueue the start time queue
     * @param stopQueue  the stop time queue
     */
    public StopWatch(final BlockingQueue<Long> startQueue, final BlockingQueue<Long> stopQueue) {
        super();

        startTimeQueue = startQueue;

        stopTimeQueue = stopQueue;
    }

    /**
     * Overriding Thread's run.
     */
    public void run() { //NOPMD

        final Vector<Long> vector1 = new Vector<Long>(); //NOPMD
        final Vector<Long> vector2 = new Vector<Long>(); //NOPMD

        while (true) {
            long totalDurationTime = 0;
            int temp = 0;

            if (!vector1.isEmpty() && !vector2.isEmpty()) {
                final long timestamp1 = vector2.lastElement() - vector1.firstElement();
                Logger.getInstance().debug("\t\tTotal time + Guardian : " + timestamp1);
                vector1.clear();
                vector2.clear();
            }
            Logger.getInstance().debug("\n\n\n\n\n\n\n\n\n\n");
            try {
                //sleep(60000);
                System.in.read();

            } catch (Exception e) {
                Logger.getInstance().debug(e.toString());
            }
            Logger.getInstance().debug("Starting Proccess");
            Logger.getInstance().debug("Start :" + startTimeQueue.size() + " Stop : " + stopTimeQueue.size());


            while (!startTimeQueue.isEmpty() && !stopTimeQueue.isEmpty()) {
                final Long startT = startTimeQueue.poll();
                final Long stopT = stopTimeQueue.poll();

                vector1.addElement(startT);
                vector2.addElement(stopT);

                final long duration = stopT - startT;

                totalDurationTime += duration;
                temp++;
                Logger.getInstance().debug("\t\tStartime : " + startT + " \tStopTime : " + stopT);
                Logger.getInstance().debug(" \t\t***Time of event on Engine Layer + Hibernate: " + duration);


            }
            Logger.getInstance().debug("\n\t\tTotal time of " + temp + " Events = " + totalDurationTime);


        }
    }

}

