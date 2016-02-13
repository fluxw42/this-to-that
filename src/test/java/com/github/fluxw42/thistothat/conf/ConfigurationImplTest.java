package com.github.fluxw42.thistothat.conf;

import org.junit.Test;

import java.io.File;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Date: 2/13/16 - 2:08 PM
 *
 * @author Jeroen Meulemeester
 */
public class ConfigurationImplTest {

    @Test
    public void testDefaultConfigEmptyProperties() throws Exception {
        final ConfigurationImpl config = new ConfigurationImpl(new Properties());
        assertEquals(new File("in"), config.getInputDirectory());
        assertEquals(new File("out"), config.getOutputDirectory());
    }

    @Test
    public void testDefaultConfigNonExistingFile() throws Exception {
        final ConfigurationImpl config = new ConfigurationImpl(new File("non-existing-file"));
        assertEquals(new File("in"), config.getInputDirectory());
        assertEquals(new File("out"), config.getOutputDirectory());
    }

}