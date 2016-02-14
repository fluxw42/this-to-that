package com.github.fluxw42.thistothat.conf;

import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        assertEquals(5000, config.getFileSystemQuietTime());
        assertNotNull(config.getWatchedExtensions());
        assertTrue(config.getWatchedExtensions().isEmpty());
    }

    @Test
    public void testDefaultConfigNonExistingFile() throws Exception {
        final ConfigurationImpl config = new ConfigurationImpl(new File("non-existing-file"));
        assertEquals(new File("in"), config.getInputDirectory());
        assertEquals(new File("out"), config.getOutputDirectory());
        assertEquals(5000, config.getFileSystemQuietTime());
        assertNotNull(config.getWatchedExtensions());
        assertTrue(config.getWatchedExtensions().isEmpty());
    }

    @Test
    public void testGetWatchedExtensions() throws Exception {
        final Properties properties = new Properties();
        properties.setProperty("fs.watched.extensions", "wma,ogg,wav");

        final ConfigurationImpl config = new ConfigurationImpl(properties);
        final List<String> watchedExtensions = config.getWatchedExtensions();
        assertNotNull(watchedExtensions);
        assertEquals(3, watchedExtensions.size());
        assertTrue(watchedExtensions.contains("wma"));
        assertTrue(watchedExtensions.contains("ogg"));
        assertTrue(watchedExtensions.contains("wav"));
    }

    @Test
    public void testIsWatched() throws Exception {
        final Properties properties = new Properties();
        properties.setProperty("fs.watched.extensions", "wma,ogg,wav");

        final ConfigurationImpl config = new ConfigurationImpl(properties);
        final List<String> watchedExtensions = config.getWatchedExtensions();
        assertNotNull(watchedExtensions);
        assertEquals(3, watchedExtensions.size());

        assertFalse(config.isWatched(new File("test.txt")));
        assertFalse(config.isWatched(new File("tes.twma")));
        assertFalse(config.isWatched(new File("wma")));
        assertFalse(config.isWatched(new File("testwma")));
        assertFalse(config.isWatched(new File("test.wmat")));

        assertTrue(config.isWatched(new File("test.wma")));
        assertTrue(config.isWatched(new File("test.WMA")));
        assertTrue(config.isWatched(new File("test.ogg")));
        assertTrue(config.isWatched(new File("test.oGg")));
        assertTrue(config.isWatched(new File("test.wav")));
    }

}