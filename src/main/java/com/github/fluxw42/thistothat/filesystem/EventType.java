package com.github.fluxw42.thistothat.filesystem;

import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;

/**
 * The different update event types
 *
 * Date: 2/9/16 - 5:31 PM
 *
 * @author Jeroen Meulemeester
 */
public enum EventType {

    /**
     * Indicates that a new file was created
     */
    CREATED(StandardWatchEventKinds.ENTRY_CREATE),

    /**
     * Indicates that an existing file was modified
     */
    MODIFIED(StandardWatchEventKinds.ENTRY_MODIFY),

    /**
     * Indicates that an existing file was deleted
     */
    DELETED(StandardWatchEventKinds.ENTRY_DELETE);

    /**
     * The matching {@link WatchEvent}
     */
    private final Kind<?> kind;

    /**
     * Create a new enum instance with the given matching {@link Kind}
     *
     * @param kind The matching {@link Kind}
     */
    EventType(final Kind<?> kind) {
        this.kind = kind;
    }

    /**
     * Map the given event kind to an event type.
     *
     * @param kind The event type to be mapped
     * @return The matching {@link EventType}, or 'null' when there is no match
     */
    public static EventType fromKind(final Kind<?> kind) {
        for (final EventType eventType : values()) {
            if (eventType.kind == kind) {
                return eventType;
            }
        }
        return null;
    }

}
