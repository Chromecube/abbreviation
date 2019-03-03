package me.niklas.abbreviation.enums;

/**
 * Created by Niklas on 24.02.2019 in abbreviation
 */
public enum EventType {

    /**
     * Param: Array of type {@link me.niklas.abbreviation.enums.GamepadInput} (typed keys)
     */
    EDIT_COMBINATION,
    /**
     * Param: Array of type {@link me.niklas.abbreviation.enums.GamepadInput} (typed keys)
     */
    RUN_COMBINATION,
    /**
     * Param: {@link String} (directory path) or {@code null}
     */
    RELOAD,
    /**
     * Param: {@link me.niklas.abbreviation.enums.GamepadInput} (typed key)
     */
    TYPED,
    /**
     * Param: {@link me.niklas.abbreviation.enums.GamepadInput} (currently typed keys)
     */
    SHOW_PREVIEW,
    /**
     * Param: {@link String} (message)
     */
    SHOW_MESSAGE,
}