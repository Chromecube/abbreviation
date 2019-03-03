package me.niklas.abbreviation.enums;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Niklas on 24.02.2019 in abbreviation
 */
public class GamepadInputTest {

    /**
     * Tests the functionality of {@link GamepadInput#toGamepadInputs(int[])}
     * by creating  new {@link GamepadInput} array and checking whether it has been set correctly.
     */
    @Test
    public void toGamepadInputs() {
        GamepadInput[] input = GamepadInput.toGamepadInputs(new int[]{0, 1, 2});
        Assert.assertEquals(0, input[0].ordinal());
        Assert.assertEquals(1, input[1].ordinal());
        Assert.assertEquals(2, input[2].ordinal());
    }
}