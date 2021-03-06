package me.niklas.abbreviation;

import me.niklas.abbreviation.events.EventManager;
import me.niklas.abbreviation.input.CombinationManager;
import me.niklas.abbreviation.input.GamepadInputReceiver;
import me.niklas.abbreviation.input.InputProcessor;
import me.niklas.abbreviation.tray.TrayManager;
import me.niklas.abbreviation.util.VersionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static me.niklas.abbreviation.enums.EventType.*;

public class Main {

    /**
     * @param args The path to the combination directory (optional, by default /combinations)
     */
    private Main(String[] args) {
        Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

        logger.info("This is version " + VersionInfo.VERSION);
        logger.info("Built on " + VersionInfo.BUILD_DATE + " at " + VersionInfo.BUILD_TIME);

        logger.debug("Creating objects");

        EventManager events = new EventManager();
        CombinationManager combinations = new CombinationManager(events);
        InputProcessor processor = new InputProcessor(events);
        GamepadInputReceiver input = new GamepadInputReceiver(events);
        TrayManager tray = new TrayManager(events);

        logger.debug("Registering event subscribers");

        events.subscribeFor(combinations, EDIT_COMBINATION, RUN_COMBINATION, RELOAD, SHOW_PREVIEW);
        events.subscribeFor(processor, TYPED);
        events.subscribeFor(tray, SHOW_MESSAGE);
        events.subscribeFor(input);

        logger.debug("Initiating RELOAD");
        events.fire(RELOAD, String.join(" ", args).trim());
    }

    public static void main(String[] args) {
        new Main(args);
    }
}
