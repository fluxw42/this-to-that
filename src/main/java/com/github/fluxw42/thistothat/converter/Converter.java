package com.github.fluxw42.thistothat.converter;

import java.io.File;

/**
 * Date: 2/5/16 - 3:51 PM
 *
 * @author Jeroen Meulemeester
 */
public abstract class Converter {

    /**
     * The input file
     */
    private final File inputFile;

    /**
     * The output file
     */
    private final File outputFile;

    /**
     * Create a new converter with the given input and output files
     *
     * @param inputFile  The file that should be converted
     * @param outputFile The result of the conversion
     */
    protected Converter(final File inputFile, final File outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    /**
     * This method allows you to start the conversion process. This method will block until the
     * conversion succeeds, or when an error occurs, resulting in a {@link ConversionException}
     * Progress can be tracked by passing an optional {@link ConversionProgressListener}
     *
     * @param listener The optional listener, allowing the caller to get status and progress updates
     *                 about the process
     * @throws ConversionException When the conversion failed
     */
    public abstract void convert(final ConversionProgressListener listener) throws ConversionException;

    /**
     * Get the input file
     *
     * @return The input file
     */
    protected final File getInputFile() {
        return inputFile;
    }

    /**
     * Get the requested output file
     *
     * @return The output file of this conversion task
     */
    protected final File getOutputFile() {
        return outputFile;
    }


}
