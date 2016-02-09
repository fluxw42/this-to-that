package com.github.fluxw42.thistothat.conf;

import java.io.File;

/**
 * Date: 2/8/16 - 7:59 PM
 *
 * @author Jeroen Meulemeester
 */
public class ConfigurationImpl extends PropertiesConfig implements Configuration {

    /**
     * Create a new this-to-that {@link Configuration} using the given config file
     *
     * @param configFile The config file containing the properties
     */
    public ConfigurationImpl(final File configFile) {
        super(configFile);
    }

}
