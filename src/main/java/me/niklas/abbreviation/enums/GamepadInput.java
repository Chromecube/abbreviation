package me.niklas.abbreviation.enums;

/**
 * Created by Niklas on 14.10.2018 in abbreviation
 */
public enum GamepadInput {

    NONE,
    START,
    A,
    X,
    Y,
    B,
    DPAD_DOWN,
    DPAD_LEFT,
    DPAD_UP,
    DPAD_RIGHT,
    BACK_SELECT, //Back or Select Button (depending on your controller)
    LB,               //Left Button 
    RB,               //Right Button 
    LT,               //Left Trigger 
    RT,               //Right Trigger 
    LS_DOWN,     //Left Stick Down
    LS_LEFT,     //Left Stick Left
    LS_UP,         //Left Stick Uo
    LS_RIGHT,   //Left Stick Right
    RS_DOWN,     //Right Stick Down
    RS_LEFT,     //Right Stick Left
    RS_UP,         //Right Stick Up
    RS_RIGHT,   //Right Stick Right
    LS_PRESS,   //Left Stick Press 
    RS_PRESS;   //Right Stick Press 

    /**
     * Converts an {@code array} into another array of GamepadInputs according to the {@code index}.
     *
     * @param array An array of indices.
     * @return An array of GamepadInputs
     */
    public static GamepadInput[] toGamepadInputs(int[] array) {
        GamepadInput[] values = new GamepadInput[array.length];
        for (int i = 0; i < array.length; i++) {
            values[i] = valueOf(array[i]);
        }
        return values;
    }

    /**
     * Creates a String out of an array.
     *
     * @param inputs The input array.
     * @return A readable version, consisting of the indices in the following format: NAME[INDEX] + NAME[INDEX] + ...
     */
    public static String toReadable(GamepadInput[] inputs) {
        if (inputs.length == 0) return "";
        StringBuilder result = new StringBuilder();

        for (GamepadInput input : inputs) {
            result.append(" + ").append(input.name()).append('[').append(input.ordinal()).append(']');
        }

        return result.toString().trim().substring(2).trim();
    }

    /**
     * Converts an {@code index} into a GamepadInput.
     *
     * @param index The index of the input.
     * @return The GamepadInput.
     */
    public static GamepadInput valueOf(int index) {
        if (index < 0 || index > GamepadInput.values().length) return NONE;
        return GamepadInput.values()[index];
    }

}
