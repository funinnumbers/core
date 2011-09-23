package eu.funinnumbers.station.stopwatch;

import eu.funinnumbers.util.Logger;

import java.util.Vector;
import java.util.concurrent.BlockingQueue;

/**
 * StopWatch for Station Metrics.
 */
public class StopWatch extends Thread { //NOPMD

    /**
     * Queue with StartTimes.
     */
    private final BlockingQueue startTimeQueue;

    /**
     * Queue with StopTimes.
     */
    private final BlockingQueue stopTimeQueue;

    /**
     * Public Contuctor.
     *
     * @param startQueue Queue with StartTimes
     * @param stopQueue  Queue with StopTimes
     */
    public StopWatch(final BlockingQueue startQueue, final BlockingQueue stopQueue) {
        super();

        //Initalize the queue
        startTimeQueue = startQueue;

        //Initalize the queue
        stopTimeQueue = stopQueue;
    }

    /**
     * Overriding Thread.run.
     */
    public void run() { //NOPMD
        final Vector<Long> vector1 = new Vector<Long>(); //NOPMD
        final Vector<Long> vector2 = new Vector<Long>(); //NOPMD

        long startTime, stopTime;

        long duration;

        while (true) {
            long totalDurationTime = 0;
            int temp = 0;

            if (!vector1.isEmpty() && !vector2.isEmpty()) {
                final long timeP = vector2.lastElement() - vector1.firstElement();
                Logger.getInstance().debug("\t\tTotal time + Guardian : " + timeP);
                vector1.clear();
                vector2.clear();
            }
            try {
                System.in.read();
            } catch (Exception e) {
                Logger.getInstance().debug(e.toString());
            }
            Logger.getInstance().debug("Starting Proccess");
            Logger.getInstance().debug("Start :" + startTimeQueue.size() + " Stop : " + stopTimeQueue.size());

            while (!startTimeQueue.isEmpty() && !stopTimeQueue.isEmpty()) {
                final Long startT = (Long) startTimeQueue.poll();
                final Long stopT = (Long) stopTimeQueue.poll();
                startTime = startT;
                stopTime = stopT;
                vector1.addElement(startT);
                vector2.addElement(stopT);
                duration = stopTime - startTime;
                totalDurationTime += duration;
                temp++;
                Logger.getInstance().debug("\t\tStartime : " + startTime + " \tStopTime : " + stopTime);
                Logger.getInstance().debug(" \t\t***Time of event on Station Layer + RMI: " + duration);


            }
            Logger.getInstance().debug("\n\t\tTotal time of " + temp + " Events : " + totalDurationTime);
        }
    }

}
