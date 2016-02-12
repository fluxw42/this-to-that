package com.github.fluxw42.thistothat.filesystem;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertFalse;
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

    /**
     * The temporary watched folder used during these test
     */
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

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

    @Test
    public void testCreatedEvent() throws Exception {
        final DirectoryWatchServiceImpl service = new DirectoryWatchServiceImpl();
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
        final DirectoryWatchServiceImpl service = new DirectoryWatchServiceImpl();
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
        final DirectoryWatchServiceImpl service = new DirectoryWatchServiceImpl();
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

        verify(listener, timeout(5000)).updated(eq(modifiedFile), eq(EventType.MODIFIED));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testMultipleListeners() throws Exception {
        final DirectoryWatchServiceImpl service = new DirectoryWatchServiceImpl();
        service.start();

        final File directory = this.folder.newFolder("modified-event-test");
        final File modifiedFile = new File(directory, "test-file");
        assertTrue(modifiedFile.createNewFile());

        final DirectoryWatchListener[] listeners = new DirectoryWatchListener[10];
        for (int i = 0; i < listeners.length / 2; i++) {
            listeners[i] = mock(DirectoryWatchListener.class);
            service.addListener(directory, listeners[i]);
            verifyZeroInteractions(listeners[i]);
        }

        try (final OutputStream out = new FileOutputStream(modifiedFile)) {
            out.write(new byte[1]);
            out.flush();
        }

        for (final DirectoryWatchListener listener : listeners) {
            if (listener != null) {
                verify(listener, timeout(5000).times(1)).updated(eq(modifiedFile), eq(EventType.MODIFIED));
                verifyNoMoreInteractions(listener);
            }
        }

        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] == null) {
                listeners[i] = mock(DirectoryWatchListener.class);
                service.addListener(directory, listeners[i]);
                verifyZeroInteractions(listeners[i]);
            }
        }

        try (final OutputStream out = new FileOutputStream(modifiedFile)) {
            out.write(new byte[1]);
            out.flush();
        }

        for (final DirectoryWatchListener listener : listeners) {
            verify(listener, timeout(5000).times(1)).updated(eq(modifiedFile), eq(EventType.MODIFIED));
            verifyNoMoreInteractions(listener);
        }

    }

}