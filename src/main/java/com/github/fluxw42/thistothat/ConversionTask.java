package com.github.fluxw42.thistothat;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Date: 2/13/16 - 3:29 PM
 *
 * @author Jeroen Meulemeester
 */
public class ConversionTask implements Delayed {

    /**
     * The input file waiting to be converted
     */
    private final File file;

    /**
     * The reference
     */
    private long reference;

    /**
     * The initial delay in nano seconds
     */
    private final long nanoDelay;

    /**
     * Create a new conversion task. This {@link Delayed} instance is used to buffer multiple file
     * system events so we can execute a task on a stable file.
     *
     * @param file      The input file that should be converted, cannot be 'null'
     * @param delay     The time to wait after the last file system event
     * @param delayUnit The unit of the delay, cannot be 'null'
     */
    public ConversionTask(final File file, final long delay, final TimeUnit delayUnit) {
        this.file = Objects.requireNonNull(file);
        this.nanoDelay = Objects.requireNonNull(delayUnit).toNanos(delay);
        reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getDelay(final TimeUnit unit) {
        final long now = System.nanoTime();
        final long passed = now - reference;
        final long left = this.nanoDelay - passed;
        return unit.convert(left, TimeUnit.NANOSECONDS);
    }

    /**
     * Get the file this task is converting
     *
     * @return The file
     */
    public final File getFile() {
        return file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Delayed o) {
        if (o == null) {
            return 1;
        } else if (o == this) {
            return 0;
        } else {
            final long thisDelay = getDelay(TimeUnit.NANOSECONDS);
            final long otherDelay = o.getDelay(TimeUnit.NANOSECONDS);
            return Long.compare(thisDelay, otherDelay);
        }
    }

    /**
     * Reset the delay back to it's original value
     */
    public final void reset() {
        this.reference = System.nanoTime();
    }

    /**
     * Indicates if the task's delay has expired
     *
     * @return <tt>true</tt> when the delay is expired, <tt>false</tt> when not
     */
    public final boolean isExpired() {
        return getDelay(TimeUnit.NANOSECONDS) < 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConversionTask)) return false;
        ConversionTask that = (ConversionTask) o;
        return Objects.equals(file, that.file);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(file);
    }
}
