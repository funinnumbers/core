package eu.funinnumbers.guardian.communication.dtservice;

import eu.funinnumbers.db.model.StorableEntity;
import eu.funinnumbers.guardian.communication.echoprotocol.EchoProtocolManager;
import eu.funinnumbers.guardian.storage.StorageService;
import eu.funinnumbers.util.Logger;

/**
 * DTS Evend Sender.
 */
public class EventSender extends Thread { //NOPMD

    /**
     * Overriding Thread's run function.
     */
    public final void run() { //NOPMD
        Logger.getInstance().debug("\tDTS Event Sender started");
        while (true) {
            try {
                if (EchoProtocolManager.getInstance().getMyStation().isActive()
                        && (!StorageService.getInstance().listEntities(StorableEntity.ACTIONEVENTENTITY).isEmpty()
                        || !StorageService.getInstance().listEntities(StorableEntity.EVENTENTITY).isEmpty())) {
                    Logger.getInstance().debug("\tDTS Event Sender is emptying buffer");
                    DTSManager.getInstance().emptyEventBuffer();
                } else {
                    synchronized (this) {
                        try {
                            wait();
                            Logger.getInstance().debug("\tDTS Event Sender woke up");
                        } catch (Exception e) {
                            Logger.getInstance().debug("DTS Event Sender could not stand waiting...");
                            Logger.getInstance().debug(e.toString());
                        }
                    }
                }
            } catch (final Exception e) {
                Logger.getInstance().debug(e.getMessage());
            }

        }
    }

    /**
     * Wake Up Evend Sender Thread.
     */
    public synchronized void doNotify() { //NOPMD
        try {
            notify(); //NOPMD
        } catch (Exception e) {
            Logger.getInstance().debug("\tI don't wanna wait");
            Logger.getInstance().debug(e.toString());
        }

    }

}

