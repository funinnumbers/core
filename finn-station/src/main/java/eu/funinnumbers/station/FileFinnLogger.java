package eu.funinnumbers.station;

import eu.funinnumbers.util.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.GregorianCalendar;

/**
 * Logs any incoming string to a data file.
 */
public class FileFinnLogger {
    private static FileFinnLogger ourInstance = new FileFinnLogger();

    /**
     * Provides access to the unique FinnLogger instance.
     *
     * @return FinnLogger instance
     */
    public static FileFinnLogger getInstance() {

        synchronized (FileFinnLogger.class) {
            // Check if an instance has already been created
            if (ourInstance == null) {
                // Create a new instance if not
                ourInstance = new FileFinnLogger();
            }
        }
        return ourInstance;


    }

    /**
     * Default constructor.
     *
     * @param filename    the log filename
     * @param description the data to be writen.
     */
    public void writeToFile(final String filename, final String description) {

        final BufferedWriter buffWriter;
        final File directory = new File(System.getProperty("user.home") + "/finnlogs");

        try {
            if (!directory.exists()) {
                Logger.getInstance().debug("NO DIRECTORY");
                directory.mkdir();
            }
            final File logfile = new File(System.getProperty("user.home") + "/finnlogs/" + filename + "." + yyymmdd(new GregorianCalendar()));
            buffWriter = new BufferedWriter(new FileWriter(logfile, logfile.exists()));
            buffWriter.write(description);
            buffWriter.newLine();
            buffWriter.close();

        } catch (IOException e) {
            Logger.getInstance().debug(e);
        }

    }

    /**
     * Generates a string out of current date in YYYYMMDD format.
     *
     * @param gcal the GrecgoricanCalendar to be used.
     * @return string with YYYYMMDD format
     */
    private String yyymmdd(final GregorianCalendar gcal) {
        final int month = gcal.get(GregorianCalendar.MONTH) + 1;
        final int day = gcal.get(GregorianCalendar.DATE);
        final String strMonth = Integer.toString(month);
        final String strDay = Integer.toString(day);
        return gcal.get(GregorianCalendar.YEAR) 
                + (month < 10 ? "0" + strMonth : strMonth)
                + (day < 10 ? "0" + strDay : strDay);
    }
}


