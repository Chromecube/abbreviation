package me.niklas.abbreviation.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Niklas on 24.02.2019 in abbreviation
 */
public class UtilsTest {

    private final UtilMethods util = new UtilMethods();

    /**
     * Tests the execute function by opening a new notepad.
     * <p>
     * Windows-only test.
     */
    @Test
    public void execute() {
        if (!OperatingSystem.WINDOWS) {
            System.out.println("Skipping utils test because this is not a windows system.");
            return;
        }

        util.execute("start notepad.exe");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(util.isProcessRunning("notepad.exe"));
    }

    /**
     * Tests whether the {@link UtilMethods} can execute very basic java code.
     */
    @Test
    public void executeJava() {
        Assert.assertTrue((Boolean) util.executeJava("return true;"));
    }
}