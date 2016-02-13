package com.github.fluxw42.thistothat.conf;

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Date: 2/9/16 - 10:01 AM
 *
 * @author Jeroen Meulemeester
 */
public class PropertiesConfigTest {

    @Test
    public void testGetIntOption() throws Exception {
        final Properties properties = new Properties();
        properties.setProperty("testkey", "1");
        properties.setProperty("wrongvalue", "-");

        final PropertiesConfig config = new PropertiesConfig(properties) {

        };

        assertEquals(1, config.getIntOption("testkey", 2));
        assertEquals(3, config.getIntOption("nonexistent", 3));
        assertEquals(4, config.getIntOption("wrongvalue", 4));
    }

    @Test
    public void testGetLongOption() throws Exception {
        final Properties properties = new Properties();
        properties.setProperty("testkey", "1");
        properties.setProperty("wrongvalue", "-");

        final PropertiesConfig config = new PropertiesConfig(properties) {

        };

        assertEquals(1, config.getLongOption("testkey", 2));
        assertEquals(3, config.getLongOption("nonexistent", 3));
        assertEquals(4, config.getLongOption("wrongvalue", 4));
    }

    @Test(expected = NullPointerException.class)
    public void testGetIntOptionNullProperties() throws Exception {
        new PropertiesConfig((Properties) null) {
        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullKey() throws Exception {
        final Properties properties = new Properties();
        final PropertiesConfig propertiesConfig = new PropertiesConfig(properties) {

        };
        propertiesConfig.getIntOption(null, 1);
    }
}