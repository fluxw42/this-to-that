package com.github.fluxw42.thistothat.filesystem;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Date: 2/9/16 - 2:17 PM
 *
 * @author Jeroen Meulemeester
 */
public class DirectoryWatchServiceImpl implements DirectoryWatchService {

    /**
     * The logger for class DirectoryWatchServiceImpl
     */
    private static final Logger logger = Logger.getLogger(DirectoryWatchServiceImpl.class.getName());

    /**
     * Indicates if the service is started or not
     */
    private volatile boolean started = false;

    /**
     * The lock used to protected concurrent start and stop calls
     */
    private final Lock startStopLock = new ReentrantLock();

    /**
     * The map of listeners, indexed by watched directory
     */
    private final Map<File, List<DirectoryWatchListener>> listeners = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public final void start() {
        this.startStopLock.lock();
        try {
            if (isStarted()) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING, "Service already started. Ignoring.");
                }
                return;
            }

            // TODO: Implement me

            this.started = true;
        } finally {
            this.startStopLock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void stop() {
        this.startStopLock.lock();
        try {
            if (!isStarted()) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING, "Service not running. Ignoring.");
                }
                return;
            }

            // TODO: Implement me

            this.started = false;
        } finally {
            this.startStopLock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isStarted() {
        this.startStopLock.lock();
        try {
            return this.started;
        } finally {
            this.startStopLock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addListener(final File directory, final DirectoryWatchListener listener) throws IllegalArgumentException {
        verifyDirectoryArg(directory);
        updateListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeListener(final File directory, final DirectoryWatchListener listener) throws IllegalArgumentException {
        verifyDirectoryArg(directory);
        this.listeners.getOrDefault(directory, Collections.emptyList()).remove(listener);
        updateListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeListener(final DirectoryWatchListener listener) {
        this.listeners.values().forEach(l -> l.remove(listener));
        updateListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Set<File> getWatchedDirectories() {
        return Collections.unmodifiableSet(this.listeners.keySet());
    }

    /**
     * Go through the map of {@link #listeners} and remove the entries which have 0 listeners left.
     */
    private final void updateListeners() {
        final Set<File> watchedDirectories = this.listeners.keySet();
        for (final File watchedDirectory : watchedDirectories) {
            final List<DirectoryWatchListener> dirListeners = this.listeners.get(watchedDirectory);
            if (dirListeners != null && dirListeners.isEmpty()) {
                this.listeners.remove(watchedDirectory);
            }
        }
    }

    /**
     * Checks if the specified directory is not null, is an actual directory and is readable.
     *
     * @param directory the directory that has to be verified
     * @throws IllegalArgumentException if {@code directory} is null, not a directory or not
     *                                  readable
     */
    private static void verifyDirectoryArg(final File directory) throws IllegalArgumentException {
        if (directory == null) {
            throw new IllegalArgumentException("Expected a directory but received [null]");
        }

        if (!directory.exists()) {
            throw new IllegalArgumentException("Directory [" + directory + "] doesn't exist.");
        }

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Expected a directory  but [" + directory + "] is not one.");
        }

        if (!directory.canRead()) {
            throw new IllegalArgumentException("Can't read contents of directory  [" + directory + "] due to directory permissions.");
        }
    }

}
