package eu.funinnumbers.util.eventconsumer;

import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.util.Observable;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Receives Events From Engine and update observers.
 */
public final class EventConsumer extends Observable {

    /**
     * The Blocking Queue events received from Engine.
     */
    private final BlockingQueue<Event> eventBuffer;

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static EventConsumer ourInstance = null;

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private EventConsumer() {

        // Initialize the Queue
        eventBuffer = new LinkedBlockingQueue<Event>();

        // Initialize Distributer
        (new Distributer(eventBuffer)).start();

    }

    /**
     * EventConsumer is loaded on the first execution of EventConsumer.getInstance()
     * or the first access to EventConsumer.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static EventConsumer getInstance() {
        synchronized (EventConsumer.class) {
            if (ourInstance == null) {
                ourInstance = new EventConsumer();
            }
        }

        return ourInstance;
    }

    /**
     * Insert the event to buffer.
     *
     * @param event The event reveived from Engine
     */
    public void addEvent(final Event event) {
        //Insert the event to the eventBuffer
        eventBuffer.offer(event);
    }
}
