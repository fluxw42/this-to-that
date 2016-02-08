package com.github.fluxw42.thistothat.util;

/**
 * Date: 2/8/16 - 3:15 PM
 *
 * @author Jeroen Meulemeester
 */
public enum OSType {

    LINUX,
    WINDOWS;

    /**
     * Get the OS type of the current system
     *
     * @return The OS type
     */
    public static OSType getOSType() {
        final String os = System.getProperty("os.name").toLowerCase();
        return os.contains("windows") ? WINDOWS : LINUX;
    }

}
