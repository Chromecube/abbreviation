package me.niklas.abbreviation.util;

import me.niklas.abbreviation.enums.OS;

/**
 * Created by Niklas on 02.03.2019 in abbreviation
 */
public class OperatingSystem {

    /**
     * The operating system.
     */
    public static final OS SYSTEM;
    /**
     * Whether this is a Windows machine.
     */
    public static final boolean WINDOWS;

    static {
        String name = System.getProperty("os.name").toUpperCase();

        if (name.startsWith("WIN")) {
            SYSTEM = OS.WINDOWS;
        } else if (name.startsWith("MAC")) {
            SYSTEM = OS.MAC;
        } else if (name.startsWith("LIN")) {
            SYSTEM = OS.LINUX;
        } else {
            SYSTEM = OS.OTHER;
        }

        WINDOWS = SYSTEM == OS.WINDOWS;
    }
}
