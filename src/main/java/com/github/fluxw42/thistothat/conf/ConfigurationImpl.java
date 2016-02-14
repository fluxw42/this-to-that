package com.github.fluxw42.thistothat.conf;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public long getFileSystemQuietTime() {
        return getLongOption("fs.quiet.time", TimeUnit.SECONDS.toMillis(5));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getWatchedExtensions() {
        final String configValue = getStringOption("fs.watched.extensions", "");
        final String[] extensions = configValue.trim().split(",");
        final List<String> watchedExtensions = new ArrayList<>();
        for (final String extension : extensions) {
            if (extension != null) {
                final String cleanedExtension = extension.trim().toLowerCase();
                if (!cleanedExtension.isEmpty()) {
                    watchedExtensions.add(cleanedExtension);
                }
            }
        }
        return Collections.unmodifiableList(watchedExtensions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWatched(final File file) {
        final String fileName = Objects.requireNonNull(file).getName().toLowerCase();
        final List<String> watchedExtensions = getWatchedExtensions();
        for (final String watchedExtension : watchedExtensions) {
            if (fileName.endsWith("." + watchedExtension)) {
                return true;
            }
        }
        return false;
    }
}
