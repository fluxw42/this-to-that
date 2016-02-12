package com.github.fluxw42.thistothat.filesystem;

import java.io.File;

/**
 * Date: 2/9/16 - 5:31 PM
 *
 * @author Jeroen Meulemeester
 */
public interface DirectoryWatchListener {

    /**
     * This method gets called by the {@link DirectoryWatchService} when a file in a watched folder
     * gets created, modified or deleted.
     *
     * @param file The file that got updated
     * @param type The type of change to the file
     */
    void updated(final File file, final EventType type);

}
