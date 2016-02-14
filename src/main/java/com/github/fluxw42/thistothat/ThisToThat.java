package com.github.fluxw42.thistothat;

import com.github.fluxw42.thistothat.conf.Configuration;
import com.github.fluxw42.thistothat.converter.ConversionException;
import com.github.fluxw42.thistothat.converter.audio.AudioConverter;
import com.github.fluxw42.thistothat.converter.audio.AudioFormat;
import com.github.fluxw42.thistothat.filesystem.DirectoryWatchService;
import com.github.fluxw42.thistothat.filesystem.DirectoryWatchServiceImpl;
import com.github.fluxw42.thistothat.filesystem.EventType;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Date: 2/12/16 - 10:16 PM
 *
 * @author Jeroen Meulemeester
 */
public class ThisToThat {

    /**
     * The logger for class ThisToThat
     */
    private static final Logger logger = Logger.getLogger(ThisToThat.class.getName());

    /**
     * The configuration
     */
    private final Configuration config;

    /**
     * The task executor, responsible for
     */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * The queue with pending tasks
     */
    private final BlockingQueue<ConversionTask> queue = new DelayQueue<>();

    /**
     * Create a new {@link ThisToThat} instance using the given configuration
     *
     * @param config The configuration for this instance
     */
    public ThisToThat(final Configuration config) throws IllegalArgumentException {
        if (config == null) {
            throw new IllegalArgumentException("ThisToThat needs a config, but config was 'null'!");
        }
        this.config = config;
    }

    /**
     * Start the service
     */
    public final void start() {
        final DirectoryWatchService directoryWatchService = new DirectoryWatchServiceImpl();
        try {
            directoryWatchService.start();
            directoryWatchService.addListener(config.getInputDirectory(), this::eventHandler);
            this.executor.submit(this::taskHandler);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Method used to handle file system events and to put new {@link ConversionTask}s on the queue
     */
    private void eventHandler(final File file, final EventType eventType) {
        if (file == null) {
            return;
        }

        if (eventType != EventType.CREATED && eventType != EventType.MODIFIED) {
            return;
        }

        if (!this.config.isWatched(file)) {
            return;
        }


        final ConversionTask task = new ConversionTask(file, this.config.getFileSystemQuietTime(), TimeUnit.MILLISECONDS);
        final boolean updated = this.queue.remove(task);
        final boolean success = this.queue.offer(task);
        if (!success) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Failed to add task [" + task + "] to queue!");
            }
        } else if (!updated) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Successfully added task [" + task + "] to queue!");
            }
        }
    }

    /**
     * Method used to handle tasks in the {@link ExecutorService}
     */
    private void taskHandler() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                final ConversionTask task = queue.take();
                final File source = task.getFile();
                final File destination = new File(config.getOutputDirectory(), source.getName() + ".mp3");
                if (destination.exists()) {
                    if (logger.isLoggable(Level.WARNING)) {
                        logger.log(Level.WARNING, "Destination [" + destination + "] already exists. Skipping.");
                    }
                    continue;
                }

                try {
                    if (logger.isLoggable(Level.INFO)) {
                        logger.log(Level.INFO, "Starting to convert [" + source + "] to [" + destination + "]");
                    }

                    final AudioConverter converter = new AudioConverter(source, destination, AudioFormat.MP3);
                    final boolean[] success = new boolean[1];
                    converter.convert(p -> {
                        if (logger.isLoggable(Level.INFO)) {
                            logger.log(Level.INFO, source + " -> " + destination + "    " + p);
                        }
                        success[0] = (p == 1000);
                    });

                    if (success[0]) {
                        if (logger.isLoggable(Level.INFO)) {
                            logger.log(Level.INFO, "Successfully converted [" + source + "] to [" + destination + "]");
                        }
                    } else {
                        if (logger.isLoggable(Level.WARNING)) {
                            logger.log(Level.WARNING, "Converting [" + source + "] to [" + destination + "] failed!");
                        }
                    }

                } catch (ConversionException e) {
                    if (logger.isLoggable(Level.WARNING)) {
                        logger.log(Level.WARNING, "Failed to convert [" + source + "] : " + e.getMessage(), e);
                    }
                }

            }
        } catch (InterruptedException e) {
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Conversion task thread stopped.");
            }
        }
    }
}
