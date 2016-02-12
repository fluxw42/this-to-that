package com.github.fluxw42.thistothat.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Date: 2/9/16 - 2:17 PM
 *
 * @author Jeroen Meulemeester
 */
public class DirectoryWatchServiceImpl implements DirectoryWatchService {

    /**
     * The logger for class DirectoryWatchServiceImpl
     */
    private static final Logger logger = Logger.getLogger(DirectoryWatchServiceImpl.class.getName());

    /**
     * Indicates if the service is started or not
     */
    private volatile boolean started = false;

    /**
     * The lock used to protected concurrency of watch service
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * The map of listeners, indexed by watched directory
     */
    private final Map<File, List<DirectoryWatchListener>> listeners = new HashMap<>();

    /**
     * The registered watch keys for each directory
     */
    private final Map<File, WatchKey> watchKeys = new HashMap<>();

    /**
     * The watch service, used to watch the required directories
     */
    private WatchService watchService = null;

    /**
     * Scheduler used to poll the keys for changes
     */
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Create a new instance of the {@link DirectoryWatchService}.
     */
    public DirectoryWatchServiceImpl() {
        this.executorService.submit(this::pollEvents);
    }

    /**
     * Poll for file system events
     */
    private void pollEvents() {
        do {
            if (!isStarted()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            try {
                final WatchKey watchKey = DirectoryWatchServiceImpl.this.watchService.take();
                if (watchKey == null) {
                    continue;
                }

                if (!Path.class.isInstance(watchKey.watchable())) {
                    continue;
                }

                try {
                    lock.readLock().lock();
                    final Path parent = (Path) watchKey.watchable();
                    final List<DirectoryWatchListener> registeredListeners = this.listeners.get(parent.toFile());

                    final List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                    for (final WatchEvent<?> watchEvent : watchEvents) {
                        final EventType eventType = EventType.fromKind(watchEvent.kind());
                        if (eventType == null) {
                            continue;
                        }

                        final Object context = watchEvent.context();
                        if (context == null) {
                            if (logger.isLoggable(Level.WARNING)) {
                                logger.log(Level.WARNING, "Expected 'File' as event context but context was 'null' for event type [" + eventType + "]");
                            }
                            continue;
                        }

                        final Class<Path> expectedClass = Path.class;
                        if (!expectedClass.isInstance(context)) {
                            if (logger.isLoggable(Level.WARNING)) {
                                logger.log(Level.WARNING, "Expected [" + expectedClass + "] as event context but was [" + context.getClass() + "] for event type [" + eventType + "]");
                            }
                            continue;
                        }

                        final Path path = parent.resolve((Path) context);
                        final File file = path.toFile();
                        for (final DirectoryWatchListener listener : registeredListeners) {
                            this.executorService.submit((Runnable) () -> listener.updated(file, eventType));
                        }

                    }
                } finally {
                    if (watchKey != null) {
                        watchKey.reset();
                    }
                    lock.readLock().unlock();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } while (!Thread.currentThread().isInterrupted());

        if (logger.isLoggable(Level.WARNING)) {
            logger.log(Level.WARNING, "Event polling thread halted!");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void start() throws IOException {
        this.lock.writeLock().lock();
        try {
            if (isStarted()) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING, "Service already started. Ignoring.");
                }
                return;
            }

            this.watchService = FileSystems.getDefault().newWatchService();
            updateListeners();

            this.started = true;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void stop() {
        this.lock.writeLock().lock();
        try {
            if (!isStarted()) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING, "Service not running. Ignoring.");
                }
                return;
            }

            this.watchKeys.values().forEach(WatchKey::cancel);
            this.watchKeys.clear();

            try {
                this.watchService.close();
            } catch (IOException e) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING, "IOException while closing watch service : " + e.getMessage(), e);
                }
            }

            this.started = false;
        } finally {
            this.watchService = null;
            this.lock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isStarted() {
        this.lock.writeLock().lock();
        try {
            return this.started;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addListener(final File directory, final DirectoryWatchListener listener) throws IllegalArgumentException, IOException {
        verifyDirectoryArg(directory);

        this.lock.writeLock().lock();
        try {
            if (this.listeners.containsKey(directory)) {
                final List<DirectoryWatchListener> directoryWatchListeners = this.listeners.get(directory);
                if (!directoryWatchListeners.contains(listener)) {
                    directoryWatchListeners.add(listener);
                }
            } else {
                final List<DirectoryWatchListener> watchListeners = new ArrayList<>();
                watchListeners.add(listener);
                this.listeners.put(directory, watchListeners);
            }
            updateListeners();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeListener(final File directory, final DirectoryWatchListener listener) throws IllegalArgumentException, IOException {
        verifyDirectoryArg(directory);

        this.lock.writeLock().lock();
        try {
            this.listeners.getOrDefault(directory, Collections.emptyList()).remove(listener);
            updateListeners();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeListener(final DirectoryWatchListener listener) throws IOException {
        this.lock.writeLock().lock();
        try {
            this.listeners.values().forEach(l -> l.remove(listener));
            updateListeners();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Set<File> getWatchedDirectories() {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableSet(this.listeners.keySet());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Go through the map of {@link #listeners} and remove the entries which have 0 listeners left.
     */
    private void updateListeners() throws IOException {
        this.lock.writeLock().lock();
        try {
            final Set<File> watchedDirectories = this.listeners.keySet();
            for (final File watchedDirectory : watchedDirectories) {
                final List<DirectoryWatchListener> dirListeners = this.listeners.get(watchedDirectory);
                if (dirListeners != null && dirListeners.isEmpty()) {
                    this.listeners.remove(watchedDirectory);
                }
            }

            for (final File directory : this.listeners.keySet()) {
                if (!this.watchKeys.containsKey(directory)) {
                    final WatchKey watchKey = directory.toPath().register(
                            this.watchService,
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.ENTRY_MODIFY
                    );
                    this.watchKeys.put(directory, watchKey);
                }
            }

            for (final File directory : watchKeys.keySet()) {
                if (!this.listeners.containsKey(directory)) {
                    final WatchKey watchKey = this.watchKeys.remove(directory);
                    if (watchKey != null) {
                        watchKey.cancel();
                    }
                }
            }

        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Checks if the specified directory is not null, is an actual directory and is readable.
     *
     * @param directory the directory that has to be verified
     * @throws IllegalArgumentException if {@code directory} is null, not a directory or not
     *                                  readable
     */
    private static void verifyDirectoryArg(final File directory) throws IllegalArgumentException {
        if (directory == null) {
            throw new IllegalArgumentException("Expected a directory but received [null]");
        }

        if (!directory.exists()) {
            throw new IllegalArgumentException("Directory [" + directory + "] doesn't exist.");
        }

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Expected a directory  but [" + directory + "] is not one.");
        }

        if (!directory.canRead()) {
            throw new IllegalArgumentException("Can't read contents of directory  [" + directory + "] due to directory permissions.");
        }
    }

}
