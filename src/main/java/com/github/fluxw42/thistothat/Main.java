package com.github.fluxw42.thistothat;

import com.github.fluxw42.thistothat.conf.ConfigurationImpl;

import java.io.File;

/**
 * Date: 2/2/16 - 11:51 PM
 *
 * @author Jeroen Meulemeester
 */
public class Main {

    /**
     * The default config file
     */
    private static final File DEFAULT_CONFIG = new File("conf", "this-to-that.properties");

    public static void main(final String[] args) throws Exception {
        final File configFile = args == null || args.length == 0 || args[0] == null || args[0].trim().isEmpty() ?
                DEFAULT_CONFIG : new File(args[0].trim());

        final ConfigurationImpl config = new ConfigurationImpl(configFile);
        final ThisToThat thisToThat = new ThisToThat(config);
        thisToThat.start();
    }

}
