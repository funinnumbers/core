package eu.funinnumbers.util;

/**
 * Simple Logging Functionality.
 */
public final class Logger {

    private static Logger ourInstance;

    /**
     * Private constructor for singleton class.
     */
    private Logger() {
        // Do nothing
    }

    /**
     * Access the single instance of the logger.
     *
     * @return the unique instance of the Logger
     */
    public static Logger getInstance() {
        synchronized (Logger.class) {
            if (ourInstance == null) {
                ourInstance = new Logger();
            }
        }

        return ourInstance;
    }

    /**
     * Prints out a debug message.
     *
     * @param msg the message to print
     */
    public void debug(final Object msg) {
        System.out.println(msg);
    }

    /**
     * Prints out the stack trace of the exception.
     *
     * @param exception the exception thrown
     */
    public void debug(final Throwable exception) {
        Logger.getInstance().debug(exception.getMessage());
    }

    /**
     * Prints out a debug message along with the stack trace of the exception.
     *
     * @param msg       the message to print
     * @param exception the exception thrown
     */
    public void debug(final Object msg, final Throwable exception) {
        debug(msg);
        debug(exception);
    }

}
