package com.github.fluxw42.thistothat.conf;

import java.io.File;
import java.util.List;

/**
 * Date: 2/8/16 - 7:36 PM
 *
 * @author Jeroen Meulemeester
 */
public interface Configuration {

    /**
     * The watched input directory. Default directory is "in"
     *
     * @return The input directory
     */
    File getInputDirectory();

    /**
     * The output directory for converted files. Default directory is "out"
     *
     * @return The output directory
     */
    File getOutputDirectory();

    /**
     * Get the delay between the last file system event and the first possible start of a task using
     * this file. This prevents a file from being used as long as there are still changes being
     * written to it. The default value is <tt>5000</tt> milliseconds
     *
     * @return The delay in milliseconds
     */
    long getFileSystemQuietTime();

    /**
     * Get the list of file extensions that should be converted. If a file in the input folder
     * doesn't match any of these extensions, the file is ignored
     *
     * @return The unmodifiable list of watched extensions, trimmed and lowercase
     */
    List<String> getWatchedExtensions();

    /**
     * Verify if the given file matches one of the watched extensions.
     *
     * @param file The file to be checked
     * @return <tt>true</tt> when the file matches a watched extension, <tt>false</tt> if not
     */
    boolean isWatched(final File file);

}
