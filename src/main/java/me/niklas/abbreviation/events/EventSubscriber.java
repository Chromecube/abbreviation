package me.niklas.abbreviation.events;

import me.niklas.abbreviation.enums.EventType;

/**
 * Created by Niklas on 24.02.2019 in abbreviation
 */
public interface EventSubscriber {

    /**
     * Fired by the {@link EventManager}.
     *
     * @param type  The {@link EventType} of the event. For example "edit a combination".
     * @param param A parameter of any type. Use "instanceof" if you want it to be a specific class.
     */
    void onEvent(EventType type, Object param);


    /**
     * Fired by the {@link EventManager}.
     * <p>
     * Implement it to perform a certain task on exit. (e.g. shutting down a thread)
     */
    default void onExit() {
    }
}
