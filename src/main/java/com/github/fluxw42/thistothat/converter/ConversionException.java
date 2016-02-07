package com.github.fluxw42.thistothat.converter;

import java.io.File;
import java.io.Serializable;

/**
 * Date: 2/5/16 - 9:15 PM
 *
 * @author Jeroen Meulemeester
 */
public class ConversionException extends Exception {

    /**
     * The serial version UID needed because {@link Throwable} implements {@link Serializable}
     */
    private static final long serialVersionUID = 1L;

    /**
     * The input file of the failed conversion
     */
    private final File inputFile;

    /**
     * The output file of the failed conversion, which possibly doesn't exist since the conversion
     * failed
     */
    private final File outputFile;

    /**
     * Create a new {@link ConversionException} with the given parameters
     *
     * @param inputFile  The input file of the conversion
     * @param outputFile The output file of the conversion
     * @param message    The exception text message
     * @param cause      The cause of the {@link ConversionException}
     */
    public ConversionException(final File inputFile, final File outputFile, final String message, final Throwable cause) {
        super(message, cause);
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    /**
     * Create a new {@link ConversionException} with the given parameters
     *
     * @param inputFile  The input file of the conversion
     * @param outputFile The output file of the conversion
     * @param message    The exception text message
     */
    public ConversionException(final File inputFile, final File outputFile, final String message) {
        super(message);
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    /**
     * Create a new {@link ConversionException} with the given parameters
     *
     * @param inputFile  The input file of the conversion
     * @param outputFile The output file of the conversion
     */
    public ConversionException(final File inputFile, final File outputFile) {
        this(inputFile, outputFile, "Failed to convert [" + inputFile + "] to [" + outputFile + "].");
    }

    /**
     * Create a new {@link ConversionException} with the given parameters
     *
     * @param inputFile  The input file of the conversion
     * @param outputFile The output file of the conversion
     * @param cause      The cause of the {@link ConversionException}
     */
    public ConversionException(final File inputFile, final File outputFile, final Throwable cause) {
        this(inputFile, outputFile, "Failed to convert [" + inputFile + "] to [" + outputFile + "].", cause);
    }

    /**
     * Get the input file of the failed conversion
     *
     * @return The input file of the conversion
     */
    public final File getInputFile() {
        return this.inputFile;
    }

    /**
     * Get the output file of the failed conversion
     *
     * @return The output file of the conversion
     */
    public final File getOutputFile() {
        return this.outputFile;
    }

}
