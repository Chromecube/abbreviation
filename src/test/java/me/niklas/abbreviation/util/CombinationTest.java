package me.niklas.abbreviation.util;

import bsh.EvalError;
import bsh.Interpreter;
import me.niklas.abbreviation.enums.GamepadInput;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * Created by Niklas on 25.02.2019 in abbreviation
 */
public class CombinationTest {

    private Combination combination;

    @Before
    public void setup() {
        combination = new Combination(new GamepadInput[]{GamepadInput.B, GamepadInput.A},
                "combination.setName(\"Test\");", "return \"Test\";");
    }

    /**
     * It is expected that the name has been set to "Test" while executing the init script.
     */
    @Test
    public void getName() {
        Assert.assertNotNull("Combination was not set up", combination);

        Assert.assertEquals("Test", combination.getName());
    }

    /**
     * Checks whether a {@link Combination} can recognize parts of it.
     */
    @Test
    public void isPartOf() {
        Assert.assertNotNull("Combination was not set up", combination);

        Assert.assertTrue("Does not recognize correct isPartOf input", combination.isPartOf(new GamepadInput[]{GamepadInput.B}));
        Assert.assertFalse("Recognizes wrong input as correct", combination.isPartOf(new GamepadInput[]{GamepadInput.BACK_SELECT}));
    }

    /**
     * Checks whether the {@link Combination} can recognize equal {@link GamepadInput}s.
     */
    @Test
    public void hasSameInput() {
        Assert.assertNotNull("Combination was not set up", combination);

        Assert.assertTrue("Does not match correct input", combination.hasSameInput(new GamepadInput[]{GamepadInput.B, GamepadInput.A}));
        Assert.assertFalse("Recognizes wrong input as correct", combination.hasSameInput(new GamepadInput[]{GamepadInput.A, GamepadInput.B}));
        Assert.assertFalse("Recognizes empty input as correct", combination.hasSameInput(new GamepadInput[0]));
    }

    /**
     * Checks whether the run script can be executed correctly. This is also a test for beanshell.
     * <p>
     * It is expected that the run script returns "Test".
     */
    @Test
    public void execute() {
        Assert.assertNotNull("Combination was not set up", combination);

        Interpreter i = new Interpreter();

        String result = "";

        try {
            Object r = i.eval(combination.getRunScript());
            Assert.assertNotNull("Eval did not return anything", r);
            Assert.assertTrue("Returned object is not a string", r instanceof String);
            result = (String) r;
        } catch (EvalError evalError) {
            fail("Evaluation error in fail safe evaluation.");
        }

        Assert.assertEquals("Test", result);
    }
}