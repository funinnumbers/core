package eu.funinnumbers.engine.util;

import eu.funinnumbers.util.Logger;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Generic RMI Server.
 */
public final class GenericRMIServer {

    /**
     * reference to the RMI registry of the local machine.
     */
    private Registry registry = null;

    /**
     * The port for the RMI hook.
     */
    static final int RMI_PORT = 1099;

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static GenericRMIServer ourInstance = null;

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private GenericRMIServer() {
        // Does nothing
        startRMI();
    }


    /**
     * GenericRMIServer is loaded on the first execution of AvatarManager.getInstance()
     * or the first access to AvatarManager.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static GenericRMIServer getInstance() {
        synchronized (GenericRMIServer.class) {
            if (ourInstance == null) {
                ourInstance = new GenericRMIServer();
            }
        }

        return ourInstance;
    }

    /**
     * Connects to RMI registry and registers this class.
     */
    private void startRMI() {

        try {
            // Try to create new RMI registry
            registry = java.rmi.registry.LocateRegistry.createRegistry(RMI_PORT);
            Logger.getInstance().debug("RMI registry ready.");

        } catch (Exception e) {
            Logger.getInstance().debug("Exception starting RMI registry:", e);
        }

        try {
            // Check if we failed to create a new registry
            if (registry == null) {
                // Connect to local registry
                registry = LocateRegistry.getRegistry();
            }
        } catch (Exception e) {
            Logger.getInstance().debug("Exception connecting to RMI registry:", e);
        }

    }

    /**
     * Register an RMI Interface to the RMI registry.
     *
     * @param ifName the name of the Interface
     * @param ifImpl the Remote object implementing the interface
     */
    public void registerInterface(final String ifName, final java.rmi.Remote ifImpl) {
        try {
            // Bind class name with class
            registry.rebind(ifName, ifImpl);
            Logger.getInstance().debug("RMI bound");

        } catch (Exception e) {
            Logger.getInstance().debug("Exception binding RMI interface:", e);
        }

    }

}
