package com.github.fluxw42.thistothat;

import org.junit.Test;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Date: 2/13/16 - 10:59 PM
 *
 * @author Jeroen Meulemeester
 */
public class ConversionTaskTest {

    @Test
    public void testGetDelay() throws Exception {
        final ConversionTask task = new ConversionTask(new File("test-file"), 10, TimeUnit.HOURS);

        Thread.sleep(250);
        assertEquals(9, task.getDelay(TimeUnit.HOURS));
        assertEquals(599, task.getDelay(TimeUnit.MINUTES));
    }

    @Test
    public void testGetFile() throws Exception {
        final File file = new File("test-file");
        final ConversionTask task = new ConversionTask(file, 10, TimeUnit.HOURS);
        assertEquals(file, task.getFile());
    }

    @Test
    public void testCompareTo() throws Exception {
        final File file = new File("test-file");

        final ConversionTask firstTask = new ConversionTask(file, 10, TimeUnit.HOURS);
        final ConversionTask secondTask = new ConversionTask(file, 5, TimeUnit.HOURS);
        assertEquals(1, firstTask.compareTo(null));
        assertEquals(1, firstTask.compareTo(secondTask));
        assertEquals(-1, secondTask.compareTo(firstTask));
        assertEquals(0, secondTask.compareTo(secondTask));
        assertEquals(0, firstTask.compareTo(firstTask));

    }

    @Test
    public void testIsExpired() throws Exception {
        assertFalse(new ConversionTask(new File("test-file"), 10, TimeUnit.HOURS).isExpired());

        final ConversionTask task = new ConversionTask(new File("test-file"), 200, TimeUnit.MILLISECONDS);
        assertFalse(task.isExpired());

        Thread.sleep(500);
        assertTrue(task.isExpired());
    }

    @Test
    public void testEquals() throws Exception {
        final File file1 = new File("test-file-1");
        final File file2 = new File("test-file-2");

        final ConversionTask firstTask = new ConversionTask(file1, 10, TimeUnit.HOURS);
        final ConversionTask secondTask = new ConversionTask(file1, 5, TimeUnit.HOURS);
        final ConversionTask thirdTask = new ConversionTask(file2, 2, TimeUnit.HOURS);
        assertTrue(firstTask.equals(secondTask));
        assertTrue(secondTask.equals(firstTask));
        assertFalse(firstTask.equals(thirdTask));
        assertFalse(secondTask.equals(thirdTask));
    }

    @Test
    public void testHashCode() throws Exception {
        final File file1 = new File("test-file-1");
        final File file2 = new File("test-file-2");

        final ConversionTask firstTask = new ConversionTask(file1, 10, TimeUnit.HOURS);
        final ConversionTask secondTask = new ConversionTask(file1, 5, TimeUnit.HOURS);
        final ConversionTask thirdTask = new ConversionTask(file2, 2, TimeUnit.HOURS);

        assertEquals(firstTask.hashCode(), secondTask.hashCode());
        assertNotEquals(thirdTask.hashCode(), secondTask.hashCode());

    }
}