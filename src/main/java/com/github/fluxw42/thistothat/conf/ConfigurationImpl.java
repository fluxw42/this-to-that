package com.github.fluxw42.thistothat.conf;

import java.io.File;
import java.util.Properties;

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

    /**
     * Create a new this-to-that {@link Configuration} using the given {@link Properties}
     *
     * @param properties The properties used as config, cannot be 'null'
     */
    public ConfigurationImpl(final Properties properties) {
        super(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getInputDirectory() {
        return new File(getStringOption("in.directory", "in"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getOutputDirectory() {
        return new File(getStringOption("out.directory", "out"));
    }

}
