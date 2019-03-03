package me.niklas.abbreviation.input;

import me.niklas.abbreviation.enums.EventType;
import me.niklas.abbreviation.events.EventManager;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by Niklas on 24.02.2019 in abbreviation
 */
public class CombinationManagerTest {

    /**
     * Tries to perform a reload by firing the RELOAD event.
     * Expected result is that the directory has been updated (because of the event).
     */
    @Test
    public void reload() {
        EventManager manager = new EventManager();
        CombinationManager combo = new CombinationManager(manager);

        manager.subscribe(combo);

        manager.fire(EventType.RELOAD, new File("").getAbsolutePath());

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(combo.getDirectory());
    }
}