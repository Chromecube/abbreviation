package me.niklas.abbreviation.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Niklas on 24.02.2019 in abbreviation
 * <p>
 * Checks whether the constants are set correctly by gradle during the build.
 */
public class VersionTest {

    @Test
    public void testVersionNumber() {
        Assert.assertNotEquals("DEVELOPMENT", VersionInfo.VERSION);
    }

    @Test
    public void testDateTime() {
        Assert.assertNotEquals("DEBUG_BUILD", VersionInfo.BUILD_DATE);
        Assert.assertNotEquals("DEBUG_BUILD", VersionInfo.BUILD_TIME);
    }

    @Test
    public void testIsDebug() {
        Assert.assertFalse(VersionInfo.IS_DEBUG);
    }
}
