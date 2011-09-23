package eu.funinnumbers.guardian.util;

/**
 * Compute CPU Usage on SUNSpot device.
 */
public class CPUUsage extends Thread {

    /**
     * Override Threads run function.
     */
    public void run() {
        int i = 0;
        long startTime = 0, stopTime = 0;
        int count = 0;


        while (count < 1000) {
            i++;
            if (i == 1) {
                startTime = System.currentTimeMillis();
            } else if (i == 62000) {
                i = 0;
                stopTime = System.currentTimeMillis();
                final long timePas = stopTime - startTime;
                //Logger.getInstance().debug(stopTime + " - " + startTime + " = " + timePas);
                count++;
                //FinnLogger.getInstance().idleUsage(timePas);
            }
        }
    }

    public static void main(final String[] args) {
        final CPUUsage the = new CPUUsage();
        the.start();
    }
}
