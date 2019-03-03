package me.niklas.abbreviation.util;

/**
 * Due to the fact that the class is used by scripts, some design rules do not apply here.
 */
@SuppressWarnings({"SameReturnValue", "ConstantConditions", "unused"})
public class VersionInfo {

    /**
     * The version number. In debug mode, the value is "DEVELOPMENT".
     */
    public static final String VERSION;
    /**
     * The build date. For the format, have a look at the build.gradle file.
     */
    public static final String BUILD_DATE;
    /**
     * The build time. For the format, have a look at the build.gradle file.
     */
    public static final String BUILD_TIME;
    /**
     * If the software is running in debug mode, the value will be true.
     */
    public static final boolean IS_DEBUG;
    private static final String VERSION_RAW = "@VERSION@";
    private static final String BUILD_DATE_RAW = "@DATE@";
    private static final String BUILD_TIME_RAW = "@TIME@";

    static {
        //noinspection ConstantConditions
        VERSION = VERSION_RAW.startsWith("@") ? "DEVELOPMENT" : VERSION_RAW;
        BUILD_DATE = BUILD_DATE_RAW.startsWith("@") ? "DEBUG_BUILD" : BUILD_DATE_RAW;
        BUILD_TIME = BUILD_TIME_RAW.startsWith("@") ? "DEBUG_BUILD" : BUILD_TIME_RAW;
        IS_DEBUG = BUILD_DATE_RAW.startsWith("@");
    }

    /**
     * @return {@link #VERSION}
     */
    public String getVersion() {
        return VERSION;
    }

    /**
     * @return {@link #BUILD_DATE}
     */
    public String getBuildDate() {
        return BUILD_DATE;
    }

    /**
     * @return {@link #BUILD_TIME}
     */
    public String getBuildTime() {
        return BUILD_TIME;
    }

    /**
     * @return {@link #IS_DEBUG}
     */
    public boolean isDebug() {
        return IS_DEBUG;
    }
}