package me.niklas.abbreviation.events;

import me.niklas.abbreviation.enums.EventType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Niklas on 24.02.2019 in abbreviation
 */
public class EventTest implements EventSubscriber {

    private int calls = 0;

    /**
     * Tests the {@link EventManager} by subscribing to ot and firing two events.
     * Only one should cause a call.
     */
    @Test
    public void testEventManager() {
        EventManager manager = new EventManager();
        manager.subscribe(this);
        manager.fire(EventType.EDIT_COMBINATION, "");

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(1, calls);
        manager.unsubscribe(this);
        manager.fire(EventType.EDIT_COMBINATION, "");

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(1, calls);
    }

    @Override
    public void onEvent(EventType type, Object param) {
        calls++;
    }
}