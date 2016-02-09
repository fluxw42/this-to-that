package com.github.fluxw42.thistothat.filesystem;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Date: 2/9/16 - 5:30 PM
 *
 * @author Jeroen Meulemeester
 */
public interface DirectoryWatchService {

    /**
     * Start the service. Calling start when already start has no influence on the service.
     *
     * @see #isStarted()
     * @see #stop()
     */
    void start();

    /**
     * Stop the service. Calling stop when already stopped has no influence on the service.
     *
     * @see #isStarted()
     * @see #stop()
     */
    void stop();

    /**
     * Indicates if the service is started or not. When the service is started, listeners will
     * receive notifications on the directory they registered on. When the service is stopped, no
     * notifications will be send, but listeners can still be added or removed.
     *
     * @return <tt>true</tt> when the service is started, <tt>false</tt> when not
     */
    boolean isStarted();

    /**
     * Register a new listener for the given directory. Listeners can be added independent if the
     * service is started or not. Registered listeners won't be notified of changes in a directory
     * as long as the service isn't started.
     *
     * @param directory The watched directory
     * @param listener  The listener callback
     * @throws IllegalArgumentException When one of the parameters is 'null' or when the given
     *                                  directory is invalid
     * @see #removeListener(File, DirectoryWatchListener)
     * @see #removeListener(DirectoryWatchListener)
     */
    void addListener(final File directory, final DirectoryWatchListener listener) throws IllegalArgumentException;

    /**
     * Remove the given listener as watch from a specific directory. If a listener is registered
     * multiple times on different directories, the others are untouched.
     *
     * @param directory The watched directory
     * @param listener  The listener callback
     * @throws IllegalArgumentException When one of the parameters is 'null' or when the given
     *                                  directory is invalid
     * @see #removeListener(DirectoryWatchListener)
     * @see #addListener(File, DirectoryWatchListener)
     */
    void removeListener(final File directory, DirectoryWatchListener listener) throws IllegalArgumentException;

    /**
     * Remove the given listener as watch from all directories
     *
     * @param listener The listener callback
     * @see #removeListener(File, DirectoryWatchListener)
     * @see #addListener(File, DirectoryWatchListener)
     */
    void removeListener(final DirectoryWatchListener listener);

    /**
     * Get the current set of watched directories
     *
     * @return The unmodifiable set of watched directories
     */
    Set<File> getWatchedDirectories();

}
