package com.github.fluxw42.thistothat.filesystem;

import org.junit.Test;

import java.nio.file.StandardWatchEventKinds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Date: 2/9/16 - 5:31 PM
 *
 * @author Jeroen Meulemeester
 */
public class EventTypeTest {

    @Test
    public void testFromKind() throws Exception {
        assertNull(EventType.fromKind(null));
        assertNull(EventType.fromKind(StandardWatchEventKinds.OVERFLOW));
        assertEquals(EventType.CREATED, EventType.fromKind(StandardWatchEventKinds.ENTRY_CREATE));
        assertEquals(EventType.MODIFIED, EventType.fromKind(StandardWatchEventKinds.ENTRY_MODIFY));
        assertEquals(EventType.DELETED, EventType.fromKind(StandardWatchEventKinds.ENTRY_DELETE));
    }

}