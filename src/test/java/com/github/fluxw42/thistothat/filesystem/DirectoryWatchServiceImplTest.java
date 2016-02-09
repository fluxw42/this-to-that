package com.github.fluxw42.thistothat.filesystem;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Date: 2/9/16 - 10:29 PM
 *
 * @author Jeroen Meulemeester
 */
public class DirectoryWatchServiceImplTest {

    @Test
    public void testStart() throws Exception {
        final DirectoryWatchServiceImpl service = new DirectoryWatchServiceImpl();
        assertFalse(service.isStarted());

        service.start();
        assertTrue(service.isStarted());

        service.start();
        assertTrue(service.isStarted());
    }

    @Test
    public void testStop() throws Exception {
        final DirectoryWatchServiceImpl service = new DirectoryWatchServiceImpl();
        assertFalse(service.isStarted());

        service.start();
        assertTrue(service.isStarted());

        service.stop();
        assertFalse(service.isStarted());

        service.stop();
        assertFalse(service.isStarted());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullDirectory() throws Exception {
        final DirectoryWatchServiceImpl service = new DirectoryWatchServiceImpl();
        service.addListener(null, mock(DirectoryWatchListener.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNonExistingDirectory() throws Exception {
        final DirectoryWatchServiceImpl service = new DirectoryWatchServiceImpl();
        service.addListener(new File("this-is-not-an-existing-folder"), mock(DirectoryWatchListener.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNonExistingDirectory() throws Exception {
        final DirectoryWatchServiceImpl service = new DirectoryWatchServiceImpl();
        service.removeListener(new File("this-is-not-an-existing-folder"), mock(DirectoryWatchListener.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullDirectory() throws Exception {
        final DirectoryWatchServiceImpl service = new DirectoryWatchServiceImpl();
        service.removeListener(null, mock(DirectoryWatchListener.class));
    }

}