package eu.funinnumbers.hyperengine.eventwriter;

import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.hyperengine.HyperStats;
import eu.funinnumbers.util.Observable;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Save All Events to a File.
 */
public class EventManager extends Observable {

    /**
     * The Blocking Queue events received from Engine.
     */
    private final BlockingQueue<Event> eventBuffer;


    /**
     * The Blocking Queue events received from Engine.
     */
    private final BlockingQueue<HyperStats> mapStats;

    /**
     * The Blocking Queue events received from Engine.
     */
    private final BlockingQueue<HyperStats> soundStats;
    /**
     * The unique instance of HyperEngineLogic.
     */
    private static EventManager thisInstance = null;

    public static EventManager getInstance() {
        synchronized (EventManager.class) {
            // Check if an instance has already been created
            if (thisInstance == null) {
                // Create a new instance if not
                thisInstance = new EventManager();
            }
        }
        // Return the EchoProtocolManager instance
        return thisInstance;
    }

    public EventManager() {
        this.eventBuffer = new LinkedBlockingQueue<Event>();
        this.mapStats = new LinkedBlockingQueue<HyperStats>();
        this.soundStats = new LinkedBlockingQueue<HyperStats>();
        (new EventWriter(eventBuffer)).start();
        (new MapNotifier(mapStats)).start();
        (new SoundNotifier(soundStats)).start();
    }

    /**
     * Insert the event to buffer.
     *
     * @param event The event reveived from Engine
     */
    public void writeEventToFile(final Event event) {
        //Insert the event to the eventBuffer
        eventBuffer.offer(event);
    }

    public void updateStats(final HyperStats hyperStats) {
        mapStats.offer(hyperStats);
        soundStats.offer(hyperStats);
    }
}
