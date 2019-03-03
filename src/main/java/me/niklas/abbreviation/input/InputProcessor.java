package me.niklas.abbreviation.input;

import me.niklas.abbreviation.enums.EventType;
import me.niklas.abbreviation.enums.GamepadInput;
import me.niklas.abbreviation.events.EventManager;
import me.niklas.abbreviation.events.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Niklas on 24.02.2019 in abbreviation
 */
public class InputProcessor implements EventSubscriber {

    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private final EventManager eventManager;
    private GamepadInput[] current;

    /**
     * Creates a new instance of the InputProcessor class.
     *
     * @param eventManager The {@link EventManager}.
     */
    public InputProcessor(EventManager eventManager) {
        this.eventManager = eventManager;
        current = new GamepadInput[0];
    }

    /**
     * Increases the size of a GameInput array by one.
     *
     * @param array The old array.
     * @return The new array size+1).
     */
    private GamepadInput[] increaseSize(GamepadInput[] array) {
        GamepadInput[] newArray = new GamepadInput[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    /**
     * Handles:
     * - TYPED (Adds the buttons (up to 10), runs the combination, opens edit dialog, reloads)
     * {@inheritDoc}
     */
    @Override
    public void onEvent(EventType type, Object param) {
        if (type == EventType.TYPED) {
            if (param instanceof GamepadInput) {
                GamepadInput input = (GamepadInput) param;
                if (input == GamepadInput.NONE) {
                    logger.error("Input is NONE at TYPED");
                    return;
                }
                if (input == GamepadInput.START) {
                    eventManager.fire(EventType.RUN_COMBINATION, current);
                    current = new GamepadInput[0];
                } else if (input == GamepadInput.BACK_SELECT && current.length > 0) { //Edit file
                    eventManager.fire(EventType.EDIT_COMBINATION, current);
                    current = new GamepadInput[0];
                } else {
                    logger.debug("Input: " + input.name());
                    if (current.length == 10) {
                        logger.debug("Max length reached.");
                        eventManager.fire(EventType.SHOW_PREVIEW, current);
                        return;
                    }

                    current = increaseSize(current);
                    current[current.length > 0 ? current.length - 1 : 0] = GamepadInput.valueOf(input.ordinal());
                    eventManager.fire(EventType.SHOW_PREVIEW, current);
                }
            } else {
                logger.warn("param is not of type GamepadInput in TYPED");
            }
        }
    }
}
