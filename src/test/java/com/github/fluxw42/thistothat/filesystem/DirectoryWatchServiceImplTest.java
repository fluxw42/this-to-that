package com.github.fluxw42.thistothat.filesystem;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Date: 2/9/16 - 10:29 PM
 *
 * @author Jeroen Meulemeester
 */
public class DirectoryWatchServiceImplTest {

    private DirectoryWatchServiceImpl service = null;

    /**
     * The temporary watched folder used during these test
     */
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        this.service = new DirectoryWatchServiceImpl();
    }

    @After
    public void tearDown() throws Exception {
        this.service.stop();
    }

    @Test
    public void testStart() throws Exception {
        assertFalse(service.isStarted());

        service.start();
        assertTrue(service.isStarted());

        service.start();
        assertTrue(service.isStarted());
    }

    @Test
    public void testStop() throws Exception {
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
        service.addListener(null, mock(DirectoryWatchListener.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNonExistingDirectory() throws Exception {
        service.addListener(new File("this-is-not-an-existing-folder"), mock(DirectoryWatchListener.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNonExistingDirectory() throws Exception {
        service.removeListener(new File("this-is-not-an-existing-folder"), mock(DirectoryWatchListener.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullDirectory() throws Exception {
        service.removeListener(null, mock(DirectoryWatchListener.class));
    }

    @Test
    public void testCreatedEvent() throws Exception {
        service.start();

        final File directory = this.folder.newFolder("create-event-test");
        final DirectoryWatchListener listener = mock(DirectoryWatchListener.class);
        service.addListener(directory, listener);

        verifyZeroInteractions(listener);

        final File createdFile = new File(directory, "test-file");
        assertTrue(createdFile.createNewFile());

        verify(listener, timeout(5000)).updated(eq(createdFile), eq(EventType.CREATED));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testDeletedEvent() throws Exception {
        service.start();

        final File directory = this.folder.newFolder("delete-event-test");
        final File deletedFile = new File(directory, "test-file");
        assertTrue(deletedFile.createNewFile());

        final DirectoryWatchListener listener = mock(DirectoryWatchListener.class);
        service.addListener(directory, listener);

        verifyZeroInteractions(listener);

        assertTrue(deletedFile.delete());

        verify(listener, timeout(5000)).updated(eq(deletedFile), eq(EventType.DELETED));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testModifiedEvent() throws Exception {
        service.start();

        final File directory = this.folder.newFolder("modified-event-test");
        final File modifiedFile = new File(directory, "test-file");
        assertTrue(modifiedFile.createNewFile());

        final DirectoryWatchListener listener = mock(DirectoryWatchListener.class);
        service.addListener(directory, listener);

        verifyZeroInteractions(listener);

        try (final OutputStream out = new FileOutputStream(modifiedFile)) {
            out.write(new byte[1]);
            out.flush();
        }

        verify(listener, timeout(5000).atLeastOnce()).updated(eq(modifiedFile), eq(EventType.MODIFIED));
        verifyNoMoreInteractions(listener);

    }

    @Test(timeout = 10000)
    public void testMultipleListeners() throws Exception {
        service.start();

        final File directory = this.folder.newFolder("multiple-event-test");
        final File modifiedFile = new File(directory, "test-file");
        assertTrue(modifiedFile.createNewFile());

        final DirectoryWatchListener[] listeners = new DirectoryWatchListener[50];
        for (int i = 0; i < listeners.length; i++) {
            listeners[i] = mock(DirectoryWatchListener.class);
            service.addListener(directory, listeners[i]);
        }

        verifyZeroInteractions(listeners);

        final Set<File> watchedDirectories = service.getWatchedDirectories();
        assertNotNull(watchedDirectories);
        assertEquals(1, watchedDirectories.size());

        try (final OutputStream out = new FileOutputStream(modifiedFile)) {
            out.write(new byte[1]);
            out.flush();
        }

        for (final DirectoryWatchListener listener : listeners) {
            verify(listener, timeout(5000).atLeastOnce()).updated(eq(modifiedFile), eq(EventType.MODIFIED));
        }

        verifyNoMoreInteractions(listeners);

    }

}