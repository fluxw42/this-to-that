package com.github.fluxw42.thistothat.conf;

import java.io.File;

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

}
