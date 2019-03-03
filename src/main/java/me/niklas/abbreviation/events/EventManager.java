package me.niklas.abbreviation.events;

import me.niklas.abbreviation.enums.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Niklas on 24.02.2019 in abbreviation
 */
public class EventManager {

    private final Logger logger = LoggerFactory.getLogger("EventManager");
    private final Map<EventSubscriber, List<EventType>> subscribers = new HashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Makes an {@link EventSubscriber} subscribe to <u>all</u> events. Use {@link #subscribeFor(EventSubscriber, EventType...)}
     * in order to subscribe to specific events.
     *
     * @param subscriber A class implementing the {@link EventSubscriber} interface.
     */
    public void subscribe(EventSubscriber subscriber) {
        subscribers.put(subscriber, Arrays.asList(EventType.values()));
        logger.debug(subscriber.getClass().getSimpleName() + " subscribed");
    }

    /**
     * Subscribe for events of a specific {@link EventType}.
     *
     * @param subscriber The {@link EventSubscriber}.
     * @param events     All events of a specific {@link EventType} you want to listen for. Specify no events to only catch the onExit() event.
     */
    public void subscribeFor(EventSubscriber subscriber, EventType... events) {
        subscribers.put(subscriber, Arrays.asList(events));
        logger.debug(subscriber.getClass().getSimpleName() + " subscribed for " + Arrays.toString(events));
    }

    /**
     * Makes an {@link EventSubscriber} unsubscribe from <u>all</u> events. The onExit() routine will neither be executed.
     *
     * @param subscriber A class implementing the {@link EventSubscriber} interface.
     */
    public void unsubscribe(EventSubscriber subscriber) {
        subscribers.remove(subscriber);
        logger.debug(subscriber.getClass().getSimpleName() + " unsubscribed");
    }

    /**
     * Fires an event of a specific {@link EventType}. All subscribers which are listening for this {@link EventType}
     * will be called. Runs {@code asynchronously}.
     *
     * @param type  The {@link EventType} of the event. Specifies what should be done/has been done.
     * @param param The parameter. Can be of any type, casting happens at the {@link EventSubscriber}'s side.
     */
    public void fire(EventType type, Object param) {
        logger.debug("Firing " + type.name());
        executor.submit(() -> subscribers.forEach((subscriber, types) -> {
            if (types.contains(type)) subscriber.onEvent(type, param);
        }));
    }

    /**
     * When called, <u>every</u> subscriber is notified. You can not unsubscribe from this event (implementation is optional,
     * implement {@link EventSubscriber#onExit()} to handle the exit).
     * Does not run asynchronously, events can not be fired anymore. This action is irreversible.
     */
    public void performExit() {
        logger.info("Performing global exit...");
        subscribers.forEach(((subscriber, types) -> subscriber.onExit()));
        executor.shutdownNow();
    }
}
