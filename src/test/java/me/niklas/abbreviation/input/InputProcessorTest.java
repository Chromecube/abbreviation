package me.niklas.abbreviation.input;

import me.niklas.abbreviation.enums.EventType;
import me.niklas.abbreviation.enums.GamepadInput;
import me.niklas.abbreviation.events.EventManager;
import me.niklas.abbreviation.events.EventSubscriber;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Niklas on 24.02.2019 in abbreviation
 */
public class InputProcessorTest implements EventSubscriber {

    private int called;

    /**
     * Tests the {@link InputProcessor} and it's ability to fire the RUN_COMBINATION event.
     */
    @Test
    public void testInputProcessor() {
        EventManager manager = new EventManager();
        InputProcessor processor = new InputProcessor(manager);

        manager.subscribe(processor);
        manager.subscribe(this);

        manager.fire(EventType.TYPED, GamepadInput.START);
        manager.fire(EventType.TYPED, GamepadInput.START);
        manager.fire(EventType.TYPED, GamepadInput.START);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(called == 3);

    }

    @Override
    public void onEvent(EventType type, Object param) {
        if (type == EventType.RUN_COMBINATION) {
            called++;
        }
    }
}