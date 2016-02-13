package com.github.fluxw42.thistothat.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Date: 2/8/16 - 7:37 PM
 *
 * @author Jeroen Meulemeester
 */
public abstract class PropertiesConfig {

    /**
     * The logger for class PropertiesConfig
     */
    private static final Logger logger = Logger.getLogger(PropertiesConfig.class.getName());

    /**
     * The properties instance, containing the full configuration
     */
    private final Properties properties;


    /**
     * Create a new configuration, using the values from the given properties file
     *
     * @param configFile The file containing the configuration properties
     */
    protected PropertiesConfig(final File configFile) {
        final Properties config = readConfig(Objects.requireNonNull(configFile));
        if (config != null) {
            this.properties = config;
        } else {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Failed to read config from [" + configFile + "]. Using default values!");
            }
            this.properties = new Properties();
        }
    }

    /**
     * Create a new configuration instance using the given properties
     *
     * @param properties The properties used for this config. Cannot be 'null'
     */
    protected PropertiesConfig(final Properties properties) {
        this.properties = Objects.requireNonNull(properties);
    }

    /**
     * Load the given file as a properties file. In case of failure log the error and return 'null'
     *
     * @param configFile The properties file. Cannot be 'null'
     * @return The loaded properties, or 'null' when the given file could not be read
     */
    private static Properties readConfig(final File configFile) {
        try (final InputStream in = new FileInputStream(configFile)) {
            final Properties properties = new Properties();
            properties.load(in);
            return properties;
        } catch (IOException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Failed to read config : " + e.getMessage(), e);
            }
            return null;
        }
    }

    /**
     * Get a configuration value as an integer by name, or the default when the given key does not
     * exist or contains an invalid value
     *
     * @param key          The keyName, cannot be 'null' or empty. Any leading and trailing
     *                     whitespace is removed from the keyname before being used
     * @param defaultValue The default integer value in case the key doesn't exist, or when the
     *                     value could not be parsed as an integer
     * @return The integer config value or the default when not found
     * @throws IllegalArgumentException When the key is 'null' or empty
     */
    protected final int getIntOption(final String key, final int defaultValue) throws IllegalArgumentException {
        final String value = this.properties.getProperty(validateKey(key), String.valueOf(defaultValue));
        try {
            return Integer.valueOf(value);
        } catch (final NumberFormatException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Unable to parse config with key [" + key + "] and value [" + value + "] " +
                        "as integer. Using default [" + defaultValue + "]", e);
            }
            return defaultValue;
        }
    }

    /**
     * Get a configuration value as a long by name, or the default when the given key does not exist
     * or contains an invalid value
     *
     * @param key          The keyName, cannot be 'null' or empty. Any leading and trailing
     *                     whitespace is removed from the keyname before being used
     * @param defaultValue The default long value in case the key doesn't exist, or when the value
     *                     could not be parsed as a long
     * @return The long config value or the default when not found
     * @throws IllegalArgumentException When the key is 'null' or empty
     */
    protected final long getLongOption(final String key, final long defaultValue) throws IllegalArgumentException {
        final String value = this.properties.getProperty(validateKey(key), String.valueOf(defaultValue));
        try {
            return Long.valueOf(value);
        } catch (final NumberFormatException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Unable to parse config with key [" + key + "] and value [" + value + "] " +
                        "as long. Using default [" + defaultValue + "]", e);
            }
            return defaultValue;
        }
    }

    /**
     * Get a configuration value as a {@link String} by name, or the default when the given key does
     * not exist.
     *
     * @param key          The keyName, cannot be 'null' or empty. Any leading and trailing
     *                     whitespace is removed from the keyname before being used
     * @param defaultValue The default integer value in case the key doesn't exist, or when the
     *                     value could not be parsed as an integer
     * @return The integer config value or the default when not found
     * @throws IllegalArgumentException When the key is 'null' or empty
     */
    protected final String getStringOption(final String key, final String defaultValue) {
        return this.properties.getProperty(validateKey(key), defaultValue);
    }

    /**
     * Return the given key when the key is not 'null' or empty. Throw
     *
     * @param key The key that has to be validated
     * @return The key when it's valid
     * @throws IllegalArgumentException When the key is invalid (null, empty or whitespace only)
     */
    private static String validateKey(final String key) throws IllegalArgumentException {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid key [" + key + "]");
        }
        return key;
    }

}
